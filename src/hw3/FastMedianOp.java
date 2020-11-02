package hw3;

import pixeljelly.features.Histogram;
import pixeljelly.ops.NullOp;
import pixeljelly.ops.PluggableImageOp;
import pixeljelly.scanners.Location;
import pixeljelly.scanners.RasterScanner;
import pixeljelly.utilities.ReflectivePadder;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.util.ArrayDeque;
import java.util.Arrays;

public class FastMedianOp extends NullOp implements PluggableImageOp {

    private int n;
    private int m;

    public FastMedianOp() {

    }

    public FastMedianOp(int n, int m) {
        this.m = m;
        this.n = n;
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

        // Assumes RGB 24bpp Image.
        var rH = new int[256];
        var gH = new int[256];
        var bH = new int[256];

        // for every row R of the src (top-to-bottom)
        for (int r = 0; r < src.getHeight(); r++) {

            // set H to be the histogram of the NxN region of first sample
            var padder = ReflectivePadder.getInstance();
            for (int y = -n / 2; y < n / 2; y++) {
                for (int x = -m / 2; x < m / 2; x++) {
                    rH[padder.getSample(src, y, x, 0)]++;
                    gH[padder.getSample(src, y, x, 1)]++;
                    bH[padder.getSample(src, y, x, 2)]++;
                }
            }

            // find the median value MED by examining H
            var rM = getMedian(rH);
            var gM = getMedian(gH);
            var bM = getMedian(bH);

            // write MED to DEST
            dest.getRaster().setSample(r, 0, 0, rM);
            dest.getRaster().setSample(r, 0, 1, gM);
            dest.getRaster().setSample(r, 0, 2, bM);

            // for every remaining column C of R (left-to-right)
            for (int c = 0; c < src.getWidth(); c++) {
                // update H by removing the value in the left most column of the previous region
                for (int remove = -n / 2; remove < n / 2; remove++) {
                    int rN = padder.getSample(src, r + remove, c, 0);
                    rH[rN]--;
                    if(rN < rM) rM++;
                    gH[padder.getSample(src, r + remove, c, 1)]--;
                    bH[padder.getSample(src, r + remove, c, 2)]--;
                }

                // update H by adding the values in the right most column of the current region.
                for (int remove = -n / 2; remove < n / 2; remove++) {
                    rH[padder.getSample(src, r + remove, c + m, 0)]--;
                    gH[padder.getSample(src, r + remove, c + m, 1)]--;
                    bH[padder.getSample(src, r + remove, c + m, 2)]--;
                }

                // find the median value MED by scanning higher or lower depending on the values that have been removed/added
                // write MED to DEST
                dest.getRaster().setSample(r, 0, 0, rM);
                dest.getRaster().setSample(r, 0, 1, gM);
                dest.getRaster().setSample(r, 0, 2, bM);
            }
        }
        return super.filter(src, dest);
    }

    private int getMedian(int[] histogram) {
        var totalSamples = m * n;
        var countedSamples = 0;
        var i = 0;

        while (countedSamples < totalSamples / 2) {
            countedSamples += histogram[i];
            i++;
        }

        return i;
    }
}
