package hw5;

import pixeljelly.io.ImageDecoder;
import pixeljelly.scanners.ImageTiler;
import pixeljelly.scanners.Location;
import pixeljelly.scanners.RasterScanner;
import pixeljelly.scanners.ZigZagScanner;

import javax.imageio.stream.MemoryCacheImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class DCTDecoder extends ImageDecoder {

    private final int[][] quantizationTable = new int[][]{
            {16, 11, 10, 16, 24, 40, 51, 61},
            {12, 12, 14, 19, 26, 58, 60, 55},
            {14, 13, 16, 24, 40, 57, 69, 56},
            {14, 17, 22, 29, 51, 87, 80, 62},
            {18, 22, 37, 56, 68, 109, 103, 77},
            {24, 35, 55, 64, 81, 104, 113, 92},
            {49, 64, 78, 87, 103, 121, 120, 101},
            {72, 92, 95, 98, 112, 100, 103, 99}};

    @Override
    public String getMagicWord() {
        return "DCT";
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
        int n = inStream.readByte();

        BufferedImage img = new BufferedImage(width, height, type);

        BufferedImage smalls = new BufferedImage(8, 8, 1);
        int[][] dctIn = new int[8][8];
        for (int band = 0; band < 3; band++) {
            for (ImageTiler it = new ImageTiler(img, 8, 8); it.hasNext(); ) {
                Rectangle rectangle = it.next();
                int tN = 0;
                for (Location pt : new ZigZagScanner(smalls, 8, 8)) {
                    if (tN < n) {
                        dctIn[pt.row][pt.col] = inStream.readByte() * quantizationTable[pt.row][pt.col];
                        tN++;
                    }
                }

                int[][] samples = inverseDCT(dctIn);

                for (Location pt : new RasterScanner(rectangle)) {
                    if (pt.col < width && pt.row < height) {
                        img.getRaster().setSample(pt.col, pt.row, band, samples[pt.row - rectangle.y][pt.col - rectangle.x]);
                    }
                }

            }
        }
        return img;
    }

    public int[][] inverseDCT(int[][] dctIn) {
        int[][] result = new int[8][8];
        double aU, aV;

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {

                int sum = 0;

                for (int u = 0; u < dctIn.length; u++) {

                    // calc aU
                    if (u == 0) aU = Math.sqrt(1.0 / 8);
                    else aU = Math.sqrt(2.0 / 8);
                    double cosU = Math.cos(((2 * x + 1) * u * Math.PI) / 16);

                    for (int v = 0; v < dctIn.length; v++) {

                        if (v == 0) aV = Math.sqrt(1.0 / 8);
                        else aV = Math.sqrt(2.0 / 8);

                        int cUV = dctIn[u][v];

                        double cosV = Math.cos(((2 * x + 1) * v * Math.PI) / 16);

                        sum += aU * aV * cUV * cosU * cosV;

                    }
                }

                result[x][y] = sum;
            }
        }

        return result;
    }

}
