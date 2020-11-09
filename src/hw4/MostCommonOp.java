package hw4;

import pixeljelly.ops.NullOp;
import pixeljelly.ops.PluggableImageOp;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;

public class MostCommonOp extends NullOp implements PluggableImageOp {
    private int m;
    private int n;

    public MostCommonOp() {

    }

    public MostCommonOp(int m, int n) {
        this.m = m;
        this.n = n;
    }

    @Override
    public BufferedImageOp getDefault(BufferedImage bufferedImage) {
        return new MostCommonOp(9, 9);
    }

    @Override
    public String getAuthorName() {
        return "Quinn Zipse";
    }

    public int getM() {
        return m;
    }

    public int getN() {
        return n;
    }
}