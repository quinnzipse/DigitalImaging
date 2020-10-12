package hw2;

import pixeljelly.ops.NullOp;
import pixeljelly.ops.PluggableImageOp;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;

public class VerticalFlipOp extends NullOp implements PluggableImageOp, BufferedImageOp {
    @Override
    public BufferedImageOp getDefault(BufferedImage bufferedImage) {
        return null;
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
