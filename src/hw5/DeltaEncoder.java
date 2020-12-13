package hw5;

import pixeljelly.io.ImageEncoder;
import pixeljelly.scanners.Location;
import pixeljelly.scanners.RasterScanner;

import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class DeltaEncoder extends ImageEncoder {
    public int[] deltas;

    public DeltaEncoder(int[] deltas) {
        this.deltas = deltas;
    }

    @Override
    public String getMagicWord() {
        return "QDLT";
    }

    @Override
    public void encode(BufferedImage bufferedImage, OutputStream outputStream) throws IOException {
        writeHeader(bufferedImage, new DataOutputStream(outputStream));
        MemoryCacheImageOutputStream os = new MemoryCacheImageOutputStream(outputStream);

        for (int delta : deltas) {
            System.out.print(delta);
            os.writeByte(delta);
        }

        int prev = -1;
        for (int b = 0; b < 3; b++) {
            for (Location pt : new RasterScanner(bufferedImage, false)) {
                int sample = bufferedImage.getRaster().getSample(pt.col, pt.row, b);

                if (pt.col == 0) {
                    // New Row. Encode the first value.
                    os.writeByte(sample);
                    prev = sample;
                    System.out.println();
                    continue;
                }

                int error = sample - prev;
                System.out.print(error + " ");
                if (error > 0) { //deltas[pt.band]
                    // write if it's bigger or smaller than the delta amount.
                    os.writeBit(1);
                    prev += deltas[b];
                } else {
                    os.writeBit(0);
                    prev -= deltas[b];
                }
            }
        }
        os.flush();

        os.close();
    }
}
