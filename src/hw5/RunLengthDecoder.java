package hw5;

import pixeljelly.io.ImageDecoder;
import pixeljelly.scanners.Location;
import pixeljelly.scanners.RasterScanner;

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
                boolean isWhite = true;
                for (int y = 0; y < out.getHeight(); y++) {
                    int colsRecovered = 0;
                    while (colsRecovered < out.getWidth()) {
                        int run = in.read();
                        recoverRun(band, bit, colsRecovered, y, isWhite, run, out);
                        colsRecovered += run;
                        if (run != 255) isWhite = !isWhite;
                    }
                }
            }
        }

        for (Location pt : new RasterScanner(out, true)) {
            int sample = greyToBin(out.getRaster().getSample(pt.col, pt.row, pt.band));
            out.getRaster().setSample(pt.col, pt.row, pt.band, sample);
        }

        return out;
    }

    private int greyToBin(int num) {
        int mask = num;
        while (mask != 0) {
            mask >>= 1;
            num ^= mask;
        }
        return num;
    }

    private void recoverRun(int band, int bit, int colOffset, int y, boolean isWhite, int run, BufferedImage img) {
        if (!isWhite) return;
        int mask = (1 << bit);

        for (int x = colOffset; x < run + colOffset; x++) {
            int sample = img.getRaster().getSample(x, y, band) | mask;
            img.getRaster().setSample(x, y, band, sample);
        }
    }

    private boolean magicWordMatches(DataInputStream inputStream) throws IOException {
        return getMagicWord().equals(inputStream.readUTF());
    }
}
