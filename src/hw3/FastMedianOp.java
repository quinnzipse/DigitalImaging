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
    private int[][] histograms;
    private int[] medians;
    private int[] oldCDFs;

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

        // initialize everything dependant on the src image!
        medians = new int[src.getSampleModel().getNumBands()];
        oldCDFs = new int[src.getSampleModel().getNumBands()];
        histograms = new int[src.getSampleModel().getNumBands()][(int) Math.pow(2, src.getSampleModel().getSampleSize(0))];

        for (Location pt : new RasterScanner(src, true)) {
            if (pt.col == 0) {
                // create a new histogram.
                createHistogram(src, pt.band, pt);

                findInitMedianCDF(pt.band);
            } else {
                // Update the existing histogram!
                updateHistogram(src, pt.band, pt);

                updateMedianCDF(pt.band);
            }

            dest.getRaster().setSample(pt.col, pt.row, pt.band, medians[pt.band]);
        }

        return dest;
    }

    private void updateMedianCDF(int b) {
        // CDF if greater than or equal to the median.
        // iterate down the histogram.
        while (medians[b] > 0 && oldCDFs[b] - histograms[b][medians[b]] >= medianThreshold) {
            oldCDFs[b] -= histograms[b][medians[b]];
            medians[b]--;
        }

        // CDF is less than 5
        // iterate up the histogram.
        while (medians[b] < 255 && oldCDFs[b] < medianThreshold) {
            medians[b]++;
            oldCDFs[b] += histograms[b][medians[b]];
        }
    }

    private void findInitMedianCDF(int b) {
        // Compute the median and CDF Value.
        medians[b] = 0;
        oldCDFs[b] = histograms[b][medians[b]];
        while (oldCDFs[b] < medianThreshold && medians[b] < 255) {
            medians[b]++;
            oldCDFs[b] += histograms[b][medians[b]];
        }
    }

    private void createHistogram(BufferedImage src, int b, Location pt) {
        // Generate a new histogram!
        histograms[b] = new int[256];
        for (Location maskPt : new RasterScanner(scanBounds)) {
            int sample = padder.getSample(src, pt.col + maskPt.col, pt.row + maskPt.row, b);
            histograms[b][sample]++;
        }
    }

    private void updateHistogram(BufferedImage src, int b, Location pt) {
        for (int yOff = scanBounds.y; yOff <= -scanBounds.y; yOff++) {
            // Remove the previous column.
            int removed = padder.getSample(src, pt.col + scanBounds.x, pt.row + yOff, b);
            histograms[b][removed]--;

            // Check to see if the cdf changed.
            if (removed <= medians[b]) {
                oldCDFs[b]--;
            }

            // Add the next column
            int added = padder.getSample(src, pt.col - scanBounds.x, pt.row + yOff, b);
            histograms[b][added]++;

            // Check to see if the cdf changed.
            if (added <= medians[b]) {
                oldCDFs[b]++;
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
