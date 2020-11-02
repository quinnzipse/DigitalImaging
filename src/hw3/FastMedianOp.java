package hw3;

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

        // Assumes RGB 24bpp Image.
        int[][] histograms = new int[3][256];

        // for every row R of the src (top-to-bottom)
        for (int r = 0; r < src.getHeight(); r++) {

            // set H to be the histogram of the NxN region of first sample
            ImagePadder padder = ReflectivePadder.getInstance();
            for (int y = -n / 2; y < n / 2; y++) {
                for (int x = -m / 2; x < m / 2; x++) {
                    for (int b = 0; b < 3; b++) {
                        histograms[b][padder.getSample(src, y, x, b)]++;
                    }
                }
            }


            // find the median value MED by examining H
            int[] rMED_CDF = getMedian(rH);
            int[] gMED_CDF = getMedian(gH);
            int[] bMED_CDF = getMedian(bH);

            // write MED to DEST
            dest.getRaster().setSample(0, r, 0, rMED_CDF[0]);
            dest.getRaster().setSample(0, r, 1, gMED_CDF[0]);
            dest.getRaster().setSample(0, r, 2, bMED_CDF[0]);

            // for every remaining column C of R (left-to-right)
            for (int c = 0; c < src.getWidth(); c++) {
                // update H by removing the value in the left most column of the previous region
                for (int remove = -n / 2; remove < n / 2; remove++) {
                    int rN = padder.getSample(src, r + remove, c, 0);
                    rH[rN]--;
                    if (rN < rMED_CDF[0]) rMED_CDF[1]--;

                    int gN = padder.getSample(src, r + remove, c, 1);
                    gH[gN]--;
                    if (gN < gMED_CDF[0]) gMED_CDF[1]--;

                    int bN = padder.getSample(src, r + remove, c, 2);
                    bH[bN]--;
                    if (bN < bMED_CDF[0]) bMED_CDF[1]--;
                }

                // update H by adding the values in the right most column of the current region.
                for (int remove = -n / 2; remove < n / 2; remove++) {
                    int rN = padder.getSample(src, r + remove, c, 0);
                    rH[rN]++;
                    if (rN > rMED_CDF[0]) rMED_CDF[1]++;

                    int gN = padder.getSample(src, r + remove, c, 1);
                    gH[gN]++;
                    if (gN > gMED_CDF[0]) gMED_CDF[1]++;

                    int bN = padder.getSample(src, r + remove, c, 2);
                    bH[bN]++;
                    if (bN < bMED_CDF[0]) bMED_CDF[1]++;
                }

                // find the median value MED by scanning higher or lower depending on the values that have been removed/added
                while (rMED_CDF[1] < Math.ceil(m * n / 2.0)) {
                    rMED_CDF[1] += rH[rMED_CDF[0]];
                    rMED_CDF[0]++;
                }
                while (rMED_CDF[1] - rH[rMED_CDF[0]] >= Math.ceil(m * n / 2.0)) {
                    rMED_CDF[1] -= rH[rMED_CDF[0]];
                    rMED_CDF[0]--;
                }
                while (gMED_CDF[1] < Math.ceil(m * n / 2.0)) {
                    gMED_CDF[1] += gH[gMED_CDF[0]];
                    gMED_CDF[0]++;
                }
                while (gMED_CDF[1] - gH[gMED_CDF[0]] >= Math.ceil(m * n / 2.0)) {
                    gMED_CDF[1] -= gH[gMED_CDF[0]];
                    gMED_CDF[0]--;
                }
                while (bMED_CDF[1] < Math.ceil(m * n / 2.0)) {
                    bMED_CDF[1] += bH[bMED_CDF[0]];
                    bMED_CDF[0]++;
                }
                while (bMED_CDF[1] - bH[gMED_CDF[0]] >= Math.ceil(m * n / 2.0)) {
                    bMED_CDF[1] -= bH[gMED_CDF[0]];
                    bMED_CDF[0]--;
                }

                // write MED to DEST
                dest.getRaster().setSample(c, r, 0, rMED_CDF[0]);
                dest.getRaster().setSample(c, r, 1, gMED_CDF[0]);
                dest.getRaster().setSample(c, r, 2, bMED_CDF[0]);
            }
        }
        return super.filter(src, dest);
    }

    private int[] getMedian(int[] histogram) {
        int totalSamples = m * n;
        int countedSamples = 0;
        int i = 0;

        while (countedSamples < totalSamples / 2) {
            countedSamples += histogram[i];
            i++;
        }

        return new int[]{i, countedSamples};
    }
}
