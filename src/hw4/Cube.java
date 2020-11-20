package hw4;

import java.awt.*;

public class Cube implements Comparable<Cube> {
    public int x;
    public int y;
    public int z;
    public int width;
    public int height;
    public int length;

    public Cube() {

    }

    public Cube(int x, int y, int z, int width, int height, int length) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.width = width;
        this.height = height;
        this.length = length;
    }

    public Color toColor() {
        System.out.println((x + width / 2) + ", " + (y + height / 2) + ", " + (z + height / 2));
        return new Color(x + width / 2, y + height / 2, z + height / 2);
    }

    @Override
    public Cube clone() {
        return new Cube(x, y, z, width, height, length);
    }

    public int getLongestLength() {
        return Math.max(width, Math.max(height, length));
    }

    public Cube cut() {
        Cube other;
        int longest = getLongestLength();

        if (longest == height) {
            height /= 2;
            other = clone();
            other.y += height;
        } else if (longest == length) {
            length /= 2;
            other = clone();
            other.x += length;
        } else {
            width /= 2;
            other = clone();
            other.z += width;
        }

        return other;
    }

    public int compareTo(Cube o) {
        return o.getLongestLength() - getLongestLength();
    }

}
