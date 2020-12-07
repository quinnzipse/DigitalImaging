package hw5;

import pixeljelly.io.ImageEncoder;
import pixeljelly.scanners.Location;
import pixeljelly.scanners.RasterScanner;

import javax.imageio.ImageIO;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.*;
import java.net.URL;

public class RLECompressor {
//    private static final int bitDepth = 8;
//    private final BufferedImage img;
//    private final int[][] rle;
//    private final int[][] previous;
//    private final RandomAccessFile writer;
//
//    public RLECompressor(BufferedImage img, String fileName) throws FileNotFoundException {
//        this.img = img;
//        rle = new int[img.getRaster().getNumBands()][bitDepth];
//        previous = new int[img.getRaster().getNumBands()][bitDepth];
//        writer = new RandomAccessFile(fileName, "rw");
//    }

    public static void main(String[] args) {
        try {
            if (args.length < 2) {
                throw new Exception("Not Enough Args!");
            }

            switch (args[0].toLowerCase()) {
                case "encode":
                    OutputStream os = new FileOutputStream(args[3]);
                    RLEEncoder compressor = new RLEEncoder();

                    compressor.encode(getImage(args[1], args[2]), os);
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

//    private void encode() throws Exception {
//
//
//    }

}
