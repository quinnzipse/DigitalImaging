package exam;

import pixeljelly.ops.BandExtractOp;
import pixeljelly.ops.ConvolutionOp;
import pixeljelly.ops.NullOp;
import pixeljelly.scanners.Location;
import pixeljelly.scanners.RasterScanner;
import pixeljelly.utilities.ColorUtilities;
import pixeljelly.utilities.Kernel2D;
import pixeljelly.utilities.SeperableKernel;
import pixeljelly.utilities.SimpleColorModel;

import java.awt.image.BufferedImage;

public class EdgeDetectionOp extends NullOp {

    private static final float SENSITIVITY = 2F;

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dest) {
        if (dest == null) {
            dest = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        }

        dest = new BandExtractOp(SimpleColorModel.HSV, 2).filter(src, dest);

        // Create a X and Y Sobel Kernel.
        Kernel2D kernelX = new SeperableKernel(new float[]{1 / SENSITIVITY, 2 / SENSITIVITY, 1 / SENSITIVITY}, new float[]{1 / SENSITIVITY, 0, -1 / SENSITIVITY});
        Kernel2D kernelY = new SeperableKernel(new float[]{1 / SENSITIVITY, 0, -1 / SENSITIVITY}, new float[]{1 / SENSITIVITY, 2 / SENSITIVITY, 1 / SENSITIVITY});

        // Apply them to the images.
        BufferedImage imgX = new ConvolutionOp(kernelX, true).filter(dest, null);
        BufferedImage imgY = new ConvolutionOp(kernelY, true).filter(dest, null);

        // Return the combination of the two.
        for (Location pt : new RasterScanner(dest, false)) {
            int x = imgX.getRaster().getSample(pt.col, pt.row, 0);
            int y = imgY.getRaster().getSample(pt.col, pt.row, 0);

            System.out.println(Math.sqrt(x * x + y * y) + " " + ColorUtilities.clamp(Math.sqrt(x * x + y * y)));
            dest.getRaster().setSample(pt.col, pt.row, 0, ColorUtilities.clamp(Math.sqrt(x * x + y * y)));
        }

        return dest;
    }
}
