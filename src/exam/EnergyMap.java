package exam;

import org.ujmp.core.collections.composite.SortedListSet;
import pixeljelly.scanners.Location;
import pixeljelly.scanners.RasterScanner;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

public class EnergyMap {
    private double[][] energy;
    private final double[][] edges;
    private double max = 0;

    public EnergyMap(double[][] edges) {
        this.edges = edges;
        this.energy = new double[edges.length][edges[0].length];
        initEnergy();
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

//    public Integer[] getDistinctPaths(int count) {
//        double[][] energy = energy
//    }


    /**
     * Given an x and y to search at, it will calculate the best next step.
     *
     * @param x initial x
     * @param y initial y
     * @return x coordinate of next step
     */
    public int findBestPath(int x, int y) {

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

    private double getEnergy(int x, int y) {
        if (x < energy.length && x >= 0) {
            return energy[x][y];
        }

        return -1;
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
            return (int) (o.weight - this.weight);
        }
    }

}
