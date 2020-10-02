package hw1;

import java.awt.*;
import java.util.HashMap;

/**
 * @author Quinn Zipse
 */
public class IndexedDigitalImage implements DigitalImage {

    // 8-bit color depth means 255 colors on the palette
    private final Color[] palette = new Color[256];
    private final int height, width;
    private final byte[] raster;

    IndexedDigitalImage(int width, int height) {
        this.width = width;
        this.height = height;
        this.raster = new byte[width * height];
        System.arraycopy(IndexedDigitalImage.generatePalette(), 0, this.palette, 0, 256);
    }

    IndexedDigitalImage(int width, int height, Color[] palette) {
        this(width, height);
        System.arraycopy(palette, 0, this.palette, 0, palette.length);
    }

    public static Color[] generatePalette() {
        // Generates the best palette in the world.
        var palette = new Color[256];
        int[] r = {0x0, 0x24, 0x49, 0x6D, 0x92, 0xB6, 0xdb, 0xff};
        int[] g = {0x0, 0x24, 0x49, 0x6D, 0x92, 0xB6, 0xdb, 0xff};
        int[] b = {0x0, 0x49, 0x92, 0xdb};

        int i = 0;
        for (int red : r) {
            for (int green : g) {
                for (int blue : b) {
                    palette[i] = new Color(red << 16 | green << 8 | blue);
                    i++;
                }
            }
        }

        return palette;
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
        return 3;
    }

    @Override
    public int[] getPixel(int x, int y) {
        int index = this.raster[x + y * width];
        if (index < 0) index += 256;

        var color = this.palette[index];

        return new int[]{color.getRed(), color.getGreen(), color.getBlue()};
    }

    @Override
    public void setPixel(int x, int y, int[] pixel) {
        var newColor = new Color(pixel[0], pixel[1], pixel[2]);

        for (int i = 0; i < this.palette.length; i++) {
            if (palette[i] != null && palette[i].equals(newColor)) {
                this.raster[x + y * this.width] = (byte) i;
                return;
            }
        }

        var palletIndex = addColorToPallet(newColor);

        this.raster[x + y * this.width] = palletIndex;
    }

    private byte addColorToPallet(Color newColor) {
        // if the color isn't in the pallet add it.
        int openIndex;
        for (openIndex = 0; openIndex < palette.length; openIndex++) {
            if (palette[openIndex] == null) break;
        }

        if (openIndex < palette.length) {
            setPaletteColor(openIndex, newColor);
        } else {
            int min = Integer.MAX_VALUE;
            int minIndex = -1;
            // No more room in the palette.
            for (int i = 0; i < palette.length; i++) {
                int diff = 0;

                diff += Math.abs(newColor.getBlue() - palette[i].getBlue());
                diff += Math.abs(newColor.getRed() - palette[i].getRed());
                diff += Math.abs(newColor.getGreen() - palette[i].getGreen());

                if (diff < min) {
                    min = diff;
                    minIndex = i;
                }
            }
            var oldColor = palette[minIndex];
            palette[minIndex] = new Color((newColor.getRed() + oldColor.getRed()) / 2,
                    (newColor.getGreen() + oldColor.getGreen()) / 2,
                    (newColor.getBlue() + oldColor.getBlue()) / 2);

            return (byte) minIndex;
        }

        return (byte) openIndex;
    }

    @Override
    public int getSample(int x, int y, int band) {
        return (this.palette[this.raster[x + y * this.width]].getRGB() >> (16 - band) * 8) & 0xFF;
    }

    @Override
    /*
     * Could possibly affect multiple pixels.
     */
    public void setSample(int x, int y, int band, int sample) {
        // Get the rgb value and the index in the palette.
        int paletteIndex = this.raster[x + y * this.width],
                rgb = this.palette[paletteIndex].getRGB();

        // Bit mask the sample into the correct bit locations.
        rgb = (rgb & ~(0xff << 16 - band * 8)) | ((sample & 0xff) << 16 - band * 8);

        // Create the new color and put it in the palette.
        this.palette[paletteIndex] = new Color(rgb);
    }
}
