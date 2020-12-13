package hw5;

import pixeljelly.io.ImageEncoder;

import javax.imageio.ImageIO;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;

public class DeltaCompressor {
    public static void main(String[] args) {
        try {
            if (args.length < 3) {
                throw new Exception("Not Enough Args!");
            }

            String mode = args[0], input = args[1], output = args[args.length - 1];

            switch (mode.toLowerCase()) {
                case "encode":
                    OutputStream os = new FileOutputStream(output);
                    double[] deltas = new double[]{Double.parseDouble(args[3]),
                            Double.parseDouble(args[4]), Double.parseDouble(args[5])};
                    ImageEncoder compressor = new DeltaEncoder(deltas);
                    compressor.encode(getImage(input, args[2]), os);
                    break;
                case "decode":
                    RunLengthDecoder decoder = new RunLengthDecoder();
                    File in = new File(input), out = new File(output);

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
