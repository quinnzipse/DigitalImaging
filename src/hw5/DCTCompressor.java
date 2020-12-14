package hw5;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

public class DCTCompressor {
    public static void main(String[] args) {
        try {
            if (args.length < 3) {
                throw new Exception("Not Enough Args!");
            }

            switch (args[0].toLowerCase()) {
                case "encode":
                    DCTEncoder encoder = new DCTEncoder(Integer.parseInt(args[2]));
                    encoder.encode(getImage(args[1]), new File(args[args.length - 1]));
                    break;
                case "decode":
                    DCTDecoder decoder = new DCTDecoder();
                    File out = new File(args[2]);
                    BufferedImage image = decoder.decode(new File(args[1]));
                    ImageIO.write(image, "png", out);
                    break;
                default:
                    throw new Exception("Mode Invalid!");
            }

        } catch (Exception e) {
            System.out.println("DCTCompressor Usage: \n DCTCompressor <mode> <input> [<N>] <output>\n");
            e.printStackTrace();
        }
    }

    private static BufferedImage getImage(String inURL) throws Exception {
        // Get the image from the internet.
        URL url = new URL(inURL);

        // Convert the image to the selected color space
        return ImageIO.read(url);
    }
}
