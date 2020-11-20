package hw4;

import pixeljelly.features.Histogram;
import pixeljelly.ops.NullOp;
import pixeljelly.ops.PluggableImageOp;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.util.Arrays;
import java.util.PriorityQueue;

public class DitherOp extends NullOp implements PluggableImageOp {

    public enum Type {STUCKI, JARVIS, FLOYD_STEINBURG, SIERRA, SIERRA_2_4A}

    private Type type;
    private Color[] palette;
    private int paletteSize;

    public DitherOp() {
    }

    public DitherOp(Type type, int paletteSize) {
        this.type = type;
        this.paletteSize = paletteSize;
    }

    public DitherOp(Type type, Color[] palette) {
        this.type = type;
        this.palette = palette;
    }

    private Color[] medianCut(int size, BufferedImage src) {
        // Get the bounding box around the histograms
        Cube c = getSmallestBox(src);
        PriorityQueue<Cube> pq = new PriorityQueue<>();
        pq.add(c);

        while (pq.size() != size) {
            Cube cube = pq.remove();
            Cube cube2 = cube.cut();

            pq.add(cube);
            pq.add(cube2);
        }

        return cubesToColors(pq.toArray(Cube[]::new));
    }

    private Color[] cubesToColors(Cube[] cubes) {
        return Arrays.stream(cubes).map(Cube::toColor).toArray(Color[]::new);
    }

    private Cube getSmallestBox(BufferedImage img) {
        Histogram r = new Histogram(img, 0);
        Histogram g = new Histogram(img, 1);
        Histogram b = new Histogram(img, 2);

        Cube cube = new Cube();

        cube.width = r.getMaxValue() - r.getMinValue();
        cube.x = r.getMinValue();
        cube.height = g.getMaxValue() - g.getMinValue();
        cube.y = g.getMinValue();
        cube.length = b.getMaxValue() - b.getMinValue();
        cube.z = b.getMinValue();

        return cube;
    }

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dest) {
        if (dest == null) {
            dest = createCompatibleDestImage(src, src.getColorModel());
        }

        this.palette = medianCut(paletteSize, src);

        for (Color c : palette) {
            System.out.println(c.getRed() + ", " + c.getGreen() + ", " + c.getBlue());
        }

        return dest;
    }

    @Override
    public BufferedImageOp getDefault(BufferedImage bufferedImage) {
        return new DitherOp(Type.JARVIS, 16);
    }

    @Override
    public String getAuthorName() {
        return "Quinn Zipse";
    }
}