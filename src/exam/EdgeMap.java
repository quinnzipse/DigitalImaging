package exam;

import pixeljelly.scanners.Location;
import pixeljelly.scanners.RasterScanner;

import java.awt.image.BufferedImage;

public class EdgeMap {

    private BufferedImage src;
    private final BufferedImage edges;
    private int[][] energy;
    private int max = 0;

    public EdgeMap(BufferedImage srcImg) {
        this.src = srcImg;
        this.energy = new int[srcImg.getWidth()][srcImg.getHeight()];
        this.edges = new EdgeDetectionOp().filter(srcImg, null);
        initEnergy();
    }

    public BufferedImage getImg() {
        return src;
    }

    public BufferedImage getEdges(){
        return edges;
    }

    public BufferedImage getEnergyImg() {
        BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

        for (Location pt : new RasterScanner(src, false)) {
            out.getRaster().setSample(pt.col, pt.row, 0, (int) (energy[pt.col][pt.row] / (max / 255.0)));
        }

        return out;
    }

    private void initEnergy() {
        // Initialize the destination img by copying the bottom row.
        for (int x = 0; x < edges.getWidth(); x++) {
            energy[x][edges.getHeight() - 1] = edges.getRaster().getSample(x, edges.getHeight() - 1, 0);
        }

        // Iterate through the image from bottom to top.
        for (int y = edges.getHeight() - 2; y >= 0; y--) {
            for (int x = 0; x < edges.getWidth(); x++) {
                int greyVal = edges.getRaster().getSample(x, y, 0);

                energy[x][y] = greyVal + energy[findBestPath(x, y)][y + 1];
            }
        }
    }

    private int findBestPath(int x, int y) {

        int left = getEnergy(x - 1, y + 1);
        int right = getEnergy(x + 1, y + 1);
        int min = getEnergy(x, y + 1);
        int minIndex = x;

        max = Math.max(left, max);
        max = Math.max(right, max);
        max = Math.max(min, max);

        if (left < min && left != -1) {
            min = left;
            minIndex = x - 1;
        }
        if (right < min && right != -1) {
            minIndex = x + 1;
        }

        return minIndex;
    }

    public void deletePath(int[] path) {
        BufferedImage dest = new BufferedImage(src.getWidth() - 1, src.getHeight(), src.getType());
        int[][] newEnergy = new int[energy.length - 1][energy[0].length];

        for (Location pt : new RasterScanner(dest, false)) {

            if (path[pt.row] == pt.col) continue;

            // If the location is after the removal, shift over one.
            int offset = path[pt.row] < pt.col ? 1 : 0;

            newEnergy[pt.col - offset][pt.row] = energy[pt.col][pt.row];

            dest.setRGB(pt.col - offset, pt.row, src.getRGB(pt.col, pt.row));
        }

        energy = newEnergy;
        src = dest;
    }

    public int[] findPath(int[] path) {
        if (path == null || path.length != src.getHeight()) {
            path = new int[src.getHeight()];
        }

        path[0] = getStartingPoint();
        int y = 1;

        while (y < src.getHeight()) {
            path[y] = findBestPath(path[y - 1], y - 1);
            y++;
        }

        return path;
    }

    private int getStartingPoint() {
        int minIndex = 0,
                minValue = energy[0][0],
                val;

        for (int x = 1; x < src.getWidth(); x++) {
            val = energy[x][0];
            if (val < minValue) {
                minIndex = x;
                minValue = val;
            }
        }

        return minIndex;
    }

    private int getEnergy(int x, int y) {
        if (x < energy.length && x >= 0) {
            return energy[x][y];
        }

        return -1;
    }
}
