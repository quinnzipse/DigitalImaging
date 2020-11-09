package hw3;

import org.ujmp.core.Matrix;
import pixeljelly.scanners.Location;
import pixeljelly.scanners.RasterScanner;

import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class SimilarityMatrix {
    private final int rn;
    private final int gn;
    private final int bn;
    private final int bins;
    private final double[][] matrix;

    public SimilarityMatrix(int rn, int gn, int bn) {
        this.rn = rn;
        this.gn = gn;
        this.bn = bn;
        this.bins = (int) Math.pow(2, rn + gn + bn);
        this.matrix = generateMatrix();
    }

    private double[][] generateMatrix() {
        double[][] a = new double[bins][bins];
        double max = Double.MIN_VALUE;
        Rectangle bounds = new Rectangle(0, 0, bins, bins);

        for (Location pt : new RasterScanner(bounds)) {
            int rgbI = getRGB(pt.col);
            int rgbJ = getRGB(pt.row);

            a[pt.col][pt.row] = calcL2Distance(rgbI, rgbJ);

            if (a[pt.col][pt.row] > max) max = a[pt.col][pt.row];
        }

        // Normalize!
        for (Location pt : new RasterScanner(bounds)) {
            a[pt.col][pt.row] = 1 - (a[pt.col][pt.row] / max);
        }

        return a;
    }

    private int getRGB(int index) {
        int rn = (int) Math.pow(2, this.rn);
        int gn = (int) Math.pow(2, this.gn);
        int bn = (int) Math.pow(2, this.bn);
        int ir = (index / (gn * bn)) * (256 / rn) + 128 / rn;
        int ig = ((index / bn) % gn) * (256 / gn) + 128 / gn;
        int ib = (index % bn) * (256 / bn) + 128 / bn;
        return ir << 16 | ig << 8 | ib;
    }

    private double calcL2Distance(int rgb1, int rgb2) {
        return Math.sqrt(Math.pow((rgb2 >> 16 & 0xff) - (rgb1 >> 16 & 0xff), 2)
                + Math.pow((rgb2 >> 8 & 0xff) - (rgb1 >> 8 & 0xff), 2)
                + Math.pow((rgb2 & 0xff) - (rgb1 & 0xff), 2));
    }

    public Matrix getMatrix() {
        Matrix m = Matrix.Factory.zeros(bins, bins);

        for (int x = 0; x < matrix.length; x++) {
            for (int y = 0; y < matrix[x].length; y++) {
                m.setAsDouble(matrix[x][y], x, y);
            }
        }

        return m;
    }
}
