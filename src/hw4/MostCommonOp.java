package hw4;

import pixeljelly.features.Histogram;
import pixeljelly.ops.NullOp;
import pixeljelly.ops.PluggableImageOp;
import pixeljelly.ops.RankOp;
import pixeljelly.scanners.Location;
import pixeljelly.scanners.RasterScanner;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;

public class MostCommonOp extends NullOp implements PluggableImageOp {
    private int m;
    private int n;

    public MostCommonOp() {

    }

    public MostCommonOp(int m, int n) {
        this.m = m;
        this.n = n;
    }

    @Override
    public BufferedImageOp getDefault(BufferedImage bufferedImage) {
        return new MostCommonOp(9, 9);
    }

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dest) {
        if (dest == null) {
            dest = createCompatibleDestImage(src, src.getColorModel());
        }

        Histogram h = null;

        for (Location pt : new RasterScanner(src, false)) {
            if (pt.col == 0) {
                createHistogram(src, h, pt);
            } else {
                updateHistogram(src, h);
            }
        }

        return dest;
    }

    private void createHistogram(BufferedImage src, Histogram h, Location pt) {

    }

    private void updateHistogram(BufferedImage src, Histogram h) {

    }

    @Override
    public String getAuthorName() {
        return "Quinn Zipse";
    }

    public int getM() {
        return m;
    }

    public int getN() {
        return n;
    }
}