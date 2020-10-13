package hw2;

import pixeljelly.ops.NullOp;
import pixeljelly.ops.PluggableImageOp;
import pixeljelly.scanners.Location;
import pixeljelly.scanners.RasterScanner;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;

public class HorizontalFlipOp extends NullOp implements PluggableImageOp {

    public HorizontalFlipOp(){

    }

    @Override
    public BufferedImageOp getDefault(BufferedImage bufferedImage) {
        return new HorizontalFlipOp();
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
            if (pt.col > src.getWidth() - pt.col) continue;

            int a = src.getRaster().getSample(pt.col, pt.row, pt.band);
            int b = src.getRaster().getSample(Math.abs((src.getWidth() - 1) - pt.col), pt.row, pt.band);

            dest.getRaster().setSample(pt.col, pt.row, pt.band, b);
            dest.getRaster().setSample(Math.abs((src.getWidth() - 1) - pt.col), pt.row, pt.band, a);
        }

        return dest;
    }

}
