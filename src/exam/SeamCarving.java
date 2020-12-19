package exam;

import jdk.jshell.spi.SPIResolutionException;
import pixeljelly.ops.BandExtractOp;
import pixeljelly.ops.MagnitudeOfGradientOp;
import pixeljelly.utilities.SimpleColorModel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class SeamCarving {

    public static void main(String[] args) {
        try {

            // Read input
            String input = args[0];
            String output = args[1];
            String mode = args[2];

            BufferedImage img = getImage(input);

            System.out.println("Image Loaded!");

            if (mode.substring(1).equalsIgnoreCase("erase")) {
                // erase
                ArrayList<Rectangle> rectangles = new ArrayList<>();
                for (int r = 3; r < args.length; r++) {
                    String[] parms = args[r].split(",");
                    rectangles.add(new Rectangle(Integer.parseInt(parms[0]),
                            Integer.parseInt(parms[1]),
                            Integer.parseInt(parms[2]),
                            Integer.parseInt(parms[3])));
                }

                System.out.println("Removing " + rectangles.size() + " rectangle(s)!");

                img = Carver.erase(img, rectangles);

            } else if (mode.substring(1).equalsIgnoreCase("size")) {
                // size
                String[] numbers = args[3].split(",");

                int[] properties = new int[numbers.length];

                for (int i = 0; i < numbers.length; i++) {
                    properties[i] = Integer.parseInt(numbers[i]);
                }

                img = sizeImage(img, properties[0], properties[1]);

            } else {
                throw new Exception("Invalid Command!");
            }

            ImageIO.write(img, "png", new File(output));

        } catch (Exception e) {
            System.out.println("SeamCarver Usage:\nSeamCarver <input> <output> (-erase <x,y,w,h>* | -size <w,h> )");
            e.printStackTrace();
        }
    }

    private static BufferedImage getImage(String imgUrl) throws IOException {
        URL url = new URL(imgUrl);
        return ImageIO.read(url);
    }

    public static BufferedImage sizeImage(BufferedImage img, int w, int h) {

        int width = w - img.getWidth();
        int height = h - img.getHeight();

        System.out.println("Resizing to " + w + ", " + h + "!");

        if (width > 0) {
            img = Carver.addPathsX(img, width);
        } else {
            img = Carver.deletePathsX(img, Math.abs(width), null);
        }

        if (height > 0) {
            img = Carver.addPathsY(img, height);
        } else {
            img = Carver.deletePathsY(img, Math.abs(height), null);
        }

        return img;
    }
}
