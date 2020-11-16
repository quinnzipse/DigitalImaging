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
import java.awt.geom.RectangularShape;
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

    private static class Cube implements Comparable<Cube> {
        private int x;
        private int y;
        private int z;
        private int width;
        private int height;
        private int length;

        public Cube(int x, int y, int z, int width, int height, int length) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.width = width;
            this.height = height;
            this.length = length;
        }

        @Override
        public Cube clone() {
            return new Cube(x, y, z, width, height, length);
        }

        public int getLongestLength() {
            return Math.max(width, Math.max(height, length));
        }

        public Cube cut() {
            Cube other = clone();
            int longest = getLongestLength();
            int band = 0;
            if (longest == height) {
                band = 1;
            } else if (longest == length) {
                band = 2;
            }

            // TODO:
        }

        public int compareTo(Cube o) {
            return getLongestLength() - o.getLongestLength();
        }

    }

    private Color[] medianCut1(int size, BufferedImage src) {
        Cube c = getSmallestBox(src);
        PriorityQueue<Cube> pq = new PriorityQueue<>();
        pq.add(c);

        while (pq.size() != size) {
            Cube cube = pq.remove();
            Cube cube2 = cube.cut();

            pq.add(cube);
            pq.add(cube2);
        }

        return cubesToColors(pq.toArray(Cube[]::new));
    }

    private Color[] cubesToColors(Cube[] cubes) {
        for (Cube c : cubes) {
        }
        return new Color[0];
    }

    private Cube getSmallestBox(BufferedImage img) {
        Histogram r = new Histogram(img, 0);
        Histogram g = new Histogram(img, 1);
        Histogram b = new Histogram(img, 2);

        Cube cube = new Cube();

        cube.width = r.getMaxValue() - r.getMinValue();
        cube.x = r.getMinValue();
        cube.height = g.getMaxValue() - g.getMinValue();
        cube.y = g.getMinValue();
        cube.length = b.getMaxValue() - b.getMinValue();
        cube.z = b.getMinValue();

        return cube;
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