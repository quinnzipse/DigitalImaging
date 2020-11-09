package hw4;

import pixeljelly.features.Histogram;
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

    private Color[] generateOptimalPalette(int size, BufferedImage src) {

        Histogram red = new Histogram(src, 0);
        Histogram green = new Histogram(src, 1);
        Histogram blue = new Histogram(src, 2);

        int redMedian = 0;
        int greenMedian = 0;
        int blueMedian = 0;

        return new Color[0];
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