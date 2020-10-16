package hw2;

import pixeljelly.features.Histogram;
import pixeljelly.ops.NullOp;
import pixeljelly.ops.PluggableImageOp;
import pixeljelly.utilities.ImagePadder;
import pixeljelly.utilities.ReflectivePadder;

import java.awt.image.*;

import static pixeljelly.utilities.ColorUtilities.*;

public class LocalEqualizeOp extends NullOp implements PluggableImageOp {
    private int w, h;
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
        for (int i = 0; i < src.getWidth(); i++) {
            for (int j = 0; j < src.getHeight(); j++) {
                BufferedImage hsv = getHSVImage(getSubImage(src, i, j));

                equalize(hsv, 2);

                dest.getRaster().setPixel(i, j, hsv.getRaster().getPixel((int) Math.ceil(hsv.getWidth() / 2.0), (int) Math.ceil(hsv.getHeight() / 2.0), new float[3]));
            }
        }
        return getRGBImage(dest);
    }

    private BufferedImage getSubImage(BufferedImage src, int i, int j){
        BufferedImage subImg = new BufferedImage(w, h, src.getType());

        for (int x = -(w / 2); x < (w / 2); x++) {
            for (int y = -(h / 2); y < (h / 2); y++) {
                for (int b = 0; b < 3; b++) {
                    ImagePadder padder = ReflectivePadder.getInstance();
                    subImg.getRaster().setSample(x + (w / 2), y + (h / 2), b, padder.getSample(src, i + x, j + y, b));
                }
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
