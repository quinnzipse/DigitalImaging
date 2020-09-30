import hw1.DigitalImage;

import java.awt.*;

/**
 * @author Quinn Zipse
 */
public class IndexedDigitalImage implements DigitalImage {

    // 8-bit color depth means 255 colors on the palette
    private final Color[] palette = new Color[255];
    private final int height, width;
    private final int[] raster;

    IndexedDigitalImage(int width, int height) {
        this.width = width;
        this.height = height;
        this.raster = new int[width * height];
    }

    IndexedDigitalImage(int width, int height, Color[] palette) {
        this(width, height);
        System.arraycopy(palette, 0, this.palette, 0, palette.length);
    }

    public void setPaletteColor(int paletteIndex, Color color) {
        this.palette[paletteIndex] = color;
    }

    public Color getPaletteColor(int paletteIndex) {
        return this.palette[paletteIndex];
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public int getBands() {
        // Only one because one band refers to one index in the palette.
        return 1;
    }

    @Override

    public int[] getPixel(int x, int y) {
        var color = this.palette[this.raster[x + y * this.width]];

        return new int[]{color.getRed(), color.getGreen(), color.getBlue()};
    }

    @Override
    public void setPixel(int x, int y, int[] pixel) {
        var newColor = new Color(pixel[0], pixel[1], pixel[2]);

        for (int i = 0; i < this.palette.length; i++) {
            if (this.palette[i].equals(newColor)) {
                this.raster[x + y * this.width] = i;
                return;
            }
        }

        // if the color isn't in the pallet add it.
        int openIndex;
        for (openIndex = 0; openIndex < this.palette.length; openIndex++) {
            if (this.palette[openIndex] == null) break;
        }

        setPaletteColor(openIndex, newColor);

        this.raster[x + y * this.width] = openIndex;
    }

    @Override
    public int getSample(int x, int y, int band) {
        return (this.palette[this.raster[x + y * this.width]].getRGB() >> (16 - band) * 8) & 0xFF;
    }

    @Override
    public void setSample(int x, int y, int band, int sample) {
        // Does this need to act like RGB???
    }
}
