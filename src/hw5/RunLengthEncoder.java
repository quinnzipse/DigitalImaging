package hw5;

import pixeljelly.io.ImageEncoder;

import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class RunLengthEncoder extends ImageEncoder {
    private void writeRun(int length, DataOutputStream os) throws IOException {
        while (length >= 255) {
            os.write(255);
            length -= 255;
        }
        os.write(length);
    }

    public String getMagicWord() {
        return "QRLE";
    }

    private boolean getBit(int sample, int bit) {

        // Grey Codes?
        sample ^= sample >> 8;
        sample ^= sample >> 4;
        sample ^= sample >> 2;
        sample ^= sample >> 1;

        return (sample & (0x1 << bit)) != 0;
    }

    private int getRun(BufferedImage source, int col, int row, int band, int bit, boolean isWhite) {
        int result = 0;

        while (col < source.getWidth() && getBit(source.getRaster().getSample(col, row, band), bit) == isWhite) {
            col++;
            result++;
        }

        return result;
    }

    public void encode(BufferedImage src, OutputStream os) throws IOException {
        DataOutputStream outStream = new DataOutputStream(os);

        writeHeader(src, outStream);
        for (int band = 0; band < src.getSampleModel().getNumBands(); band++) {
            for (int bit = 0; bit < src.getSampleModel().getSampleSize(band); bit++) {
                for (int y = 0; y < src.getHeight(); y++) {
                    boolean isWhite = true;
                    int pixelsEncoded = 0;
                    while (pixelsEncoded < src.getWidth()) {
                        int length = getRun(src, pixelsEncoded, y, band, bit, isWhite);
                        isWhite = !isWhite;
                        writeRun(length, outStream);
                        pixelsEncoded += length;
                    }
                }
            }
        }
    }
}
