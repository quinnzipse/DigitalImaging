package hw4;

import pixeljelly.features.Histogram;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class MedianCut {
    private final BufferedImage src;
    private final int bins;
    private Color[] palette;

    public MedianCut(BufferedImage src, int bins) {
        this.src = src;
        this.bins = bins;
        this.palette = calculatePalette();
    }

    public Color[] getPalette() {
        return palette;
    }

    private Color[] calculatePalette() {
        int[] pixels = src.getRaster().getPixels(0, 0, src.getWidth(), src.getHeight(), new int[0]);

        return medianCut(pixels, 0, bins);
    }

    private Color getColorFromPixels(int[] pixels) {
        int r = 0;
        int b = 0;
        int g = 0;

        for (int pixel : pixels) {
            r += (pixel >> 16) & 0xff;
            g += (pixel >> 8) & 0xff;
            b += pixel & 0xff;
        }

        // Get the averages.
        r /= pixels.length;
        g /= pixels.length;
        b /= pixels.length;

        return new Color(r, g, b);
    }

    private Color[] medianCut(int[] pixels, int depth, int maxDepth) {

        if (depth >= maxDepth) {
            return new Color[]{getColorFromPixels(pixels)};
        }

        int band = biggestRange(pixels);

        pixels = sortByBand(pixels, band);

        int[] a = Arrays.copyOfRange(pixels, 0, pixels.length / 2);
        int[] b = Arrays.copyOfRange(pixels, pixels.length / 2 + 1, pixels.length);

        ArrayList<Color> colors = (ArrayList<Color>) Arrays.asList(medianCut(a, depth + 1, maxDepth));
        colors.addAll(Arrays.asList(medianCut(b, depth + 1, maxDepth)));

        return colors.toArray(Color[]::new);
    }

    private int[] sortByBand(int[] pixels, int band) {
        int offset = (25 - 8 * band);

        // Box the array into objects.
        Integer[] vals = Arrays.stream(pixels).boxed().toArray(Integer[]::new);

        // Sort the list.
        Arrays.sort(vals, (Integer pixel, Integer other) -> (pixel >> offset) & 0xff - (other >> offset) & 0xff);

        // Convert back.
        return Arrays.stream(vals).mapToInt(Integer::intValue).toArray();
    }

    private int biggestRangeHist(BufferedImage src) {
        Histogram r = new Histogram(src, 0);
        Histogram g = new Histogram(src, 1);
        Histogram b = new Histogram(src, 2);

        int rRange = r.getMaxValue() - r.getMinValue();
        int gRange = g.getMaxValue() - g.getMinValue();
        int bRange = b.getMaxValue() - b.getMinValue();

        int biggestRange = Math.max(rRange, Math.max(gRange, bRange));

        if (biggestRange == rRange) return 0;
        else if (biggestRange == gRange) return 1;
        else return 2;
    }

    private int biggestRange(int[] rgbVals) {
        int rMin = Integer.MAX_VALUE;
        int rMax = Integer.MIN_VALUE;

        int gMin = Integer.MAX_VALUE;
        int gMax = Integer.MIN_VALUE;

        int bMin = Integer.MAX_VALUE;
        int bMax = Integer.MIN_VALUE;

        for (int rgb : rgbVals) {
            int r = (rgb >> 16) & 0xff, g = (rgb >> 8) & 0xff, b = rgb & 0xff;

            rMin = Math.min(r, rMin);
            rMax = Math.max(r, rMax);
            gMin = Math.min(g, gMin);
            gMax = Math.max(g, gMax);
            bMin = Math.min(b, bMin);
            bMax = Math.max(b, bMax);
        }

        int rRange = rMax - rMin;
        int gRange = gMax - gMin;
        int bRange = bMax - bMin;

        int largestRange = Math.max(rRange, Math.max(gRange, bRange));

        if (rRange == largestRange) {
            return 0;
        } else if (gRange == largestRange) {
            return 1;
        } else {
            return 2;
        }
    }

    public void makeHTML(String fileName) {
        try {
            FileWriter fw = new FileWriter(fileName);

            fw.write("<html><head><title>Color Palette</title></head><body>");

            for (Color c : palette) {
                String hex = String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
                fw.write("<div style='width: 10vw; height: 10vh; background-color: " + hex + ";'></div>");
            }

            fw.write("</body></html>");

        } catch (IOException e) {
            System.err.println("Cannot write file " + fileName);
        }
    }
}
