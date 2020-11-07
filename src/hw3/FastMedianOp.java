package hw3;

import pixeljelly.ops.NullOp;
import pixeljelly.ops.PluggableImageOp;
import pixeljelly.scanners.Location;
import pixeljelly.scanners.RasterScanner;
import pixeljelly.utilities.ImagePadder;
import pixeljelly.utilities.ReflectivePadder;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;

public class FastMedianOp extends NullOp implements PluggableImageOp {

    private int m;
    private int n;
    private int medianThreshold;
    private final ImagePadder padder = ReflectivePadder.getInstance();
    private Rectangle scanBounds;
    private int[] histogram;
    private int median;
    private int oldCDF;

    public FastMedianOp() {

    }

    public FastMedianOp(int n, int m) {
        this.m = m;
        this.n = n;
        this.medianThreshold = (m * n + 1) / 2;
        this.scanBounds = new Rectangle(-m / 2, -n / 2, m, n);
    }

    @Override
    public BufferedImageOp getDefault(BufferedImage bufferedImage) {
        return new FastMedianOp(9, 9);
    }

    @Override
    public String getAuthorName() {
        return "Quinn Zipse";
    }

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dest) {
        if (dest == null) {
            dest = createCompatibleDestImage(src, src.getColorModel());
        }

        for (int b = 0; b < src.getSampleModel().getNumBands(); b++) {
            histogram = new int[(int) Math.pow(2, src.getSampleModel().getSampleSize(b))];

            for (Location pt : new RasterScanner(src, false)) {
                if (pt.col == 0) {
                    // Generate a new histogram!
                    histogram = new int[256];
                    for (Location maskPt : new RasterScanner(scanBounds)) {
                        int sample = padder.getSample(src, pt.col + maskPt.col, pt.row + maskPt.row, b);
                        histogram[sample]++;
                    }

                    // Compute the median and CDF Value.
                    median = 0;
                    oldCDF = histogram[median];
                    while (oldCDF < medianThreshold && median < 255) {
                        median++;
                        oldCDF += histogram[median];
                    }
                } else {
                    // Update the existing histogram!
                    updateHistogram(src, b, pt);

                    // CDF if greater than or equal to the median.
                    // iterate down the histogram.
                    while (median > 0 && oldCDF - histogram[median] >= medianThreshold) {
                        oldCDF -= histogram[median];
                        median--;
                    }

                    // CDF is less than 5
                    // iterate up the histogram.
                    while (median < 255 && oldCDF < medianThreshold) {
                        median++;
                        oldCDF += histogram[median];
                    }
                }

                dest.getRaster().setSample(pt.col, pt.row, b, median);
            }
        }

        return dest;
    }

    private void updateHistogram(BufferedImage src, int b, Location pt) {
        for (int yOff = scanBounds.y; yOff <= -scanBounds.y; yOff++) {
            // Remove the previous column.
            int removed = padder.getSample(src, pt.col + scanBounds.x, pt.row + yOff, b);
            histogram[removed]--;

            // Check to see if the cdf changed.
            if (removed <= median) {
                oldCDF--;
            }

            // Add the next column
            int added = padder.getSample(src, pt.col - scanBounds.x, pt.row + yOff, b);
            histogram[added]++;

            // Check to see if the cdf changed.
            if (added <= median) {
                oldCDF++;
            }
        }
    }

    public int getM() {
        return m;
    }

    public int getN() {
        return n;
    }
}
