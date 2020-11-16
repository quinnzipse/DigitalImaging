package hw4;

import org.w3c.dom.css.Rect;
import pixeljelly.features.Histogram;
import pixeljelly.gui.InterruptableTask;
import pixeljelly.ops.NullOp;
import pixeljelly.ops.PluggableImageOp;
import pixeljelly.scanners.Location;
import pixeljelly.scanners.RasterScanner;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Queue;

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

    private Color[] medianCut(int size, BufferedImage src) {
        int[][] samples = new int[src.getRaster().getNumBands()][src.getSampleModel().getSampleSize(0)];
        int[] ranges = new int[src.getRaster().getNumBands()];

        for (Location pt : new RasterScanner(src, true)) {
            samples[pt.band][src.getRaster().getSample(pt.col, pt.row, pt.band)]++;
        }

        for (int i = 0; i < samples.length; i++) {
            int[] band = samples[i];
            int min = 0;
            int max = band.length - 1;

            while (band[min] == 0) {
                min++;
            }

            while (band[max] == 0) {
                max--;
            }

            ranges[i] = max - min;
        }

        int maxRangeIndex = 0;
        for (int i = 1; i < ranges.length; i++) {
            if (maxRangeIndex < ranges[i]) {
                maxRangeIndex = i;
            }
        }

        // We know what band has the most range...

        return null;

    }

    private Color[] medianCut1(int size, BufferedImage src) {
        Queue<Rectangle> pq = new PriorityQueue<>();

        Histogram r = new Histogram(src, 0);
        Histogram g = new Histogram(src, 1);
        Histogram b = new Histogram(src, 2);

        int rRange = r.getMaxValue() - r.getMinValue();
        int gRange = g.getMaxValue() - g.getMinValue();
        int bRange = b.getMaxValue() - b.getMinValue();

        int maxRange = Math.max(Math.max(rRange, gRange), bRange);
        int maxBand = 0;

        if (gRange == maxRange) {
            maxBand = 1;
        } else if (bRange == maxRange) {
            maxRange = 2;
        }

        // Max band is now the color we need to split based on.

        pq.add(r, r.get);
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