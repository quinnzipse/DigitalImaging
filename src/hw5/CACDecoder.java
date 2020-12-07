package hw5;

import pixeljelly.io.ImageDecoder;

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

        return null;
    }
}
