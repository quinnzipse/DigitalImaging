package hw5;

import pixeljelly.io.ImageDecoder;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class RLEDecoder extends ImageDecoder {
    @Override
    public String getMagicWord() {
        return "QRLE";
    }

    @Override
    public BufferedImage decode(InputStream inputStream) throws IOException {
        return null;
    }
}
