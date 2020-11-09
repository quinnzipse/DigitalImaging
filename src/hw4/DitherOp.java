package hw4;

import pixeljelly.ops.NullOp;
import pixeljelly.ops.PluggableImageOp;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;

public class DitherOp extends NullOp implements PluggableImageOp {

    public enum Type {STUCKI, JARVIS, FLOYD_STEINBURG, SIERRA, SIERRA_2_4A}

    private Type type;
    private Color[] palette;

    public DitherOp() {
    }

    public DitherOp(Type type, int paletteSize) {
        this.palette = new Color[paletteSize]; // Use median cut algorithm.
        this.type = type;
    }

    public DitherOp(Type type, Color[] palette) {
        this.type = type;
        this.palette = palette;
    }

    @Override
    public BufferedImageOp getDefault(BufferedImage bufferedImage) {
        return new DitherOp(Type.JARVIS, 16);
    }

    @Override
    public String getAuthorName() {
        return "Quinn Zipse";
    }
}