package exam;

import jdk.jshell.spi.SPIResolutionException;
import pixeljelly.ops.BandExtractOp;
import pixeljelly.ops.MagnitudeOfGradientOp;
import pixeljelly.utilities.SimpleColorModel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class SeamCarving {
    private static final String IMG_URL = "https://upload.wikimedia.org/wikipedia/commons/thumb/c/cb/Broadway_tower_edit.jpg/1280px-Broadway_tower_edit.jpg";
//    private static final String IMG_URL = "https://www.reproduction-gallery.com/catalogue/uploads/1522662218_large-image_dali-persistence-of-memory-lg.jpg?is_thumbnail=yes";
//    private static final String IMG_URL = "https://cdn.discordapp.com/attachments/779857732807032874/789711636830617650/unknown.png";

    public static void main(String[] args) {
        try {

            // Read input
            String input = args[0];
            String output = args[1];
            String mode = args[2];
            String[] numbers = args[3].split(",");

            int[] properties = new int[numbers.length];

            for (int i = 0; i < numbers.length; i++) {
                properties[i] = Integer.parseInt(numbers[i]);
            }

            BufferedImage img = getImage(input);

            System.out.println("Image Loaded!");

            if (mode.substring(1).equalsIgnoreCase("erase")) {
                // erase

            } else if (mode.substring(1).equalsIgnoreCase("size")) {
                // size
                int width = img.getWidth() - properties[0];
                int height = img.getHeight() - properties[1];

                System.out.println("Resizing to " + width + ", " + height + "!");

                if (width > 0) {
                    Carver.addPathsX(img, width);
                } else {
                    Carver.deletePathsX(img, Math.abs(width));
                }

                if (height > 0) {
                    Carver.addPathsY(img, height);
                } else {
                    Carver.deletePathsY(img, Math.abs(height));
                }

            } else {
                throw new Exception("Invalid Command!");
            }

//        ImageIO.write(new MagnitudeOfGradientOp().filter(new BandExtractOp(SimpleColorModel.HSV, 2).filter(img, null), null), "png", new File("mog.png"));

//        BufferedImage res = Carver.deletePathsX(img, 200);
//        res = Carver.deletePathsY(res, 200);

            BufferedImage res = Carver.addPathsY(img, 200);
//        res = Carver.addPathsX(res, 200);

            ImageIO.write(res, "png", new File(output));

        } catch (Exception e) {
            System.out.println("SeamCarver Usage:\nSeamCarver <input> <output> (-erase <x,y,w,h>* | -size <w,h> )");
            e.printStackTrace();
        }
    }

    private static BufferedImage getImage(String imgUrl) throws IOException {
        URL url = new URL(imgUrl);
        return ImageIO.read(url);
    }
}
