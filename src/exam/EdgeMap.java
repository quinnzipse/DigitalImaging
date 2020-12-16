package exam;

import pixeljelly.scanners.Location;
import pixeljelly.scanners.RasterScanner;

import java.awt.image.BufferedImage;

public class EdgeMap {

    private final BufferedImage img;
    private final int[][] energy;
    private int max = 0;

    public EdgeMap(BufferedImage img) {
        this.img = img;
        this.energy = new int[img.getWidth()][img.getHeight()];
        initEnergy();
    }

    public BufferedImage toImg() {
        BufferedImage out = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());

        for (Location pt : new RasterScanner(img, false)) {
            if (pt.col == 0) {
                System.out.println();
            }
            System.out.print(energy[pt.col][pt.row] + ", ");
            out.getRaster().setSample(pt.col, pt.row, 0, (int) (energy[pt.col][pt.row] / (255.0 / max)));
        }

        return out;
    }

    private void initEnergy() {

        // Initialize the destination img by copying the bottom row.
        for (int x = 0; x < img.getWidth(); x++) {
            energy[x][img.getHeight() - 1] = img.getRaster().getSample(x, img.getHeight() - 1, 0);
        }

        // Iterate through the image from bottom to top.
        for (int y = img.getHeight() - 2; y >= 0; y--) {
            for (int x = 0; x < img.getWidth(); x++) {
                int greyVal = img.getRaster().getSample(x, y, 0);

                energy[x][y] = greyVal + findBestPath(x, y);
            }
        }
    }

    private int findBestPath(int x, int y) {

        int left = getEnergy(x - 1, y + 1);
        int right = getEnergy(x + 1, y + 1);
        int min = getEnergy(x, y + 1);

        max = Math.max(left, max);
        max = Math.max(right, max);
        max = Math.max(min, max);

        if (left < min && left != -1) {
            min = left;
        } else if (right < min && right != -1) {
            min = right;
        }

        return min;
    }

    private int getEnergy(int x, int y) {
        if (x < img.getWidth() && x >= 0) {
            return energy[x][y];
        }

        return -1;
    }
}
