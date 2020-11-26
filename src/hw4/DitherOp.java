package hw4;

import hw1.IndexedDigitalImage;
import pixeljelly.ops.NullOp;
import pixeljelly.ops.PluggableImageOp;
import pixeljelly.scanners.Location;
import pixeljelly.scanners.RasterScanner;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;

import static java.awt.image.BufferedImage.TYPE_BYTE_INDEXED;

public class DitherOp extends NullOp implements PluggableImageOp {

    public enum Type {STUCKI, JARVIS, FLOYD_STEINBURG, SIERRA, SIERRA_2_4A}

    private Type type;
    private Color[] palette;
    private int paletteSize;

    public DitherOp() {
    }

    public DitherOp(Type type, int paletteSize) {
        this.type = type;
        this.paletteSize = paletteSize;
    }

    public DitherOp(Type type, Color[] palette) {
        this.type = type;
        this.palette = palette;
        this.paletteSize = palette.length;
    }

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dest) {
        if (dest == null) {
            dest = new BufferedImage(src.getWidth(), src.getHeight(), TYPE_BYTE_INDEXED);
        }

        if (this.palette == null) {
            MedianCut cutter = new MedianCut(src, paletteSize);
            this.palette = cutter.getPalette();
//            cutter.makeHTML("medianCut.html");
        }

        //Pseudo Code from the Power Point.
        for (Location pt : new RasterScanner(src, false)) {
            // For every pixel....
            // index of palette color closest
            int index = getClosestIndex(src.getRGB(pt.col, pt.row));
            // set the destination to that color.
            dest.getRaster().setSample(pt.col, pt.row, 0, index);
            int[] error = getErrorPerBand(src.getRGB(pt.col, pt.row), dest.getRGB(pt.col, pt.row)); // Calculate the error.
            // Diffuse the error to the nearby pixels on a band-by-band basis.
            switch (type) {
                case JARVIS:
                    diffuseGeneralError(src, error, pt, new int[]{7, 5, 4, 5, 7, 5, 3, 1, 3, 5, 3, 1}, 48);
                    break;
                case SIERRA:
                    diffuseGeneralError(src, error, pt, new int[]{5, 3, 2, 4, 5, 4, 2, 0, 2, 3, 2, 0}, 32);
                    break;
                case STUCKI:
                    diffuseGeneralError(src, error, pt, new int[]{8, 4, 2, 4, 8, 4, 2, 1, 2, 4, 2, 1}, 42);
                    break;
                case SIERRA_2_4A:
                    diffuseSmallError(src, error, pt, false);
                    break;
                case FLOYD_STEINBURG:
                    diffuseSmallError(src, error, pt, true);
                    break;
                default:
                    System.out.println("Error");
            }
        }

        return dest;
    }

    private int getClosestIndex(int color) {
        int minIndex = -1;
        double distance = Integer.MAX_VALUE;

        for (int i = 0; i < palette.length; i++) {
            double d = distanceBetween(palette[i].getRGB(), color);
            if (distance > d) {
                minIndex = i;
                distance = d;
            }
        }

        return minIndex;
    }

    private int[] getErrorPerBand(int rgb1, int rgb2) {
        int[] error = new int[3];

        error[0] = ((rgb1 >> 16) & 0xff) - ((rgb2 >> 16) & 0xff);
        error[1] = ((rgb1 >> 8) & 0xff) - ((rgb2 >> 8) & 0xff);
        error[2] = (rgb1 & 0xff) - (rgb2 & 0xff);

        return error;
    }

    private double distanceBetween(int c1, int c2) {
        int r1 = (c1 >> 16) & 0xff;
        int g1 = (c1 >> 8) & 0xff;
        int b1 = c1 & 0xff;
        int r2 = (c2 >> 16) & 0xff;
        int g2 = (c2 >> 8) & 0xff;
        int b2 = c2 & 0xff;

        return Math.sqrt(Math.pow(r2 - r1, 2)
                + Math.pow(g2 - g1, 2)
                + Math.pow(b2 - b1, 2));
    }

    private void diffuseSmallError(BufferedImage src, int[] error, Location pt, boolean floyd) {
        int[][] calcError = new int[4][3];

        int[] coefficents = new int[]{7, 3, 5, 1};
        double divisor = 16;

        if (!floyd) {
            coefficents = new int[]{2, 1, 1, 0};
            divisor = 4;
        }

        for (int i = 0; i < coefficents.length; i++) {
            calcError[i][0] = error[0] * (int) (coefficents[i] / divisor);
            calcError[i][1] = error[1] * (int) (coefficents[i] / divisor);
            calcError[i][2] = error[2] * (int) (coefficents[i] / divisor);
        }

        for (int i = 0; i < 3; i++) {
            if (pt.col + 1 < src.getWidth()) {
                int old1 = src.getRaster().getSample(pt.col + 1, pt.row, i) + calcError[0][i];
                src.getRaster().setSample(pt.col + 1, pt.row, i, clamp(old1));
            }
            if (pt.col - 1 > 0 && pt.row + 1 < src.getHeight()) {
                int old2 = src.getRaster().getSample(pt.col - 1, pt.row + 1, i) + calcError[1][i];
                src.getRaster().setSample(pt.col - 1, pt.row + 1, i, clamp(old2));
            }
            if (pt.row + 1 < src.getHeight()) {
                int old3 = src.getRaster().getSample(pt.col, pt.row + 1, i) + calcError[2][i];
                src.getRaster().setSample(pt.col, pt.row + 1, i, clamp(old3));
            }

            if (pt.col + 1 < src.getWidth() && pt.row + 1 < src.getHeight()) {
                int old4 = src.getRaster().getSample(pt.col + 1, pt.row + 1, i) + calcError[3][i];
                src.getRaster().setSample(pt.col + 1, pt.row + 1, i, clamp(old4));
            }
        }

    }

    private int clamp(int i) {
        if (i < 0) return 0;
        else return Math.min(i, 255);
    }

    private void diffuseGeneralError(BufferedImage src, int[] error, Location pt, int[] matrix, double divisor) {
        int[][] calcError = new int[12][3];

        for (int i = 0; i < matrix.length; i++) {
            for (int b = 0; b < 3; b++) {
                calcError[i][b] = error[b] * (int) (matrix[i] / divisor);
            }
        }

        for (int i = 0; i < 3; i++) {
            if (pt.col + 1 < src.getWidth()) {
                int old1 = src.getRaster().getSample(pt.col + 1, pt.row, i) + calcError[0][i];

                if (old1 < 0) old1 = 0;
                if (old1 > 255) old1 = 255;

                src.getRaster().setSample(pt.col + 1, pt.row, i, old1);
            }
            if (pt.col + 2 < src.getWidth()) {
                int old2 = src.getRaster().getSample(pt.col + 2, pt.row, i) + calcError[1][i];

                if (old2 < 0) old2 = 0;
                if (old2 > 255) old2 = 255;

                src.getRaster().setSample(pt.col + 2, pt.row, i, old2);
            }

            for (int x = 0; x < 10; x++) {
                int col = ((x % 5) - 3) + pt.col;
                int row = (x > 5 ? 2 : 1) + pt.row;

                if (col < src.getWidth() && col >= 0 && row < src.getHeight() && row >= 0) {
                    int oldn = src.getRaster().getSample(col, row, i) + calcError[x + 2][i];
                    src.getRaster().setSample(col, row, i, clamp(oldn));
                }
            }

        }

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