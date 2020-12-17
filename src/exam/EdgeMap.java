package exam;

import pixeljelly.scanners.Location;
import pixeljelly.scanners.RasterScanner;

import java.awt.image.BufferedImage;

public class EdgeMap {

    private final BufferedImage img;
    private int[][] energy;
    private int max = 0;

    public EdgeMap(BufferedImage img) {
        this.img = img;
        this.energy = new int[img.getWidth()][img.getHeight()];
        initEnergy();
    }

    public BufferedImage toImg() {
        BufferedImage out = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());

        for (Location pt : new RasterScanner(img, false)) {
            out.getRaster().setSample(pt.col, pt.row, 0, (int) (energy[pt.col][pt.row] / (max / 255.0)));
        }

        return out;
    }

    private void initEnergy() {

        // Initialize the destination img by copying the bottom row.
        for (int x = 0; x < img.getWidth(); x++) {
            energy[x][img.getHeight() - 1] = img.getRaster().getSample(x, img.getHeight() - 1, 0);
        }

        // Iterate through the image from bottom to top.
        for (int y = img.getHeight() - 2; y >= 0; y--) {
            for (int x = 0; x < img.getWidth(); x++) {
                int greyVal = img.getRaster().getSample(x, y, 0);

                energy[x][y] = greyVal + findBestPath(x, y);
            }
        }
    }

    private int findBestPath(int x, int y) {

        int left = getEnergy(x - 1, y + 1);
        int right = getEnergy(x + 1, y + 1);
        int min = getEnergy(x, y + 1);

        max = Math.max(left, max);
        max = Math.max(right, max);
        max = Math.max(min, max);

        if (left < min && left != -1) {
            min = left;
        } else if (right < min && right != -1) {
            min = right;
        }

        return min;
    }

    public BufferedImage deletePath(int[] path, BufferedImage img) {
        BufferedImage dest = new BufferedImage(img.getWidth() - 1, img.getHeight(), img.getType());

        for (Location pt : new RasterScanner(img, false)) {
            if (path[pt.row] == pt.col) continue;

            // If the location is after the removal, shift over one.
            int offset = path[pt.row] < pt.col ? 1 : 0;

            dest.setRGB(pt.col - offset, pt.row, img.getRGB(pt.col, pt.row));
        }

        return dest;
    }

    public int[] findPath(int[] path) {
        if (path == null || path.length != img.getHeight()) {
            path = new int[img.getHeight()];
        }

        path[0] = getStartingPoint();
        int y = 1;

        while (y < img.getHeight() - 1) {
            path[y] = findBestPath(path[y - 1], y);
            y++;
        }

        return path;
    }

//    private int findNext(int x, int y) {
//        int left = getEnergy(x - 1, y + 1);
//        int mid = getEnergy(x, y + 1);
//        int right = getEnergy(x + 1, y + 1);
//
//
//    }

    private int getStartingPoint() {
        int minIndex = 0,
                minValue = energy[0][0],
                val;

        for (int x = 1; x < img.getWidth(); x++) {
            val = energy[x][0];
            if (val < minValue) {
                minIndex = x;
                minValue = val;
            }
        }

        return minIndex;
    }

    private int getEnergy(int x, int y) {
        if (x < img.getWidth() && x >= 0) {
            return energy[x][y];
        }

        return -1;
    }
}
