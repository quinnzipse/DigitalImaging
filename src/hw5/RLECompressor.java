package hw5;

import javax.imageio.ImageIO;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.*;
import java.net.URL;

public class RLECompressor {
    public static void main(String[] args) {
        try {
            if (args.length < 2) {
                throw new Exception("Not Enough Args!");
            }

            switch (args[0].toLowerCase()) {
                case "encode":
                    OutputStream os = new FileOutputStream(args[3] + ".rle");
                    RunLengthEncoder compressor = new RunLengthEncoder();

                    compressor.encode(getImage(args[1], args[2]), os);
                    break;
                case "decode":
                    InputStream is = new FileInputStream(args[1]);
                    RunLengthDecoder decoder = new RunLengthDecoder();

                    BufferedImage image = decoder.decode(is);

                    File file = new File(args[2]);
                    ImageIO.write(image, ".png", file);
                    break;
                default:
                    throw new Exception("Mode Invalid!");
            }

        } catch (Exception e) {
            System.out.println("RLECompressor Usage: \n RLECompressor <mode> <input> [<model>] <output>\n\n");
            e.printStackTrace();
        }
    }

    private static BufferedImage getImage(String inURL, String model) throws Exception {
        // Get the image from the internet.
        URL url = new URL(inURL);
        BufferedImage img = ImageIO.read(url);

        ColorConvertOp op;
        if (model.equalsIgnoreCase("hsb")) {
            op = new ColorConvertOp(img.getColorModel().getColorSpace(), ColorSpace.getInstance(ColorSpace.TYPE_HSV), null);
        } else {
            op = new ColorConvertOp(img.getColorModel().getColorSpace(), ColorSpace.getInstance(ColorSpace.TYPE_RGB), null);
        }

        // Convert the image to the selected color space
        return op.filter(img, null);
    }

}
