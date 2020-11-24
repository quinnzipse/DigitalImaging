package hw4;

import pixeljelly.ops.BandExtractOp;
import pixeljelly.ops.ConvolutionOp;
import pixeljelly.ops.NullOp;
import pixeljelly.ops.PluggableImageOp;
import pixeljelly.scanners.Location;
import pixeljelly.scanners.RasterScanner;
import pixeljelly.utilities.NonSeperableKernel;
import pixeljelly.utilities.SimpleColorModel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;

public class OrientedEdgeOp extends NullOp implements PluggableImageOp {
    private double strength;
    private double orientation;
    private double epsilon;
    private boolean invertMax, invertMin;
    private double max, min;

    public OrientedEdgeOp() {
        this(.5, .75, .15);
    }

    public OrientedEdgeOp(double strength, double orientation, double epsilon) {
        this.epsilon = epsilon;
        this.orientation = orientation;
        this.strength = strength;
        max = this.orientation + epsilon;
        if (max > 1) {
            max -= 1;
            invertMax = true;
        }
        min = this.orientation - epsilon;
        if (min < 0) {
            min += 1;
            invertMin = true;
        }
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

        BufferedImage xConvolve = (new ConvolutionOp(gX, false)).filter(temp, null);
        BufferedImage yConvolve = (new ConvolutionOp(gY, false)).filter(temp, null);

        double maxDist = 0;

        for (Location pt : new RasterScanner(temp, false)) {
            // The convolved images are greyscale (0-255) based on their x and y differences respectively.
            int dx = xConvolve.getRaster().getSample(pt.col, pt.row, 0);
            int dy = yConvolve.getRaster().getSample(pt.col, pt.row, 0);

            // Convert to polar coordinates
            double distance = Math.sqrt(dx * dx + dy * dy) / 255;
            double direction = Math.atan2(dy, dx);

            if (distance > strength) {
                maxDist = Math.max(maxDist, distance);
                dest.setRGB(pt.col, pt.row, Color.GREEN.getRGB());

                if (direction < max && min < direction) {
                    dest.setRGB(pt.col, pt.row, Color.RED.getRGB());
                }
            } else {
                dest.setRGB(pt.col, pt.row, src.getRGB(pt.col, pt.row));
            }
        }

        System.out.println("Max Dist: " + maxDist);

        return dest;
    }
}