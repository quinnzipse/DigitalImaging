package hw5;

import pixeljelly.io.ImageDecoder;

import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class RunLengthDecoder extends ImageDecoder {
    @Override
    public String getMagicWord() {
        return "QRLE";
    }

    @Override
    public BufferedImage decode(InputStream inputStream) throws IOException {
        DataInputStream in = new DataInputStream(inputStream);

        if (!magicWordMatches(in)) {
            return null;
        }

        int width = in.readShort();
        int height = in.readShort();
        int type = in.readInt();

        BufferedImage out = new BufferedImage(width, height, type);

        for (int band = 0; band < out.getSampleModel().getNumBands(); band++) {
            for (int bit = 0; bit < out.getSampleModel().getSampleSize(band); bit++) {
                for (int y = 0; y < out.getHeight(); y++) {
                    int colsRecovered = 0;
                    boolean isWhite = true;
                    while (colsRecovered < out.getWidth()) {
                        int run = in.read();
                        recoverRun(band, bit, y, isWhite, run, out);
                        colsRecovered += run;
                        if (run != 255) isWhite = !isWhite;
                    }
                }
            }
        }

        return out;
    }

    private void recoverRun(int band, int bit, int y, boolean isWhite, int run, BufferedImage img) {
        if (!isWhite) return;

        for (int i = 0; i < run; i++) {
            int sample = img.getRaster().getSample(i, y, band);
            img.getRaster().setSample(i, y, band, sample | 1 << bit);
        }
    }

    private boolean magicWordMatches(DataInputStream inputStream) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < getMagicWord().length(); i++) {
            sb.append(inputStream.readChar());
        }

        return !getMagicWord().equals(sb.toString());
    }
}
