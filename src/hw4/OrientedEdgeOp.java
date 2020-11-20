package hw4;

import pixeljelly.ops.*;
import pixeljelly.scanners.Location;
import pixeljelly.scanners.RasterScanner;
import pixeljelly.utilities.ColorUtilities;
import pixeljelly.utilities.NonSeperableKernel;
import pixeljelly.utilities.SeperableKernel;
import pixeljelly.utilities.SimpleColorModel;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.ConvolveOp;

public class OrientedEdgeOp extends NullOp implements PluggableImageOp {
    private double strength;
    private double orientation;
    private double epsilon;

    public OrientedEdgeOp() {

    }

    public OrientedEdgeOp(double strength, double orientation, double epsilon) {
        this.epsilon = epsilon;
        this.orientation = orientation;
        this.strength = strength;
    }

    @Override
    public BufferedImageOp getDefault(BufferedImage bufferedImage) {
        return new OrientedEdgeOp(.5, .75, .15);
    }

    public double getStrength() {
        return strength;
    }

    public void setStrength(double strength) {
        this.strength = strength;
    }

    public double getOrientation() {
        return orientation;
    }

    public void setOrientation(double orientation) {
        this.orientation = orientation;
    }

    public double getEpsilon() {
        return epsilon;
    }

    public void setEpsilon(double epsilon) {
        this.epsilon = epsilon;
    }

    @Override
    public String getAuthorName() {
        return "Quinn Zipse";
    }

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dest) {
        if (dest == null) {
            dest = createCompatibleDestImage(src, src.getColorModel());
        }

        BufferedImage temp = (new BandExtractOp(SimpleColorModel.HSV, 2)).filter(src, null);

        NonSeperableKernel gX = new NonSeperableKernel(3, 1, new float[]{1, 0, -1});
        NonSeperableKernel gY = new NonSeperableKernel(1, 3, new float[]{1, 0, -1});

        temp = new ConvolutionOp(gX);

        BufferedImageOp mog = new AddBinaryOp(ConvolveOp(), ConvolveOp())

        return dest;
    }
}