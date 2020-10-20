package hw2;

import pixeljelly.features.Histogram;
import pixeljelly.ops.BrightnessBandExtractOp;
import pixeljelly.ops.HistogramEqualizeOp;
import pixeljelly.ops.NullOp;
import pixeljelly.ops.PluggableImageOp;
import pixeljelly.scanners.Location;
import pixeljelly.scanners.RasterScanner;
import pixeljelly.utilities.ImagePadder;
import pixeljelly.utilities.ReflectivePadder;

import java.awt.image.*;

import static pixeljelly.utilities.ColorUtilities.*;

public class LocalEqualizeOp extends NullOp implements PluggableImageOp {
    private final int w;
    private final int h;
    private boolean isBanded;

    public LocalEqualizeOp() {
        this(5, 5, true);
    }

    public LocalEqualizeOp(int w, int h, boolean brightnessBandOnly) {
        this.w = w;
        this.h = h;
        this.isBanded = !brightnessBandOnly;
    }

    @Override
    public BufferedImageOp getDefault(BufferedImage bufferedImage) {
        return new LocalEqualizeOp();
    }

    @Override
    public String getAuthorName() {
        return "Quinn Zipse";
    }

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dest) {
        if (dest == null) {
            dest = createCompatibleDestImage(src, src.getColorModel());
        }

        BufferedImage hsv = getHSVImage(src);

        for (Location pt : new RasterScanner(hsv, true)) {
            BufferedImage img = getBrightnessSubImage(hsv, pt.col, pt.row);
            img = (new HistogramEqualizeOp(1)).filter(img, null);
            hsv.getRaster().setSample(pt.col, pt.row, 2, img.getRaster().getSample((int) Math.ceil(w / 2.0), (int) Math.ceil(h / 2.0), 0));
        }

        return getRGBImage(hsv);

    }

    private BufferedImage getBrightnessSubImage(BufferedImage src, int i, int j) {
        BufferedImage subImg = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
        ImagePadder padder = ReflectivePadder.getInstance();
        for (int x = -(w / 2); x <= (w / 2); x++) {
            for (int y = -(h / 2); y <= (h / 2); y++) {
                subImg.getRaster().setSample(x + (w / 2), y + (h / 2), 0, padder.getSample(src, i + x, j + y, 2));
            }
        }

        return subImg;
    }

    private BufferedImage equalize(BufferedImage src, int band) {
        Histogram histogram = new Histogram(src, band);
        LookupOp op = new LookupOp(new ByteLookupTable(0, histogram.toCDFArray()), null);

        return op.filter(src, null);
    }

    private BufferedImage getHSVImage(BufferedImage src) {
        BufferedImage dest = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());
        for (int x = 0; x < src.getWidth(); x++) {
            for (int y = 0; y < src.getHeight(); y++) {
                float[] rgb = src.getRaster().getPixel(x, y, new float[3]);
                rgb[0] /= 255;
                rgb[1] /= 255;
                rgb[2] /= 255;
                float[] hsv = RGBtoHSV(rgb);
                hsv[0] *= 255;
                hsv[1] *= 255;
                hsv[2] *= 255;
                dest.getRaster().setPixel(x, y, hsv);
            }
        }

        return dest;
    }

    private BufferedImage getRGBImage(BufferedImage src) {
        BufferedImage rgbImg = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());
        WritableRaster srcRaster = src.getRaster();
        WritableRaster rgbImgRaster = rgbImg.getRaster();
        for (int i = 0; i < srcRaster.getWidth(); i++) {
            for (int j = 0; j < srcRaster.getHeight(); j++) {
                float[] hsv = src.getRaster().getPixel(i, j, new float[3]);
                hsv[0] /= 255;
                hsv[1] /= 255;
                hsv[2] /= 255;
                float[] rgb = HSVtoRGB(hsv);
                rgb[0] *= 255;
                rgb[1] *= 255;
                rgb[2] *= 255;
                rgbImgRaster.setPixel(i, j, rgb);
            }
        }
        rgbImg.setData(rgbImgRaster);
        return rgbImg;
    }
}
