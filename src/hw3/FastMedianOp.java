package hw3;

import pixeljelly.ops.NullOp;
import pixeljelly.ops.PluggableImageOp;
import pixeljelly.scanners.Location;
import pixeljelly.scanners.RasterScanner;

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

        float[] histogram = new float[m * n];
        int c = 0;

        // for every row R of the src (top-to-bottom)
        for (int r = 0; r < src.getHeight(); r++) {

            // set H to be the histogram of the NxN region of first sample
            // find the median value MED by examining H
            // write MED to DEST
            dest.getRaster().setSample(r, 0, );
            // for every remaining column C of R (left-to-right)
            for (; c < src.getWidth(); c++) {
                // update H by removing the value in the left most column of the previous region
                // update H by adding the values in the right most column of the current region.
                // find the median value MED by scanning higher or lower depending on the values that have been removed/added
                // write MED to DEST
            }
        }
        return super.filter(src, dest);
    }
}
