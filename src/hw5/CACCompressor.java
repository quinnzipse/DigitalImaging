package hw5;

import javax.imageio.ImageIO;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.io.File;
import java.net.URL;

public class CACCompressor {
    public static void main(String[] args) {
        try {
            if (args.length < 2) {
                throw new Exception("Not Enough Args!");
            }

            switch (args[0].toLowerCase()) {
                case "encode":
                    CACEncoder cacCompressor = new CACEncoder(new int[]{10, 20, 10});
                    cacCompressor.encode(getImage(args[1], args[2]), new File("something"));
                    break;
                case "decode":
                    CACDecoder decoder = new CACDecoder();
                    File in = new File(args[1]), out = new File(args[2]);

                    if (decoder.canDecode(in)) {
                        BufferedImage image = decoder.decode(in);
                        ImageIO.write(image, "png", out);
                    } else {
                        throw new Exception("Incompatible File!");
                    }

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
            op = new ColorConvertOp(img.getColorModel().getColorSpace(), ColorSpace.getInstance(ColorSpace.TYPE_HLS), null);
        } else {
            op = new ColorConvertOp(img.getColorModel().getColorSpace(), ColorModel.getRGBdefault().getColorSpace(), null);
        }

        // Convert the image to the selected color space
        return op.filter(img, null);
    }

}
