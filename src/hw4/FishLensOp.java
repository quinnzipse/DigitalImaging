package hw4;

import pixeljelly.ops.NullOp;
import pixeljelly.ops.PluggableImageOp;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;

public class FishLensOp extends NullOp implements PluggableImageOp {

    private double weight;
    private boolean isInverted;

    public FishLensOp() {

    }

    public FishLensOp(double weight, boolean isInverted) {
        this.isInverted = isInverted;
        this.weight = weight;
    }

    @Override
    public BufferedImageOp getDefault(BufferedImage bufferedImage) {
        return new FishLensOp(5, false);
    }

    @Override
    public String getAuthorName() {
        return "Quinn Zipse";
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public boolean isInverted() {
        return isInverted;
    }

    public void setInverted(boolean inverted) {
        isInverted = inverted;
    }

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dest) {
        if (dest == null) {
            dest = createCompatibleDestImage(src, src.getColorModel());
        }

        int focalLength = calcFocalLength(src);
        double scale = calcScale(focalLength);

        return null;
    }

    private int calcFocalLength(BufferedImage src) {
        return Math.max(src.getHeight(), src.getWidth());
    }

    private double calcScale(int focalLength) {
        return focalLength / Math.log(weight * focalLength + 1);
    }

    private double calcR(int focalLength, double rPrime, double scale) {
        if (rPrime >= focalLength) {
            return rPrime;
        } else if (isInverted) {
            return scale * Math.log(weight * rPrime + 1);
        } else {
            return (Math.pow(Math.E, rPrime / scale) - 1) / weight;
        }
    }
}
