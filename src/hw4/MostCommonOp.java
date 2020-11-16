package hw4;

import pixeljelly.ops.NullOp;
import pixeljelly.ops.PluggableImageOp;
import pixeljelly.scanners.Location;
import pixeljelly.scanners.RasterScanner;
import pixeljelly.utilities.ImagePadder;
import pixeljelly.utilities.ReflectivePadder;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.util.Arrays;

public class MostCommonOp extends NullOp implements PluggableImageOp {
    private int m;
    private int n;
    private static final ImagePadder padder = ReflectivePadder.getInstance();

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
    public BufferedImage filter(BufferedImage src, BufferedImage dest) {
        if (dest == null) {
            dest = createCompatibleDestImage(src, src.getColorModel());
        }

        int[] histogram = new int[(int)Math.pow(2, src.getSampleModel().getSampleSize(0))];

        for (Location pt : new RasterScanner(src, true)) {
            dest.getRaster().setSample(pt.col, pt.row, pt.band, getMostCommon(src, pt, histogram));
        }

        return dest;
    }

    private int getMostCommon(BufferedImage src, Location pt, int[] histogram) {
        // Generate a new histogram!
        Rectangle mask = new Rectangle(pt.col - n / 2, pt.row - m / 2, n, m);

        Arrays.fill(histogram, 0);

        for (Location m_pt : new RasterScanner(mask)) {
            histogram[padder.getSample(src, m_pt.col, m_pt.row, pt.band)]++;
        }

        int max = Arrays.stream(histogram).max().orElseThrow();

        int index = 0;
        while (histogram[index] != max) {
            index++;
        }

        return index;
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