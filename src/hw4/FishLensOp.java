package hw4;

import pixeljelly.ops.GeometricTransformOp;
import pixeljelly.ops.NullOp;
import pixeljelly.ops.PluggableImageOp;
import pixeljelly.utilities.BilinearInterpolant;
import pixeljelly.utilities.Interpolant;
import pixeljelly.utilities.InverseMapper;

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

        Interpolant interpolant = new BilinearInterpolant();
        InverseMapper mapper = new FishLensMapper(weight, isInverted);
        BufferedImageOp op = new GeometricTransformOp(mapper, interpolant);

        return op.filter(src, dest);
    }
}
