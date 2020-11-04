package hw3;

import pixeljelly.features.Histogram;
import pixeljelly.ops.NullOp;
import pixeljelly.ops.PluggableImageOp;
import pixeljelly.utilities.ImagePadder;
import pixeljelly.utilities.ReflectivePadder;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;

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
        final int BANDS = src.getRaster().getNumBands();

        // Assumes RGB 24bpp Image.
        int[][] histograms = new int[BANDS][256];

        // for every row R of the src (top-to-bottom)
        for (int r = 0; r < src.getHeight(); r++) {

            // set H to be the histogram of the NxN region of first sample
            ImagePadder padder = ReflectivePadder.getInstance();
            BufferedImage sub = src.getSubimage(-m / 2, r - n / 2, m, n);
//            for (int y = -n / 2; y < n / 2; y++) {
//                for (int x = -m / 2; x < m / 2; x++) {
//                    for (int b = 0; b < BANDS; b++) {
//                        histograms[b][padder.getSample(src, y, x, b)]++;
//                    }
//                }
//            }

            Histogram hist = new Histogram(sub, 1);

            // find the median value MED by examining H
            int[] medians = new int[BANDS];
            int[][] cdf = new int[BANDS][256];

            // write MED to DEST
            for (int b = 0; b < BANDS; b++) {
                medians[b] = getMedian(histograms[b]);
                System.out.println(medians[b]);
                cdf[b] = getCDF(histograms[b], cdf[b]);
            }

            int sample;

            // for every remaining column C of R (left-to-right)
            for (int c = 0; c < src.getWidth(); c++) {
                // update H by removing the value in the left most column of the previous region
                for (int remove = -n / 2; remove < n / 2; remove++) {
                    for (int b = 0; b < BANDS; b++) {
                        sample = padder.getSample(src, r + remove, c - n / 2, b);
                        histograms[b][sample]--;
                    }
                }

                // update H by adding the values in the right most column of the current region.
                for (int remove = -n / 2; remove < n / 2; remove++) {
                    for (int b = 0; b < BANDS; b++) {
                        sample = padder.getSample(src, r + remove, c + n / 2, b);
                        histograms[b][sample]++;
                    }
                }

                // find the median value MED by scanning higher or lower depending on the values that have been removed/added
                for (int b = 0; b < BANDS; b++) {
                    System.out.println(medians[0] + ", " + medians[1] + ", " + medians[2]);
                    cdf[b] = getCDF(histograms[b], cdf[b]);
                    //error?
                    while (cdf[b][medians[b]] < m * n / 2) {
                        System.out.println(medians[b]);
                        medians[b]++;
                    }
                    while (cdf[b][medians[b] - 1] >= m * n / 2) {
                        System.err.println(medians[b]);
                        medians[b]--;
                    }
                }

                // write MED to DEST
                for (int b = 0; b < BANDS; b++) {
                    dest.getRaster().setSample(r, c, b, medians[b]);
                }
            }
        }
        return dest;
    }

    private int getMedian(int[] histogram) {
        int totalSamples = m * n;
        int countedSamples = 0;
        int i = 0;
        System.out.println(totalSamples);
        while (countedSamples < totalSamples / 2) {
            countedSamples += histogram[i];
            i++;
        }

        return i;
    }

    private int[] getCDF(int[] histogram, int[] cdf) {
        cdf[0] = histogram[0];

        for (int i = 1; i < cdf.length; i++) {
            cdf[i] += cdf[i - 1] + histogram[i];
            i++;
        }

        return cdf;
    }
}
