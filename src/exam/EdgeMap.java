package exam;

import hw2.BandExtractOp;
import pixeljelly.scanners.Location;
import pixeljelly.scanners.RasterScanner;
import pixeljelly.utilities.*;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.stream.Collectors;

public class EdgeMap {

    private final BufferedImage src;
    private final double[][] edges;
    private final double[][] energy;
    private double max = 0;

    public EdgeMap(BufferedImage srcImg) {
        this.src = srcImg;
        this.energy = new double[srcImg.getWidth()][srcImg.getHeight()];
        this.edges = new EdgeDetectionOp().filter(srcImg);
        initEnergy();
    }

    public BufferedImage getImg() {
        return src;
    }

    public double[][] getEdges() {
        return edges;
    }

    public BufferedImage getEnergyImg() {
        BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

        for (Location pt : new RasterScanner(src, false)) {
            out.getRaster().setSample(pt.col, pt.row, 0, (int) (energy[pt.col][pt.row] / (max / 255.0)));
        }

        return out;
    }

    private void initEnergy() {
        // Initialize the destination img by copying the bottom row.
        for (int x = 0; x < edges.length; x++) {
            energy[x][edges[0].length - 1] = edges[x][edges[0].length - 1];
        }

        // Iterate through the image from bottom to top.
        for (int y = edges[0].length - 2; y >= 0; y--) {
            for (int x = 0; x < edges.length; x++) {
                double greyVal = edges[x][y];

                energy[x][y] = greyVal + energy[findBestPath(x, y)][y + 1];
            }
        }
    }

    /**
     * Given an x and y to search at, it will calculate the best next step.
     *
     * @param x initial x
     * @param y initial y
     * @return x coordinate of next step
     */
    private int findBestPath(int x, int y) {

        double left = getEnergy(x - 1, y + 1);
        double right = getEnergy(x + 1, y + 1);
        double min = getEnergy(x, y + 1);
        int minIndex = x;

        max = Math.max(left, max);
        max = Math.max(right, max);
        max = Math.max(min, max);

        if (left < min && left != -1) {
            min = left;
            minIndex = x - 1;
        }
        if (right < min && right != -1) {
            minIndex = x + 1;
        }

        return minIndex;
    }

    public EdgeMap deletePath(int[] path) {
        path = findPath(path);
        BufferedImage dest = new BufferedImage(src.getWidth() - 1, src.getHeight(), src.getType());

        for (Location pt : new RasterScanner(dest, false)) {

            if (path[pt.row] == pt.col) continue;

            // If the location is after the removal, shift over one.
            int offset = path[pt.row] < pt.col ? 1 : 0;

            dest.setRGB(pt.col - offset, pt.row, src.getRGB(pt.col, pt.row));
        }

        return new EdgeMap(dest);
    }

    public EdgeMap addPaths(int count) throws NoninvertibleTransformException {
        int BATCH_SIZE = 5;

        int[] path = new int[src.getHeight()];
        Integer[] topPaths = new Integer[BATCH_SIZE];
        EdgeMap dest = this;

        var inter = new BilinearInterpolant();
        var mapper = new AffineMapper(AffineTransform.getScaleInstance(src.getWidth() + (double) BATCH_SIZE / src.getWidth(), 1));

        for (int i = 0; i < count / BATCH_SIZE; i++) {
            // add individual paths.
            System.arraycopy(getStartingPointsDelete(), 0, topPaths, 0, BATCH_SIZE);
            for (int p = 0; p < topPaths.length; p++) {
                mapper.initializeMapping(dest.src);
                dest = dest.addPath(topPaths[p], path, inter, mapper);
            }
        }

        return dest;
    }

    public EdgeMap addPath(int startingX, int[] path, Interpolant interpolant, AffineMapper mapper) {
        if (path.length != src.getHeight()) {
            path = new int[src.getHeight()];
        }

        path = findPath(startingX, path);

        BufferedImage dest = new BufferedImage(src.getWidth() + 1, src.getHeight(), src.getType());

        Point2D dstPt = new Point2D.Double();
        Point2D srcPt = new Point2D.Double();

        for (Location pt : new RasterScanner(src, false)) {

            dstPt.setLocation(pt.col, pt.row);
            srcPt = mapper.inverseTransform(dstPt, srcPt);

            if (path[pt.row] == pt.col) {
                for (int b = 0; b < dest.getRaster().getNumBands(); b++) {
                    dest.getRaster().setSample(pt.col, pt.row, b,
                            interpolant.interpolate(src, ReflectivePadder.getInstance(), srcPt, b));
                }
            }

            // If the location is after the removal, shift over one.
            int offset = path[pt.row] <= pt.col ? 1 : 0;
            //src.getRGB(pt.col, pt.row)

            dest.setRGB(pt.col + offset, pt.row, src.getRGB(pt.col, pt.row));
        }

        return new EdgeMap(dest);
    }

    /**
     * @param path
     * @return an array of indices where indexed by y.
     */
    public int[] findPath(int[] path) {
        if (path == null || path.length != src.getHeight()) {
            path = new int[src.getHeight()];
        }

        return findPath(getStartingPoints()[0], path);
    }

    /**
     * Given an x at y=0, find the best path through the image.
     *
     * @param x
     * @param path
     * @return
     */
    public int[] findPath(int x, int[] path) {
        if (path == null || path.length != src.getHeight()) {
            path = new int[src.getHeight()];
        }

        path[0] = x;

        int y = 1;

        while (y < src.getHeight()) {
            path[y] = findBestPath(path[y - 1], y - 1);
            y++;
        }

        return path;
    }

    private Integer[] getStartingPoints() {

        double minValue = energy[0][0], val;
        ArrayList<Integer> mins = new ArrayList<>();
        mins.add(0);

        for (int x = 1; x < src.getWidth(); x++) {
            val = energy[x][0];
            if (val < minValue) {
                minValue = val;
                mins = new ArrayList<>();
                mins.add(x);
            } else if (val == minValue) {
                mins.add(x);
            }
        }

        Collections.shuffle(mins);
        return mins.toArray(Integer[]::new);
    }

    private static class Weight implements Comparable<Weight> {
        private final int index;
        private final double weight;

        public Weight(int index, double weight) {
            this.index = index;
            this.weight = weight;
        }

        @Override
        public int compareTo(Weight o) {
            return (int) (this.weight - o.weight);
        }
    }

    private Integer[] getStartingPointsDelete() {

        PriorityQueue<Weight> mins = new PriorityQueue<>();

        for (int x = 1; x < src.getWidth(); x++) {
            mins.add(new Weight(x, energy[x][0]));
        }

        return mins.stream()
                .limit(10)
                .mapToInt(o -> o.index)
                .boxed()
                .collect(Collectors.toList())
                .toArray(Integer[]::new);
    }

    private double getEnergy(int x, int y) {
        if (x < energy.length && x >= 0) {
            return energy[x][y];
        }

        return -1;
    }
}
