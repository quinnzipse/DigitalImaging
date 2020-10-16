package hw2;

import pixeljelly.ops.NullOp;
import pixeljelly.ops.PluggableImageOp;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.util.Arrays;

import static pixeljelly.utilities.ColorUtilities.*;


public class BandExtractOp extends NullOp implements PluggableImageOp {

    private char band;

    public BandExtractOp() {
        band = 'H';
    }

    public BandExtractOp(char band) {
        argCheck(band);
        this.band = band;
    }

    private void argCheck(Character arg) {
        Character[] exceptableArgs = new Character[]{'R', 'G', 'B', 'Y', 'I', 'Q', 'H', 'S', 'V'};
        if (!Arrays.asList(exceptableArgs).contains(arg)) throw new IllegalArgumentException();
    }

    @Override
    public BufferedImageOp getDefault(BufferedImage bufferedImage) {
        return new BandExtractOp('H');
    }

    @Override
    public String getAuthorName() {
        return "Quinn Zipse";
    }

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dest) {

        BufferedImage output = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

        for (int i = 0; i < src.getWidth(); i++) {
            for (int j = 0; j < src.getHeight(); j++) {
                float[] pixel = correctPixel(src.getRaster().getPixel(i, j, new float[3]));

                output.getRaster().setSample(i, j, 0, pixel[findBandIndex()]);
            }
        }

        return output;
    }

    private float[] correctPixel(float[] pixel) {
        switch (band) {
            case 'Y':
            case 'I':
            case 'Q':
                pixel = RGBtoYIQ(pixel);
                break;
            case 'H':
            case 'S':
            case 'V':
                pixel = RGBtoHSV(pixel);
                break;
            default:
                return pixel;
        }

        pixel[0] = (int) Math.ceil(255 * pixel[1]);
        pixel[1] = (int) Math.ceil(255 * pixel[1]);
        pixel[2] = (int) Math.ceil(255 * pixel[1]);

        return pixel;
    }

    private float[] RGBtoYIQ(float[] pixel){
        // Normalize RGB
        pixel[0] /= 255;
        pixel[1] /= 255;
        pixel[2] /= 255;

        // Convert to YIQ
        float[] temp = new float[3];
        temp[0] = (float) (pixel[0] * .299 + pixel[1] * .587 + pixel[2] * .114);
        temp[1] = (float) (pixel[0] * .596 + pixel[1] * -.275 + pixel[2] * -.321);
        temp[2] = (float) (pixel[0] * .212 + pixel[1] * -.523 + pixel[2] * -.311);

        return temp;
    }

    private int findBandIndex() {
        switch (this.band) {
            case 'R':
            case 'Y':
            case 'H':
                return 0;
            case 'G':
            case 'I':
            case 'S':
                return 1;
            case 'B':
            case 'Q':
            case 'V':
                return 2;
        }
        System.err.print("band: " + band + " HEEELPPP");
        return -1;
    }

    public char getBand() {
        return band;
    }

    public void setBand(char band) {
        argCheck(band);
        this.band = band;
    }
}
