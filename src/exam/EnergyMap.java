package exam;

import pixeljelly.scanners.Location;
import pixeljelly.scanners.RasterScanner;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class EnergyMap {
    private final double[][] energy;
    private final double[][] edges;
    private double max = 0;

    public EnergyMap(double[][] edges, boolean x, ArrayList<Rectangle> areas) {
        this.edges = edges;
        this.energy = new double[edges.length][edges[0].length];
        if (x) {
            initEnergyX();
        } else {
            initEnergyY();
        }
    }

    public BufferedImage getEnergyImg() {
        BufferedImage out = new BufferedImage(energy.length, energy[0].length, BufferedImage.TYPE_BYTE_GRAY);

        Rectangle rect = new Rectangle(0, 0, energy.length, energy[0].length);

        for (Location pt : new RasterScanner(rect)) {
            out.getRaster().setSample(pt.col, pt.row, 0,
                    (int) (energy[pt.col][pt.row] / (max / 255.0)));
        }

        return out;
    }

    public double get(int x, int y) throws IndexOutOfBoundsException {
        return energy[x][y];
    }

    /**
     * Given an x and y to search at, it will calculate the best next step.
     *
     * @param x initial x
     * @param y initial y
     * @return x coordinate of next step
     */
    public int findBestPathX(int x, int y) {

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

    /**
     * Given an x and y to search at, it will calculate the best next step.
     *
     * @param x initial x
     * @param y initial y
     * @return x coordinate of next step
     */
    public int findBestPathY(int x, int y) {

        double top = getEnergy(x + 1, y - 1);
        double bottom = getEnergy(x + 1, y + 1);
        double min = getEnergy(x + 1, y);
        int minIndex = y;

        max = Math.max(top, max);
        max = Math.max(bottom, max);
        max = Math.max(min, max);

        if (top < min && top != -1) {
            min = top;
            minIndex = y - 1;
        }
        if (bottom < min && bottom != -1) {
            minIndex = y + 1;
        }

        return minIndex;
    }

    private void initEnergyX() {
        // Initialize the destination img by copying the bottom row.
        for (int x = 0; x < edges.length; x++) {
            energy[x][edges[0].length - 1] = edges[x][edges[0].length - 1];
        }

        // Iterate through the image from bottom to top.
        for (int y = edges[0].length - 2; y >= 0; y--) {
            for (int x = 0; x < edges.length; x++) {
                double greyVal = edges[x][y];

                energy[x][y] = greyVal + energy[findBestPathX(x, y)][y + 1];
            }
        }
    }

    private void initEnergyY() {
        // Initialize the destination img by copying the right row.
        System.arraycopy(edges[edges.length - 1], 0, energy[edges.length - 1], 0, edges[0].length);

        // Iterate through the image from right to left.
        for (int x = edges.length - 2; x >= 0; x--) {
            for (int y = 0; y < edges[0].length; y++) {
                double greyVal = edges[x][y];

                energy[x][y] = greyVal + energy[x + 1][findBestPathY(x, y)];
            }
        }
    }

    private double getEnergy(int x, int y) {
        if (x < energy.length && x >= 0 && y >= 0 && y < energy[0].length) {
            return energy[x][y];
        }

        return -1;
    }

}
