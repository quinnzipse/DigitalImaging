package hw4;

import pixeljelly.features.Histogram;
import pixeljelly.ops.NullOp;
import pixeljelly.ops.PluggableImageOp;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.util.Arrays;
import java.util.PriorityQueue;

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
    }

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dest) {
        if (dest == null) {
            dest = createCompatibleDestImage(src, src.getColorModel());
        }

        if (this.palette == null) {
            MedianCut cutter = new MedianCut(src, paletteSize);
            this.palette = cutter.getPalette();
            cutter.makeHTML("medianCut.html");
        }

//        for (Color c : palette) {
//            System.out.println(c.getRed() + ", " + c.getGreen() + ", " + c.getBlue());
//        }
//
//        //Pseudo Code from the Power Point.
//        for (Location pt : new RasterScanner(src, false)) {
//            // For every pixel....
//            int index = getClosestIndex(src.getRGB(pt.col, pt.row)); // index of palette color closest todo needs update.
//            dest.setRGB(pt.col, pt.row, index); // set the destination to that color.
//            int error = src.getRGB(pt.col, pt.row) - dest.getRGB(pt.col, pt.row); // Calculate the error.
//            // Diffuse the error to the nearby pixels on a band-by-band basis.
//            diffuseError(error);
//        }

        return dest;
    }

//    private int getClosestIndex(int color) {
//
//        for (Color c : palette) {
//            distanceBetween(c.getRGB(), color);
//        }
//
//        return 0;
//    }

//    private int distanceBetween(int c1, int c2) {
//        int[] rgb = new int[]{
//                (c1 >> 16) & 0xff, (c1 >> 8) & 0xff, (c1 & 0xff)
//        };
//        int[] rgb2 = new int[]{
//                (c2 >> 16) & 0xff, (c2 >> 8) & 0xff, (c2 & 0xff)
//        };
////        return Math.sqrt();
//    }

    private void diffuseError(int error) {

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