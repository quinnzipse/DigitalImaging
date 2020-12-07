package hw5;

import pixeljelly.io.ImageEncoder;
import pixeljelly.scanners.Location;
import pixeljelly.scanners.RasterScanner;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

public class RLEEncoder extends ImageEncoder {
    private static final int colorDepth = 8;
    private static final boolean cDefault = false;

    @Override
    public String getMagicWord() {
        return "QRLE";
    }

    @Override
    public void encode(BufferedImage img, OutputStream outputStream) throws IOException {
        final int[][] rle = new int[img.getRaster().getNumBands()][colorDepth];
        final boolean[][] previous = new boolean[img.getRaster().getNumBands()][colorDepth];

        // Get the samples in their respective bands.
        for (Location pt : new RasterScanner(img, true)) {
            // Doing 24 bits at a time? Why Not!
            int sample = img.getRaster().getSample(pt.col, pt.row, pt.band);
            for (int i = 0; i < colorDepth; i++) {

                // Get Each Bit
                boolean color = (sample >> i & 1) == 1;

                if (rle[pt.band][i] == 255) {
                    // Reached the max, next color will be the same.
                    write(outputStream, rle[pt.band][i]);
                    rle[pt.band][i] = 0;
                    continue;
                }

                if (pt.col == 0) {
                    // New Column
                    write(outputStream, rle[pt.band][i]);
                    previous[pt.band][i] = cDefault;
                    rle[pt.band][i] = 0;
                }

                if (color == previous[pt.band][i]) {
                    // RUNNNNNNNNNNNNN.
                    rle[pt.band][i]++;
                } else {
                    // Broke the run.
                    write(outputStream, rle[pt.band][i]);

                    // Next run will be opposite color
                    previous[pt.band][i] = !color;

                    // Start at one.
                    rle[pt.band][i] = 1;
                }
            }
        }
    }

    private void write(OutputStream os, int num) throws IOException {
        if (num > 255) {
//            throw new IOException("Num Out of Bounds!");
        }
        os.write(num);
    }
}