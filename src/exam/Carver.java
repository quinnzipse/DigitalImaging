package exam;

import pixeljelly.ops.NullOp;
import pixeljelly.scanners.Location;
import pixeljelly.scanners.RasterScanner;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class Carver {

    private BufferedImage src;
    private double[][] edges;
    private EnergyMap energy;
    private final ArrayList<Rectangle> areas;

    public Carver(BufferedImage srcImg, boolean x, ArrayList<Rectangle> areas) {
        this.src = srcImg;
        this.edges = new EdgeDetectionOp().filter(srcImg);
        this.areas = areas;
        energy = new EnergyMap(edges, x);
    }

    public static BufferedImage erase(BufferedImage img, ArrayList<Rectangle> rectangles) throws IOException {
//        Carver map = new Carver(img, false, rectangles);

        int width = 0, height = 0;
        for (Rectangle rectangle : rectangles) {
            width += rectangle.width;
            height += rectangle.height;
        }

        EnergyMap mapX = new EnergyMap(new EdgeDetectionOp().filter(img), true, true);
        EnergyMap mapY = new EnergyMap(new EdgeDetectionOp().filter(img), false, true);


        mapX.initEnergyX(rectangles.get(0));
        mapY.initEnergyY(rectangles.get(0));

        ImageIO.write(mapX.getEnergyImg(), "png", new File("xEn.png"));
        ImageIO.write(mapY.getEnergyImg(), "png", new File("yEn.png"));

//        int tempWidth = width, tempHeight = height;
//        boolean w = false;
//        int[] xPath = new int[img.getWidth()];
//        int[] yPath = new int[img.getHeight()];

//        System.out.print("Removing: " + width + " columns...");
//        img = Carver.deletePathsX(img, width, rectangles);
//        System.out.print(" " + height + " rows... ");
//        img = Carver.deletePathsY(img, height, rectangles);
//        System.out.println("Done.");

//        ImageIO.write(img, "png", new File("intermediate.png"));

//        System.out.print("Replacing: " + width + " columns... ");
//        img = Carver.addPathsY(img, height);
//        System.out.print(height + " rows... ");
//        img = Carver.addPathsX(img, width);
//        System.out.println(" Done.");

//        while (tempWidth > 0 || tempHeight > 0) {
//            if (w) {
//                for (int i = 0; i < 5 && tempWidth > 0; i++) {
//                    map = map.deletePathX(xPath);
//                    tempWidth--;
//                }
//
//                w = false;
//            } else {
//                for (int i = 0; i < 5 && tempHeight > 0; i++) {
//                    map = map.deletePathY(yPath);
//                    tempHeight--;
//                }
//
//                w = true;
//            }
//        }
//        System.out.println("Replacing...");
//        while (tempWidth < height || tempHeight < width) {
//            if (w) {
//                for (int i = 0; i < 5 && tempWidth < width; i++) {
//                    map = map.addPathsX(1);
//                    tempWidth--;
//                }
//
//                w = false;
//            } else {
//                for (int i = 0; i < 5 && tempHeight < height; i++) {
//                    map = map.addPathsY(1);
//                    tempHeight--;
//                }
//
//                w = true;
//            }
//        }

        System.out.println("Finishing up!");

        return img;
    }

    public static BufferedImage deletePathsY(BufferedImage img, int paths, ArrayList<Rectangle> list) {
        Carver map = new Carver(img, false, list);

        int[] path = new int[img.getWidth()];

        for (int i = 1; i <= paths; i++) {
            map = map.deletePathY(path);
        }

        return map.getImg();
    }

    public static BufferedImage deletePathsX(BufferedImage img, int paths, ArrayList<Rectangle> list) {
        Carver map = new Carver(img, true, list);

        int[] path = new int[img.getHeight()];

        for (int i = 1; i <= paths; i++) {
            map = map.deletePathX(path);
        }

        return map.getImg();
    }

    public static BufferedImage addPathsX(BufferedImage img, int paths) {
        Carver map = new Carver(img, true, null);

        map = map.addPathsX(paths);

        return map.getImg();
    }

    public static BufferedImage addPathsY(BufferedImage img, int paths) {
        Carver map = new Carver(img, false, null);

        map = map.addPathsY(paths);

        return map.getImg();
    }

    public BufferedImage getImg() {
        return src;
    }

    public BufferedImage getEnergyImg() {
        return energy.getEnergyImg();
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

        return new Carver(dest, true, areas);
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

        return new Carver(dest, false, areas);
    }

    public Carver addPathsX(int count) {
        int width = src.getWidth();
        int[] path = new int[src.getHeight()];
        BufferedImage continuing = new NullOp().filter(src, null);

        for (int i = 0; i < count; i++) {
            continuing = addPathX(path, continuing);

            if (i % (width / 3) == 0) {
                src = continuing;
                this.edges = new EdgeDetectionOp().filter(continuing);
                energy = new EnergyMap(edges, true);
            }
        }

        return new Carver(continuing, true, areas);
    }

    public Carver addPathsY(int count) {
        int height = src.getHeight();
        int[] path = new int[src.getWidth()];
        BufferedImage continuing = new NullOp().filter(src, null);

        for (int i = 0; i < count; i++) {
            continuing = addPathY(path, continuing);

            if (i % (height / 5) == 0) {
                src = continuing;
                this.edges = new EdgeDetectionOp().filter(continuing);
                energy = new EnergyMap(edges, false);
            }
        }

        return new Carver(continuing, false, areas);
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
        energy = new EnergyMap(edges, true);

        return destImg;
    }

    public BufferedImage addPathY(int[] path, BufferedImage img) {
        path = findPathY(path);
        BufferedImage dest = new BufferedImage(src.getWidth(), src.getHeight() + 1, src.getType());
        BufferedImage destImg = new BufferedImage(img.getWidth(), img.getHeight() + 1, img.getType());

        for (Location pt : new RasterScanner(src, false)) {

            if (path[pt.col] == pt.row) {
                for (int b = 0; b < src.getRaster().getNumBands(); b++) {
                    dest.getRaster().setSample(pt.col, pt.row, b, 255 - src.getRaster().getSample(pt.col, pt.row, b));
                    destImg.getRaster().setSample(pt.col, pt.row, b, img.getRaster().getSample(pt.col, pt.row, b));
                }
            }

            // If the location is after the removal, shift over one.
            int offset = path[pt.col] <= pt.row ? 1 : 0;

            dest.setRGB(pt.col, pt.row + offset, src.getRGB(pt.col, pt.row));
            destImg.setRGB(pt.col, pt.row + offset, img.getRGB(pt.col, pt.row));
        }

        src = dest;
        this.edges = new EdgeDetectionOp().filter(dest);
        energy = new EnergyMap(edges, false);

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

        return findPathX(getStartingPointsX()[0], path);
    }

    /**
     * @param path
     * @return an array of indices where indexed by x.
     */
    public int[] findPathY(int[] path) {
        if (path == null || path.length != src.getWidth()) {
            path = new int[src.getWidth()];
        }

        return findPathY(getStartingPointsY()[0], path);
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
            path[y] = energy.findBestPathX(path[y - 1], y - 1);
            y++;
        }

        return path;
    }

    /**
     * Given an Y at x=0, find the best path through the image.
     *
     * @param y
     * @param path
     * @return
     */
    public int[] findPathY(int y, int[] path) {
        if (path == null || path.length != src.getWidth()) {
            path = new int[src.getWidth()];
        }

        path[0] = y;

        int x = 1;

        while (x < src.getWidth()) {
            path[x] = energy.findBestPathY(x - 1, path[x - 1]);
            x++;
        }

        return path;
    }

    private Integer[] getStartingPointsX() {

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

    private Integer[] getStartingPointsY() {

        double minValue = energy.get(0, 0), val;
        ArrayList<Integer> mins = new ArrayList<>();
        mins.add(0);

        for (int y = 1; y < src.getHeight(); y++) {
            val = energy.get(0, y);
            if (val < minValue) {
                minValue = val;
                mins = new ArrayList<>();
                mins.add(y);
            } else if (val == minValue) {
                mins.add(y);
            }
        }

        Collections.shuffle(mins);
        return mins.toArray(Integer[]::new);
    }
}