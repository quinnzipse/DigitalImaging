package hw4;

import pixeljelly.ops.NullOp;
import pixeljelly.ops.PluggableImageOp;
import pixeljelly.scanners.Location;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;

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

        for(Location pt : )

        return dest;
    }
}