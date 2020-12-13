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
    public double[] deltas;

    public DeltaEncoder(double[] deltas) {
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
        int prev = -1;
        for (Location pt : new RasterScanner(bufferedImage, true)) {
            int sample = bufferedImage.getRaster().getSample(pt.col, pt.row, pt.band);

            if (pt.col == 0) {
                // New Row. Encode the first value.
                os.writeByte(sample);
                prev = sample;
                continue;
            }

            int error = prev - sample;

            if (error > deltas[pt.band]) {
                // write if it's bigger or smaller than the delta amount.
                os.writeBit(1);
                prev += deltas[pt.band];
            } else {
                os.writeBit(0);
                prev -= deltas[pt.band];
            }
        }

        os.close();
    }
}
