package hw5;

import pixeljelly.io.ImageEncoder;
import pixeljelly.scanners.Location;
import pixeljelly.scanners.RasterScanner;
import pixeljelly.scanners.ZigZagScanner;
import pixeljelly.utilities.ImagePadder;
import pixeljelly.utilities.ZeroPadder;

import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class DCTEncoder extends ImageEncoder {

    private final ImagePadder padder = ZeroPadder.getInstance();
    private final int[][] quantizationTable = new int[][]{
            {16, 11, 10, 16, 24, 40, 51, 61},
            {12, 12, 14, 19, 26, 58, 60, 55},
            {14, 13, 16, 24, 40, 57, 69, 56},
            {14, 17, 22, 29, 51, 87, 80, 62},
            {18, 22, 37, 56, 68, 109, 103, 77},
            {24, 35, 55, 64, 81, 104, 113, 92},
            {49, 64, 78, 87, 103, 121, 120, 101},
            {72, 92, 95, 98, 112, 100, 103, 99}};
    private int n;

    public DCTEncoder(int n) {
        this.n = n;
    }

    @Override
    public String getMagicWord() {
        return "DCT";
    }

    @Override
    public void encode(BufferedImage bufferedImage, OutputStream outputStream) throws IOException {
        DataOutputStream os = new DataOutputStream(outputStream);
        writeHeader(bufferedImage, os);
        os.writeByte(n);

        int i = 0;
        float[][] dctInput = new float[8][8];
        for (int b = 0; b < 3; b++) {
            for (Location pt : new ZigZagScanner(bufferedImage, 8, 8)) {

                dctInput[pt.col % 8][pt.row % 8] = (float) padder.getSample(bufferedImage, pt.col, pt.row, pt.band) - 128;
                if (i % 63 == 0 && i != 0) {
                    // process the block.
                    float[][] dct = forwardDCT(dctInput);

                    // quantize
                    for (int x = 0; x < dct.length; x++) {
                        for (int y = 0; y < dct.length; y++) {
                            dct[x][y] = (int) Math.floor(dct[x][y] / quantizationTable[x][y]);
                        }
                    }

                    // write it out!
                    zigZagWriter(dct, new MemoryCacheImageOutputStream(outputStream));

                }

                i++;
            }
        }

        os.flush();

        os.close();
    }


    public void zigZagWriter(float[][] data, MemoryCacheImageOutputStream os) throws IOException {

        int i = 0;
        for (Location pt : new RasterScanner(new BufferedImage(8, 8, 1), false)) {
            if (i < n) {
                os.writeByte((int) data[pt.row][pt.col]);
            }
        }

    }

    public static float[] forwardDCT(float[] data) {
        final float alpha0 = (float) Math.sqrt(1.0 / data.length);
        final float alphaN = (float) Math.sqrt(2.0 / data.length);
        float[] result = new float[data.length];

        for (int u = 0; u < result.length; u++) {
            for (int x = 0; x < data.length; x++) {
                result[u] += data[x] * (float) Math.cos((2 * x + 1) * u * Math.PI / (2 * data.length));
            }
            result[u] *= (u == 0 ? alpha0 : alphaN);
        }
        return result;
    }

    public static float[][] forwardDCT(float[][] data) {
        float[][] result = new float[data.length][data.length];

        for (int i = 0; i < result.length; i++) {
            result[i] = forwardDCT(data[i]);
        }

        float[] column = new float[data.length];
        for (int v = 0; v < result.length; v++) {
            for (int row = 0; row < data.length; row++) {
                column[row] = result[row][v];
            }

            float[] temp = forwardDCT(column);
            for (int row = 0; row < data.length; row++) {
                result[row][v] = temp[row];
            }
        }

        return result;
    }
}
