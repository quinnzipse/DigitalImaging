package hw4;

import pixeljelly.scanners.Location;
import pixeljelly.scanners.RasterScanner;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class MedianCut {
    private final BufferedImage src;
    private final int bins;
    private final Color[] palette;

    public MedianCut(BufferedImage src, int bins) {
        this.src = src;
        this.bins = bins;
        this.palette = calculatePalette();
    }

    public Color[] getPalette() {
        return palette;
    }

    private Color[] calculatePalette() {
        Integer[] pixels = new Integer[src.getHeight() * src.getWidth()];

        int i = 0;
        for (Location pt : new RasterScanner(src, false)) {
            pixels[i] = src.getRGB(pt.col, pt.row);
            i++;
        }

        int depth = (int) (Math.log(bins) / Math.log(2));

        return medianCut(pixels, 0, depth).toArray(Color[]::new);
    }

    private Color getColorFromPixels(Integer[] pixels) {
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

    private ArrayList<Color> medianCut(Integer[] pixels, int depth, int maxDepth) {

        if (depth >= maxDepth) {
            ArrayList<Color> colors = new ArrayList<>();
            colors.add(getColorFromPixels(pixels));
            return colors;
        }

        int band = biggestRange(pixels);

        pixels = sortByBand(pixels, band);

        Integer[] a = Arrays.copyOfRange(pixels, 0, pixels.length / 2);
        Integer[] b = Arrays.copyOfRange(pixels, pixels.length / 2, pixels.length);

        ArrayList<Color> colors = medianCut(a, depth + 1, maxDepth);
        colors.addAll(medianCut(b, depth + 1, maxDepth));

        return colors;
    }

    private Integer[] sortByBand(Integer[] pixels, int band) {
        int offset = (16 - 8 * band);

        return Arrays.stream(pixels)
                .sorted(Comparator.comparingInt((Integer o) -> (o >> offset) & 0xff))
                .toArray(Integer[]::new);
    }

    private int biggestRange(Integer[] rgbVals) {
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
            fw.write("<div>" + palette.length + "</div>");

            for (Color c : palette) {
                String hex = String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
                fw.write("<div style='display:inline-block;width: 25%; height: 15vh; background-color: " + hex + ";'></div>");
            }

            fw.write("</body></html>");

            fw.flush();
            fw.close();
        } catch (IOException e) {
            System.out.println("Cannot write file " + fileName);
        }
    }
}
