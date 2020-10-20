package hw2;

import pixeljelly.ops.NullOp;
import pixeljelly.ops.PluggableImageOp;
import pixeljelly.scanners.Location;
import pixeljelly.scanners.RasterScanner;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;

import static pixeljelly.utilities.ColorUtilities.HSVtoPackedRGB;
import static pixeljelly.utilities.ColorUtilities.RGBtoHSV;


public class ColorHighlightOp extends NullOp implements PluggableImageOp {

    private Color targetColor;

    public ColorHighlightOp() {
        targetColor = new Color(220, 50, 50);
    }

    public ColorHighlightOp(Color targetColor) {
        this.targetColor = targetColor;
    }

    @Override
    public BufferedImageOp getDefault(BufferedImage bufferedImage) {
        return new ColorHighlightOp();
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

        float[] targetHSV = RGBtoHSV(targetColor);

        for (Location pt : new RasterScanner(src, false)) {
            float[] hsvVals = RGBtoHSV(src.getRGB(pt.col, pt.row));

            hsvVals[1] = (float) (calcSaturation(targetHSV, hsvVals, hsvVals[1]));

            dest.setRGB(pt.col, pt.row, HSVtoPackedRGB(hsvVals));
        }

        return dest;
    }

    private double calcSaturation(float[] hsv1, float[] hsv2, float saturation) {
        return Math.min(1, saturation * 1.1 * Math.pow(Math.E, -3 * calcL2Distance(hsv1, hsv2)));
    }

    private double calcL2Distance(float[] hsv1, float[] hsv2) {
        double distance = Math.sqrt(0 +
                Math.pow(hsv1[1] * Math.cos(2 * Math.PI * hsv1[0]) - hsv2[1] * Math.cos(2 * Math.PI * hsv2[0]), 2)
                + Math.pow(hsv1[1] * Math.sin(2 * Math.PI * hsv1[0]) - hsv2[1] * Math.sin(2 * Math.PI * hsv2[0]), 2)
                + Math.pow(hsv1[2] - hsv2[2], 2));
        return (distance / Math.sqrt(5));
    }

    public Color getTargetColor() {
        return targetColor;
    }

    public void setTargetColor(Color targetColor) {
        this.targetColor = targetColor;
    }
}
