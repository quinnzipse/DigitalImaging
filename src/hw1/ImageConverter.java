package hw1;

import java.awt.image.BufferedImage;

public class ImageConverter {

    public static BufferedImage toBufferedImage(DigitalImage src) {
        var outImage = new BufferedImage(src.getWidth(), src.getHeight(),
                (src instanceof IndexedDigitalImage ? BufferedImage.TYPE_BYTE_INDEXED : BufferedImage.TYPE_INT_RGB));

        for (int y = 0; y < src.getHeight(); y++) {
            for (int x = 0; x < src.getWidth(); x++) {
                var rgb = src.getPixel(x, y);
                outImage.setRGB(rgb[0], rgb[1], rgb[2]);
            }
        }

        return outImage;
    }

    public static DigitalImage toDigitalImage(BufferedImage src) {

        DigitalImage outImage;

        if (src.getType() == BufferedImage.TYPE_BYTE_INDEXED) {
            outImage = new IndexedDigitalImage(src.getWidth(), src.getHeight());
        } else {
            outImage = new PackedPixelImage(src.getWidth(), src.getHeight(), 3);
        }

        for (int y = 0; y < src.getHeight(); y++) {
            for (int x = 0; x < src.getWidth(); x++) {
                var rgb = src.getRGB(x, y);
                outImage.setPixel(x, y, new int[]{rgb >> 16 & 0xFF, rgb >> 8 & 0xFF, rgb & 0xFF});
            }
        }

        return outImage;
    }
}
