package hw2;

import pixeljelly.ops.NullOp;
import pixeljelly.ops.PluggableImageOp;
import pixeljelly.scanners.Location;
import pixeljelly.scanners.RasterScanner;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;

public class VerticalFlipOp extends NullOp implements PluggableImageOp {

    public VerticalFlipOp() {

    }

    @Override
    public BufferedImageOp getDefault(BufferedImage bufferedImage) {
        return new VerticalFlipOp();
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

        for (Location pt : new RasterScanner(dest, true)) {
            if (pt.row > src.getHeight() - pt.row) continue;

            int a = src.getRaster().getSample(pt.col, pt.row, pt.band);
            int b = src.getRaster().getSample(pt.col, Math.abs((src.getHeight() - 1) - pt.row), pt.band);

            dest.getRaster().setSample(pt.col, pt.row, pt.band, b);
            dest.getRaster().setSample(pt.col, Math.abs((src.getHeight() - 1) - pt.row), pt.band, a);
        }

        return dest;
    }
}
