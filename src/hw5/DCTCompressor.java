package hw5;

import pixeljelly.scanners.Location;
import pixeljelly.scanners.RasterScanner;

import javax.imageio.ImageIO;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.net.URL;

public class DCTCompressor {
    private static ColorSpace yCbCr = ColorSpace.getInstance(ColorSpace.TYPE_YCbCr);

    public static void main(String[] args) {
        try {
            if (args.length < 3) {
                throw new Exception("Not Enough Args!");
            }

            switch (args[0].toLowerCase()) {
                case "encode":
                    encode(args[1], Integer.parseInt(args[2]), args[3]);
                    break;
                case "decode":
                    break;
                default:
                    throw new Exception("Mode Invalid!");
            }

        } catch (Exception e) {
            System.out.println("DCTCompressor Usage: \n DCTCompressor <mode> <input> [<N>] <output>\n");
        }
    }

    private static void encode(String inURL, int n, String compressedName) throws Exception {

        if (n < 1 || n > 64) {
            throw new Exception("N out of bounds!");
        }

        // Get the image from the internet.
        URL url = new URL(inURL);
        BufferedImage img = ImageIO.read(url);

        // Convert the image to yCbCr
        ColorConvertOp op = new ColorConvertOp(img.getColorModel().getColorSpace(), yCbCr, null);
        img = op.filter(img, null);

        // Get the samples in their respective bands.
        int[][] b = new int[3][img.getHeight() * img.getWidth()];
        for (int i = 0; i < b.length; i++) {
            img.getRaster().getSamples(0, 0, img.getWidth(), img.getHeight(), i, b[i]);
        }

        for (Location pt : new RasterScanner(img.getRaster().getBounds())) {

        }
    }
}
