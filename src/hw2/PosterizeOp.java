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
            int pixel = src.getRGB(pt.col, pt.row);

            double minDistance = Double.MAX_VALUE;
            Color minColor = null;

            for (Color color : colors) {
                double distance = calcL2Distance(color.getRGB(), pixel);
                if (distance < minDistance) {
                    minDistance = distance;
                    minColor = color;
                }
            }

            if (minColor != null) dest.setRGB(pt.col, pt.row, minColor.getRGB());

        }

        return dest;
    }

    private double calcL2Distance(int rgb1, int rgb2) {
        return Math.sqrt(Math.pow((rgb2 >> 16 & 0xff) - (rgb1 >> 16 & 0xff), 2)
                + Math.pow((rgb2 >> 8 & 0xff) - (rgb1 >> 8 & 0xff), 2)
                + Math.pow((rgb2 & 0xff) - (rgb1 & 0xff), 2));
    }
}
