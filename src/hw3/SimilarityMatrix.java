package hw3;

import org.ujmp.core.Matrix;
import pixeljelly.scanners.Location;
import pixeljelly.scanners.RasterScanner;

import java.awt.*;

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

        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < i + 1; j++) {
                int rgbI = getRGB(i);
                int rgbJ = getRGB(j);

                a[i][j] = calcL2Distance(rgbI, rgbJ);
                a[a.length - 1 - i][a.length - 1 - j] = a[i][j];

                if (a[i][j] > max) max = a[i][j];
            }
        }

        // Normalize!
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < i + 1; j++) {
                a[i][j] = 1 - (a[i][j] / max);
                a[a.length - 1 - i][a.length - 1 - j] = a[i][j];
            }
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
