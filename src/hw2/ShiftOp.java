package hw2;

import pixeljelly.ops.NullOp;
import pixeljelly.ops.PluggableImageOp;
import pixeljelly.scanners.Location;
import pixeljelly.scanners.RasterScanner;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;

import static pixeljelly.utilities.ColorUtilities.*;


public class ShiftOp extends NullOp implements PluggableImageOp {

    private double hueTarget;
    private double satScale;
    private double shiftStrength;

    public ShiftOp() {
    }

    public ShiftOp(double hueTarget, double satScale, double shiftStrength) {
        this.hueTarget = hueTarget;
        this.satScale = satScale;
        this.shiftStrength = shiftStrength;
    }

    @Override
    public BufferedImageOp getDefault(BufferedImage bufferedImage) {
        return new ShiftOp(0, 1.5, 1);
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

        BufferedImage hsv = getHSVImage(src);
        double[] dubs = new double[3];

        for (Location pt : new RasterScanner(src, false)) {
            //        < hShift(H, hueTarget), Y * satScale, V >
            double[] hsvVals = hsv.getRaster().getPixel(pt.col, pt.row, dubs);
            hsvVals[0] = hShift(hsvVals[0] / 255) * 255;
            hsvVals[1] = clamp(hsvVals[1] * (satScale));

            hsv.getRaster().setPixel(pt.col, pt.row, hsvVals);
        }

        return hsvToImage(hsv, dest);
    }

    private double hShift(double h) {
        // Find dH by calculating the absolute angular difference
        // This difference is such that two hues differing by 180 degrees are 1 unit apart;
        // hence the difference between any two hues will always be in the interval [0, 1]
        // The function then returns H moved closer to hueTarget by the amount dHshiftStrength.

        // Calculate the distance away.
        int angle1 = (int) (hueTarget * 360 - h * 360);

        // Find the shortest direction
        boolean clockwise = Math.abs(angle1) > 180 ^ angle1 < 0;

        // Find the shortest magnitude.
        angle1 = Math.abs(angle1);
        double dH = (angle1 > 180 ? 360 - angle1 : angle1) / 180.0;

        double out;
        if (clockwise) {
            // Subtracting is clockwise!
            out = h - Math.pow(dH, shiftStrength) / 2;

            // If you went past 0, wrap around
            if (out <= 0) out += 1;
        } else {
            // Adding is counter-clockwise!
            out = h + Math.pow(dH, shiftStrength) / 2;

            // If you went past 1, wrap around
            if (out >= 1) out -= 1;
        }

        return out;
    }

    private BufferedImage getHSVImage(BufferedImage src) {
        BufferedImage dest = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (Location pt : new RasterScanner(src, false)) {
            int rgb1 = src.getRGB(pt.col, pt.row);
            float[] rgb = new float[3];
            rgb[0] = (rgb1 >> 16 & 0xFF) / 255.0F;
            rgb[1] = (rgb1 >> 8 & 0xFF) / 255.0F;
            rgb[2] = (rgb1 & 0xFF) / 255.0F;
            float[] hsv = RGBtoHSV(rgb);
            hsv[0] *= 255;
            hsv[1] *= 255;
            hsv[2] *= 255;
            dest.getRaster().setPixel(pt.col, pt.row, hsv);
        }

        return dest;
    }

    private BufferedImage hsvToImage(BufferedImage src, BufferedImage dest) {
        for (Location pt : new RasterScanner(src, false)) {
            float[] hsv = src.getRaster().getPixel(pt.col, pt.row, new float[3]);
            hsv[0] /= 255;
            hsv[1] /= 255;
            hsv[2] /= 255;

            dest.setRGB(pt.col, pt.row, HSVtoPackedRGB(hsv));
        }

        return dest;
    }

    public double getSatScale() {
        return satScale;
    }

    public void setSatScale(double satScale) {
        this.satScale = satScale;
    }

    public double getShiftStrength() {
        return shiftStrength;
    }

    public void setShiftStrength(double shiftStrength) {
        this.shiftStrength = shiftStrength;
    }

    public double getHueTarget() {
        return hueTarget;
    }

    public void setHueTarget(double hueTarget) {
        this.hueTarget = hueTarget;
    }
}
