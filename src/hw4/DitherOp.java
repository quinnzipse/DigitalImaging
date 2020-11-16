package hw4;

import pixeljelly.features.Histogram;
import pixeljelly.gui.InterruptableTask;
import pixeljelly.ops.NullOp;
import pixeljelly.ops.PluggableImageOp;
import pixeljelly.scanners.Location;
import pixeljelly.scanners.RasterScanner;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.util.ArrayList;
import java.util.Arrays;

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
        int[] max = new int[src.getRaster().getNumBands()];
        int[] min = new int[src.getRaster().getNumBands()];
        int[] range = new int[src.getRaster().getNumBands()];

        for (Location pt : new RasterScanner(src, true)) {
            int sample = src.getRaster().getSample(pt.col, pt.row, pt.band);
            max[pt.band] = Math.max(max[pt.band], sample);
            min[pt.band] = Math.min(min[pt.band], sample);
        }

        int maxOfMax = 0;
        int index = -1;
        for (int i = 0; i < max.length; i++) {
            range[i] = max[i] - min[i];
            if (range[i] > maxOfMax) {
                maxOfMax = range[i];
                index = i;
            }
        }

        double median = maxOfMax / 2.0;

        return null;
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