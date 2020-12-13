package hw5;

import pixeljelly.io.ImageDecoder;
import pixeljelly.scanners.Location;
import pixeljelly.scanners.RasterScanner;

import javax.imageio.stream.MemoryCacheImageInputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class DeltaDecoder extends ImageDecoder {
    public int[] deltas;

    public DeltaDecoder() {
    }

    @Override
    public String getMagicWord() {
        return "QDLT";
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

        BufferedImage destImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        deltas = new int[destImg.getRaster().getNumBands()];

        for (int i = 0; i < deltas.length; i++) {
            deltas[i] = inStream.read();
            System.out.print(deltas[i] + " ");
        }

        System.out.println();

        int prev = 0;

        for (int b = 0; b < destImg.getRaster().getNumBands(); b++) {
            System.out.println(b);
            int i=0;
            for (Location pt : new RasterScanner(destImg, false)) {
                if (pt.col == 0) {
                    while((i % 7) != 0){
                        i++;
                        inStream.readBit();
                    }
                    prev = inStream.read();
                    i=0;
                } else if (inStream.readBit() == 0) {
                    prev -= deltas[b];
                } else {
                    prev += deltas[b];
                }
                i++;
                destImg.getRaster().setSample(pt.col, pt.row, b, prev);
            }
        }

        return destImg;
    }
}
