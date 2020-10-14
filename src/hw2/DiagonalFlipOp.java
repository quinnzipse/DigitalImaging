package hw2;

import pixeljelly.ops.NullOp;
import pixeljelly.ops.PluggableImageOp;
import pixeljelly.scanners.Location;
import pixeljelly.scanners.RasterScanner;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;

public class DiagonalFlipOp extends NullOp implements PluggableImageOp {
    @Override
    public BufferedImageOp getDefault(BufferedImage bufferedImage) {
        return new DiagonalFlipOp();
    }

    @Override
    public String getAuthorName() {
        return "Quinn Zipse";
    }

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dest) {
        if(dest == null) {
            dest = createCompatibleDestImage(src, src.getColorModel());
        }

        for(Location pt : new RasterScanner(src, true)){
            int sample = src.getRaster().getSample(pt.col, pt.row, pt.band);
            dest.getRaster().setSample(Math.abs((src.getWidth() - 1) - pt.col), Math.abs(src.getHeight() - 1 - pt.col), pt.band, sample);
        }

        return dest;
    }
}
