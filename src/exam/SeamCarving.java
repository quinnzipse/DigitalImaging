package exam;

import pixeljelly.ops.*;
import pixeljelly.scanners.Location;
import pixeljelly.scanners.RasterScanner;
import pixeljelly.utilities.ColorUtilities;
import pixeljelly.utilities.Kernel2D;
import pixeljelly.utilities.SeperableKernel;
import pixeljelly.utilities.SimpleColorModel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class SeamCarving {
    private static final String IMG_URL = "https://www.moma.org/media/W1siZiIsIjM4NjQ3MCJdLFsicCIsImNvbnZlcnQiLCItcXVhbGl0eSA5MCAtcmVzaXplIDIwMDB4MTQ0MFx1MDAzZSJdXQ.jpg?sha=4c0635a9ee70d63e";

    public static void main(String[] args) throws IOException {
        BufferedImage img = getImage(IMG_URL);

        ImageIO.write(new MagnitudeOfGradientOp().filter(img, null), "png", new File("mog.png"));

        BufferedImage energy = getEdges(img);

        EdgeMap map = new EdgeMap(energy);

        int[] path = new int[img.getHeight()];

//        for (int i = 0; i < 200; i++) {
            map.findPath(path);
            img = map.deletePath(path, img);
//        }

        ImageIO.write(img, "png", new File("carved.png"));
//        ImageIO.write(map.toImg(), "png", new File("edgeMap.png"));
//        ImageIO.write(energy, "png", new File("edges.png"));
    }

    private static BufferedImage getImage(String imgUrl) throws IOException {
        URL url = new URL(imgUrl);
        return ImageIO.read(url);
    }

    private static BufferedImage getEdges(BufferedImage in) {
        return new BandExtractOp(SimpleColorModel.HSV, 2).filter(detectEdges(in), null);
    }

    private static BufferedImage detectEdges(BufferedImage in) {

        // Create a X and Y Sobel Kernel.
        Kernel2D kernelX = new SeperableKernel(new float[]{1, 2, 1}, new float[]{1, 0, -1});
        Kernel2D kernelY = new SeperableKernel(new float[]{1, 0, -1}, new float[]{1, 2, 1});

        // Apply them to the images.
        BufferedImage imgX = new ConvolutionOp(kernelX, true).filter(in, null);
        BufferedImage imgY = new ConvolutionOp(kernelY, true).filter(in, null);

        BufferedImage dest = new BufferedImage(imgY.getWidth(), imgY.getHeight(), imgY.getType());

        // Return the combination of the two.
        for (Location pt : new RasterScanner(dest, false)) {
            int x = imgX.getRaster().getSample(pt.col, pt.row, 0);
            int y = imgY.getRaster().getSample(pt.col, pt.row, 0);

            dest.getRaster().setSample(pt.col, pt.row, 0, ColorUtilities.clamp(Math.sqrt(x * x + y * y)));
        }

        return dest;
    }
}
