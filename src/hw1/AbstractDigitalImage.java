package hw1;

public abstract class AbstractDigitalImage {
    protected int width, height, bands;

    public AbstractDigitalImage(int w, int h, int b) {
        width = w;
        height = h;
        bands = b;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getBands() {
        return bands;
    }
}
