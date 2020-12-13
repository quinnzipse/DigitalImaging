package hw5;

import pixeljelly.io.ImageDecoder;
import pixeljelly.scanners.Location;
import pixeljelly.scanners.RasterScanner;

import javax.imageio.stream.MemoryCacheImageInputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class DeltaDecoder extends ImageDecoder {
    public double[] deltas;

    public DeltaDecoder(double[] deltas) {
        this.deltas = deltas;
    }

    @Override
    public String getMagicWord() {
        return "QDLT";
    }

    @Override
    public BufferedImage decode(InputStream inputStream) throws IOException {
        MemoryCacheImageInputStream inStream = new MemoryCacheImageInputStream(inputStream);
        inputStream.readNBytes(getMagicWord().length());

        int width = inStream.readShort();
        int height = inStream.readShort();
        int type = inStream.readInt();

        BufferedImage destImg = new BufferedImage(width, height, type);

        int prev = 0;

        for (Location pt : new RasterScanner(destImg, true)) {

            if (pt.col == 0) {
                prev = inStream.readByte();
            } else if (inStream.readBit() == 0) {
                prev -= deltas[pt.band];
            } else {
                prev += deltas[pt.band];
            }

            destImg.getRaster().setSample(pt.col, pt.row, pt.band, prev);
        }

        return destImg;
    }
}
