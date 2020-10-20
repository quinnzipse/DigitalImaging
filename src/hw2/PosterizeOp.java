package hw2;

import pixeljelly.ops.NullOp;
import pixeljelly.ops.PluggableImageOp;
import pixeljelly.scanners.Location;
import pixeljelly.scanners.RasterScanner;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;

import static pixeljelly.utilities.ColorUtilities.HSVtoPackedRGB;
import static pixeljelly.utilities.ColorUtilities.RGBtoHSV;


public class PosterizeOp extends NullOp implements PluggableImageOp {

    private static final Color[] colors = new Color[]{Color.RED, Color.GREEN, Color.BLUE, Color.CYAN, Color.MAGENTA, Color.YELLOW, Color.BLACK, Color.WHITE};

    public PosterizeOp() {

    }

    @Override
    public BufferedImageOp getDefault(BufferedImage bufferedImage) {
        return new PosterizeOp();
    }

    @Override
    public String getAuthorName() {
        return "Quinn Zipse";
    }

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dest) {
        if (dest == null) {
            dest = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());
        }

        for (Location pt : new RasterScanner(src, false)) {
            float[] hsvVals = RGBtoHSV(src.getRGB(pt.col, pt.row));

            double minDistance = Double.MAX_VALUE;
            Color minColor = null;

            for (Color color : colors) {
                double distance = calcL2Distance(hsvVals, RGBtoHSV(color));
                if (distance < minDistance) {
                    minDistance = distance;
                    minColor = color;
                }
            }

            if (minColor != null) dest.setRGB(pt.col, pt.row, minColor.getRGB());

        }

        return dest;
    }

    private double calcL2Distance(float[] hsv1, float[] hsv2) {
        double distance = Math.sqrt(0 +
                Math.pow(hsv1[1] * Math.cos(2 * Math.PI * hsv1[0]) - hsv2[1] * Math.cos(2 * Math.PI * hsv2[0]), 2)
                + Math.pow(hsv1[1] * Math.sin(2 * Math.PI * hsv1[0]) - hsv2[1] * Math.sin(2 * Math.PI * hsv2[0]), 2)
                + Math.pow(hsv1[2] - hsv2[2], 2));
        return (distance / Math.sqrt(5));
    }
}
