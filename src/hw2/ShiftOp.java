package hw2;

import pixeljelly.ops.NullOp;
import pixeljelly.ops.PluggableImageOp;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.WritableRaster;

import static pixeljelly.utilities.ColorUtilities.*;


public class ShiftOp extends NullOp implements PluggableImageOp {

    private double hueTarget;
    private double satScale;
    private double shiftStrength;

    public ShiftOp() {
        hueTarget = 0.33;
        satScale = .6;
        shiftStrength = 1;
    }

    public ShiftOp(double hueTarget, double satScale, double shiftStrength) {
        this.hueTarget = hueTarget;
        this.satScale = satScale;
        this.shiftStrength = shiftStrength;
    }

    @Override
    public BufferedImageOp getDefault(BufferedImage bufferedImage) {
        return new ShiftOp();
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

        for (int i = 0; i < hsv.getWidth(); i++) {
            for (int j = 0; j < hsv.getHeight(); j++) {
                //        < hShift(H, hueTarget), Y * satScale, V >
                double[] hsvVals = hsv.getRaster().getPixel(i, j, new double[3]);
                hsvVals[0] = hShift(hsvVals[0] / 255) * 255;
                hsvVals[1] = clamp(hsvVals[1] * (satScale));
                dest.getRaster().setPixel(i, j, hsvVals);
            }
        }
        return getRGBImage(dest);
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
            out = h - Math.pow(dH, shiftStrength);

            // If you went past 0, wrap around
            if (out < 0) out += 1;
        } else {
            // Adding is counter-clockwise!
            out = h + Math.pow(dH, shiftStrength);

            // If you went past 1, wrap around
            if(out > 1) out -= 1;
        }

        return out;
    }

    private BufferedImage getHSVImage(BufferedImage src) {
        BufferedImage dest = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());
        for (int x = 0; x < src.getWidth(); x++) {
            for (int y = 0; y < src.getHeight(); y++) {
                float[] rgb = src.getRaster().getPixel(x, y, new float[3]);
                rgb[0] /= 255;
                rgb[1] /= 255;
                rgb[2] /= 255;
                float[] hsv = RGBtoHSV(rgb);
                hsv[0] *= 255;
                hsv[1] *= 255;
                hsv[2] *= 255;
                dest.getRaster().setPixel(x, y, hsv);
            }
        }

        return dest;
    }

    private BufferedImage getRGBImage(BufferedImage src) {
        BufferedImage rgbImg = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());
        WritableRaster srcRaster = src.getRaster();
        WritableRaster rgbImgRaster = rgbImg.getRaster();
        for (int i = 0; i < srcRaster.getWidth(); i++) {
            for (int j = 0; j < srcRaster.getHeight(); j++) {
                float[] hsv = src.getRaster().getPixel(i, j, new float[3]);
                hsv[0] /= 255;
                hsv[1] /= 255;
                hsv[2] /= 255;
                float[] rgb = HSVtoRGB(hsv);
                rgb[0] *= 255;
                rgb[1] *= 255;
                rgb[2] *= 255;
                rgbImgRaster.setPixel(i, j, rgb);
            }
        }
        rgbImg.setData(rgbImgRaster);
        return rgbImg;
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
