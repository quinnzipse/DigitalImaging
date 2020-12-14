package hw5;

import pixeljelly.io.ImageDecoder;

import javax.imageio.stream.MemoryCacheImageInputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class CACDecoder extends ImageDecoder {
    @Override
    public String getMagicWord() {
        return "QCAC";
    }

    @Override
    public BufferedImage decode(InputStream inputStream) throws IOException {

        MemoryCacheImageInputStream inStream = new MemoryCacheImageInputStream(inputStream);

        if (!inStream.readUTF().equals(getMagicWord())) {
            return null;
        }

        int width = inStream.readShort();
        int height = inStream.readShort();
        int type = inStream.readInt();

        BufferedImage img = new BufferedImage(width, height, type);

        for (int i = 0; i < 3; i++) {
            restore(0, 0, width, height, i, img, inStream);
        }

        return img;
    }

    public void restore(int x, int y, int w, int h, int b, BufferedImage img, MemoryCacheImageInputStream inputStream) throws IOException {
        if (w <= 0 || h <= 0) return;

        int inByte = inputStream.read();
        if (inByte != 0) {
            for (int col = x; col < x + w; col++) {
                for (int row = y; row < y + h; row++) {
                    img.getRaster().setSample(col, row, b, inByte);
                }
            }
        } else {
            // Quadrant 0
            restore(x + w / 2, y, w - w / 2, h / 2, b, img, inputStream);
            // Quadrant 1
            restore(x, y, w / 2, h / 2, b, img, inputStream);
            // Quadrant 2
            restore(x, y + h / 2, w / 2, h - h / 2, b, img, inputStream);
            // Quadrant 3
            restore(x + w / 2, y + h / 2, w - w / 2, h - h / 2, b, img, inputStream);
        }
    }
}
