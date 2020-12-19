package exam;

import pixeljelly.ops.NullOp;
import pixeljelly.scanners.Location;
import pixeljelly.scanners.RasterScanner;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;

public class Carver {

    private BufferedImage src;
    private double[][] edges;
    private EnergyMap energy;

    public Carver(BufferedImage srcImg) {
        this.src = srcImg;
        this.edges = new EdgeDetectionOp().filter(srcImg);
        energy = new EnergyMap(edges);
    }

    public BufferedImage getImg() {
        return src;
    }

    public double[][] getEdges() {
        return edges;
    }

    public Carver deletePathX(int[] path) {
        path = findPathX(path);
        BufferedImage dest = new BufferedImage(src.getWidth() - 1, src.getHeight(), src.getType());

        for (Location pt : new RasterScanner(dest, false)) {

            if (path[pt.row] == pt.col) continue;

            // If the location is after the removal, shift over one.
            int offset = path[pt.row] < pt.col ? 1 : 0;

            dest.setRGB(pt.col - offset, pt.row, src.getRGB(pt.col, pt.row));
        }

        return new Carver(dest);
    }

    public Carver deletePathY(int[] path) {
        path = findPathY(path);
        BufferedImage dest = new BufferedImage(src.getWidth(), src.getHeight() - 1, src.getType());

        for (Location pt : new RasterScanner(dest, false)) {

            if (path[pt.col] == pt.row) continue;

            // If the location is after the removal, shift over one.
            int offset = path[pt.col] < pt.row ? 1 : 0;

            dest.setRGB(pt.col, pt.row - offset, src.getRGB(pt.col, pt.row));
        }

        return new Carver(dest);
    }

    public Carver addPathsX(int count) {
        int width = src.getWidth();
        int[] path = new int[src.getHeight()];
        BufferedImage continuing = new NullOp().filter(src, null);
        for (int i = 0; i < count; i++) {
            continuing = addPathX(path, continuing);

            if (i % (width / 3) == 0) {
                System.out.println("Recompute");
                src = continuing;
                this.edges = new EdgeDetectionOp().filter(continuing);
                energy = new EnergyMap(edges);
            }
        }

        return new Carver(continuing);
    }

    public BufferedImage addPathX(int[] path, BufferedImage img) {
        path = findPathX(path);
        BufferedImage dest = new BufferedImage(src.getWidth() + 1, src.getHeight(), src.getType());
        BufferedImage destImg = new BufferedImage(img.getWidth() + 1, img.getHeight(), img.getType());

        for (Location pt : new RasterScanner(src, false)) {

            if (path[pt.row] == pt.col) {
                for (int b = 0; b < src.getRaster().getNumBands(); b++) {
                    dest.getRaster().setSample(pt.col, pt.row, b, 255 - src.getRaster().getSample(pt.col, pt.row, b));
                    destImg.getRaster().setSample(pt.col, pt.row, b, img.getRaster().getSample(pt.col, pt.row, b));
                }
            }

            // If the location is after the removal, shift over one.
            int offset = path[pt.row] <= pt.col ? 1 : 0;

            dest.setRGB(pt.col + offset, pt.row, src.getRGB(pt.col, pt.row));
            destImg.setRGB(pt.col + offset, pt.row, img.getRGB(pt.col, pt.row));
        }

        src = dest;
        this.edges = new EdgeDetectionOp().filter(dest);
        energy = new EnergyMap(edges);

        return destImg;
    }

    /**
     * @param path
     * @return an array of indices where indexed by y.
     */
    public int[] findPathX(int[] path) {
        if (path == null || path.length != src.getHeight()) {
            path = new int[src.getHeight()];
        }

        return findPathX(getStartingPoints()[0], path);
    }

    /**
     * @param path
     * @return an array of indices where indexed by x.
     */
    public int[] findPathY(int[] path) {
        if (path == null || path.length != src.getHeight()) {
            path = new int[src.getHeight()];
        }

        return findPathY(getStartingPoints()[0], path);
    }

    /**
     * Given an x at y=0, find the best path through the image.
     *
     * @param x
     * @param path
     * @return
     */
    public int[] findPathX(int x, int[] path) {
        if (path == null || path.length != src.getHeight()) {
            path = new int[src.getHeight()];
        }

        path[0] = x;

        int y = 1;

        while (y < src.getHeight()) {
            path[y] = energy.findBestPath(path[y - 1], y - 1);
            y++;
        }

        return path;
    }

    private Integer[] getStartingPoints() {

        double minValue = energy.get(0, 0), val;
        ArrayList<Integer> mins = new ArrayList<>();
        mins.add(0);

        for (int x = 1; x < src.getWidth(); x++) {
            val = energy.get(x, 0);
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
}
