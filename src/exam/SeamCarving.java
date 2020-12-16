package exam;

import pixeljelly.ops.*;
import pixeljelly.utilities.Kernel2D;
import pixeljelly.utilities.SeperableKernel;
import pixeljelly.utilities.SimpleColorModel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class SeamCarving {
    private static final String IMG_URL = "https://cs.brown.edu/courses/cs129/results/proj3/baebi/seam.jpg";
    public static void main(String[] args) throws IOException {
        BufferedImage img = getImage(IMG_URL);

        ImageIO.write(new MagnitudeOfGradientOp().filter(img, null), "png", new File("mog.png"));

        BufferedImage energy = getEdges(img);

        EdgeMap map = new EdgeMap(energy);

        ImageIO.write(map.toImg(), "png", new File("edgeMap.png"));
        ImageIO.write(energy, "png", new File("edges.png"));
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

        // Return the combination of the two.
        return new AddBinaryOp(imgY).filter(imgX, null);
    }
}
