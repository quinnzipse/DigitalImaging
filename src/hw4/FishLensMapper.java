package hw4;

import pixeljelly.utilities.InverseMapper;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

public class FishLensMapper extends InverseMapper {
    private final double weight;
    private final boolean isInverted;
    private double scale, focalLength;
    private int centerX, centerY;

    public FishLensMapper(double weight, boolean isInverted) {
        this.weight = weight / 2;
        this.isInverted = isInverted;
    }

    @Override
    public void initializeMapping(BufferedImage src) {
        focalLength = Math.max(src.getHeight(), src.getWidth()) / 2F;
        scale = focalLength / Math.log(weight * focalLength + 1);
        centerX = src.getWidth() / 2;
        centerY = src.getHeight() / 2;
    }

    @Override
    public Point2D inverseTransform(Point2D dstPt, Point2D srcPt) {
        if (srcPt == null) {
            srcPt = new Point2D.Double();
        }

        double dx = dstPt.getX() - centerX;
        double dy = dstPt.getY() - centerY;

        // Convert to polar coordinates.
        double rPrime = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
        double tPrime = Math.atan2(dy, dx);

        rPrime = calcR(rPrime);

        srcPt.setLocation(
                rPrime * Math.cos(tPrime) + centerX,
                rPrime * Math.sin(tPrime) + centerY
        );

        return srcPt;
    }

    private double calcR(double rPrime) {
        if (rPrime >= focalLength) {
            return rPrime;
        } else if (isInverted) {
            return scale * Math.log(weight * rPrime + 1);
        } else {
            return (Math.pow(Math.E, rPrime / scale) - 1) / weight;
        }
    }
}
