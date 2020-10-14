package hw2;

import pixeljelly.ops.NullOp;
import pixeljelly.ops.PluggableImageOp;
import pixeljelly.scanners.Location;
import pixeljelly.scanners.RasterScanner;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;

public class LocalEqualizeOp extends NullOp implements PluggableImageOp {

    @Override
    public BufferedImageOp getDefault(BufferedImage bufferedImage) {
        return new LocalEqualizeOp();
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
        }

        return dest;
    }
}
