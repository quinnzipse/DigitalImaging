package hw5;

import pixeljelly.io.ImageEncoder;
import pixeljelly.scanners.Location;
import pixeljelly.scanners.RasterScanner;

import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

public class CACEncoder extends ImageEncoder {
    private final int[] tols;
    private static final int min = 0;
    private static final int max = 254;

    public CACEncoder(int[] tolerances) {
        tols = tolerances;
    }

    @Override
    public String getMagicWord() {
        return "QCAC";
    }

    @Override
    public void encode(BufferedImage bufferedImage, OutputStream outputStream) throws IOException {
        MemoryCacheImageOutputStream out = new MemoryCacheImageOutputStream(outputStream);
        writeHeader(bufferedImage, out);

        for (int band = 0; band < 3; band++) {
            write(bufferedImage, out, 0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), band);
        }

        out.close();
    }

    private void write(BufferedImage src, MemoryCacheImageOutputStream outStream, int x, int y, int w, int h, int b) throws IOException {
        if (w <= 0 || h <= 0) return;

        BufferedImage subImg = src.getSubimage(x, y, w, h);

        if (isSimilar(subImg, b)) {
//            System.out.println("is similar");
            int avg = averageSample(subImg, b);

            outStream.writeByte(clamp(avg));
        } else {
            outStream.writeByte(255);
            // Quadrant 0
            write(src, outStream, x + w / 2, y, w - w / 2, h / 2, b);
            // Quadrant 1
            write(src, outStream, x, y, w / 2, h / 2, b);
            // Quadrant 2
            write(src, outStream, x, y + h / 2, w / 2, h - h / 2, b);
            // Quadrant 3
            write(src, outStream, x + w / 2, y + h / 2, w - w / 2, h - h / 2, b);
        }
    }

    private int clamp(int avg) {
        if (avg > max) {
            return max;
        }
        return Math.max(avg, min);
    }

    private int averageSample(BufferedImage src, int band) {
        double sum = 0;
        for (Location pt : new RasterScanner(src, false)) {
            sum += src.getRaster().getSample(pt.col, pt.row, band);
        }

        return (int) Math.floor(sum / (src.getWidth() * src.getHeight()));
    }

    private boolean isSimilar(BufferedImage src, int band) {
        long sum = 0;
        for (Location pt : new RasterScanner(src, false)) {
            sum += src.getRaster().getSample(pt.col, pt.row, band);
        }

        double mean = (double) sum / (src.getWidth() * src.getHeight());

        return rootMeanSquared(src, band, mean) < tols[band];
    }

    private double rootMeanSquared(BufferedImage src, int band, double mean) {
        double sum = 0;
        for (Location pt : new RasterScanner(src, false)) {
            int sample = src.getRaster().getSample(pt.col, pt.row, band);
            sum += Math.pow(sample - mean, 2);
        }

        sum /= (src.getWidth() * src.getHeight());

        return Math.sqrt(sum);
    }
}
