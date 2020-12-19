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
//    private static final String IMG_URL = "https://upload.wikimedia.org/wikipedia/commons/thumb/c/cb/Broadway_tower_edit.jpg/1280px-Broadway_tower_edit.jpg";
    private static final String IMG_URL = "https://www.reproduction-gallery.com/catalogue/uploads/1522662218_large-image_dali-persistence-of-memory-lg.jpg?is_thumbnail=yes";

    public static void main(String[] args) throws IOException {
        BufferedImage img = getImage(IMG_URL);

        ImageIO.write(new MagnitudeOfGradientOp().filter(new BandExtractOp(SimpleColorModel.HSV, 2).filter(img, null), null), "png", new File("mog.png"));

        Carver map = new Carver(img);

//        ImageIO.write(map.getEnergyImg(), "png", new File("edgeMap.png"));

        int[] path = new int[img.getHeight()];

//        try {
//            map = map.addPathsX(1200);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        for (int i = 1; i <= 50; i++) {
            map = map.deletePathY(path);
        }

        ImageIO.write(map.getImg(), "png", new File("carved.png"));
    }

    private static BufferedImage getImage(String imgUrl) throws IOException {
        URL url = new URL(imgUrl);
        return ImageIO.read(url);
    }
}
