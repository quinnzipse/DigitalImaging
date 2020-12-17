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

        EdgeMap map = new EdgeMap(img);
        ImageIO.write(map.getEnergyImg(), "png", new File("edgeMap.png"));

        int[] path = new int[img.getHeight()];

        for (int i = 0; i < 500; i++) {
            System.out.println("Deleting line " + i);
            path = map.findPath(path);
            map.deletePath(path);
        }

        ImageIO.write(map.getImg(), "png", new File("carved.png"));
//        ImageIO.write(energy, "png", new File("edges.png"));
    }

    private static BufferedImage getImage(String imgUrl) throws IOException {
        URL url = new URL(imgUrl);
        return ImageIO.read(url);
    }
}
