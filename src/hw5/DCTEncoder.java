package hw5;

import pixeljelly.io.ImageEncoder;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

public class DCTEncoder extends ImageEncoder {

    @Override
    public String getMagicWord() {
        return "DCT";
    }

    @Override
    public void encode(BufferedImage bufferedImage, OutputStream outputStream) throws IOException {

    }
}
