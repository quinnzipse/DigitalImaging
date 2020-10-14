package hw2;

import pixeljelly.ops.NullOp;
import pixeljelly.ops.PluggableImageOp;
import pixeljelly.scanners.Location;
import pixeljelly.scanners.RasterScanner;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.WritableRaster;

public class LocalEqualizeOp extends NullOp implements PluggableImageOp {
    private int w, h;
    private boolean isBanded;

    public LocalEqualizeOp() {

    }

    public LocalEqualizeOp(int w, int h, boolean brightnessBandOnly) {
        this.w = w;
        this.h = h;
        this.isBanded = !brightnessBandOnly;
    }

    @Override
    public BufferedImageOp getDefault(BufferedImage bufferedImage) {
        return new LocalEqualizeOp(5, 5, true);
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

        for (Location pt : new RasterScanner(dest, isBanded)) {
            int sum = 0;
            WritableRaster r = src.getRaster();
            for (int y = -h / 2; y < h / 2; y++) {
                for (int x = -w / 2; x < w / 2; x++) {
                    sum += r.getSample(pt.col + x, pt.row + y, pt.band);
                }
            }

            dest.getRaster().setSample(pt.col, pt.row, pt.band, sum);
        }

        return dest;
    }
}
