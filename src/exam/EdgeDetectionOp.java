package exam;

import pixeljelly.ops.BandExtractOp;
import pixeljelly.ops.NullOp;
import pixeljelly.scanners.Location;
import pixeljelly.scanners.RasterScanner;
import pixeljelly.utilities.ReflectivePadder;
import pixeljelly.utilities.SimpleColorModel;

import java.awt.image.BufferedImage;

public class EdgeDetectionOp extends NullOp {

    /**
     * Calculates the edges in your source image.
     *
     * @param src your source image
     * @return
     */
    public double[][] filter(BufferedImage src) {
        double[][] energy = new double[src.getWidth()][src.getHeight()];
        BufferedImage brightness = new BandExtractOp(SimpleColorModel.HSV, 2).filter(src, null);

        // Apply them to the images.
        double[][] imgX = convolveX(brightness);
        double[][] imgY = convolveY(brightness);

        // Return the combination of the two.
        for (Location pt : new RasterScanner(src, false)) {
            double x = imgX[pt.col][pt.row];
            double y = imgY[pt.col][pt.row];

            energy[pt.col][pt.row] = Math.sqrt(x * x + y * y);
        }

        return energy;
    }

    /**
     * Calculates the change in X by doing a simple convolve operation
     *
     * @param bright grey scale image representing the brightness.
     * @return 2D array of doubles representing delta X at any given point in the image.
     */
    private double[][] convolveX(BufferedImage bright) {
        double[][] edges = new double[bright.getWidth()][bright.getHeight()];
        ReflectivePadder padder = ReflectivePadder.getInstance();
        for (Location pt : new RasterScanner(bright, false)) {
            edges[pt.col][pt.row] = (padder.getSample(bright, pt.col - 1, pt.row, 0) -
                    padder.getSample(bright, pt.col + 1, pt.row, 0));
        }

        return edges;
    }

    /**
     * Calculates the change in Y by doing a simple convolve operation
     *
     * @param bright grey scale image representing the brightness.
     * @return 2D array of doubles representing delta Y at any given point in the image.
     */
    private double[][] convolveY(BufferedImage bright) {
        double[][] edges = new double[bright.getWidth()][bright.getHeight()];
        ReflectivePadder padder = ReflectivePadder.getInstance();
        for (Location pt : new RasterScanner(bright, false)) {
            edges[pt.col][pt.row] = (padder.getSample(bright, pt.col, pt.row - 1, 0) -
                    padder.getSample(bright, pt.col, pt.row + 1, 0));
        }

        return edges;
    }
}