package hw2;

import pixeljelly.ops.NullOp;
import pixeljelly.ops.PluggableImageOp;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.WritableRaster;

import static pixeljelly.utilities.ColorUtilities.HSVtoRGB;
import static pixeljelly.utilities.ColorUtilities.RGBtoHSV;


public class ColorHighlightOp extends NullOp implements PluggableImageOp {

    private Color targetColor;

    public ColorHighlightOp() {
        targetColor = Color.red;
    }

    public ColorHighlightOp( Color targetColor) {
        this.targetColor = targetColor;
    }

    @Override
    public BufferedImageOp getDefault(BufferedImage bufferedImage) {
        return new ColorHighlightOp();
    }

    @Override
    public String getAuthorName() {
        return "Quinn Zipse";
    }

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dest) {
        if (dest == null) {
            dest = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());
        }

        BufferedImage hsv = getHSVImage(src);

        for (int i = 0; i < hsv.getWidth(); i++) {
            for (int j = 0; j < hsv.getHeight(); j++) {
                // < H, min(1, S * 1.1 * e^(-3*D), V >
                // D can be calculated by finding the normalized L2 distance between TargetColor and src pixel.
            }
        }
        return dest;
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
                float[] rgb = HSVtoRGB(src.getRaster().getPixel(i, j, new float[3]));
                rgb[0] *= 255;
                rgb[1] *= 255;
                rgb[2] *= 255;
                rgbImgRaster.setPixel(i, j, rgb);
            }
        }
        rgbImg.setData(rgbImgRaster);
        return rgbImg;
    }

    public Color getTargetColor() {
        return targetColor;
    }

    public void setTargetColor(Color targetColor) {
        this.targetColor = targetColor;
    }
}
