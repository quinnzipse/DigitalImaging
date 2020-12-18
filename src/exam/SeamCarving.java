package exam;

import pixeljelly.ops.BandExtractOp;
import pixeljelly.ops.MagnitudeOfGradientOp;
import pixeljelly.utilities.SimpleColorModel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class SeamCarving {
    private static final String IMG_URL = "https://upload.wikimedia.org/wikipedia/commons/c/cb/Broadway_tower_edit.jpg";

    public static void main(String[] args) throws IOException {
        BufferedImage img = getImage(IMG_URL);

        ImageIO.write(new MagnitudeOfGradientOp().filter(new BandExtractOp(SimpleColorModel.HSV, 2).filter(img, null), null), "png", new File("mog.png"));

        EdgeMap map = new EdgeMap(img);

//        ImageIO.write(map.getEdges(), "png", new File("edges.png"));
        ImageIO.write(map.getEnergyImg(), "png", new File("edgeMap.png"));

        int[] path = new int[img.getHeight()];

        for (int i = 1; i <= 600; i++) {
            if (i % 100 == 0)
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
