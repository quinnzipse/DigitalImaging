package hw2;

import pixeljelly.ops.NullOp;
import pixeljelly.ops.PluggableImageOp;

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
        return null;
    }
}
