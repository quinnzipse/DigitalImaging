package hw2;

import pixeljelly.ops.NullOp;
import pixeljelly.ops.PluggableImageOp;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;

public class HorizontalFlipOp extends NullOp implements PluggableImageOp, BufferedImageOp {
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

        return null;
    }

}
