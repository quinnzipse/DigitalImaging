package hw3;

import org.ujmp.core.Matrix;

import java.awt.*;

public class SimilarityMatrix {
    private final int rn;
    private final int gn;
    private final int bn;
    private final float[][] matrix;

    public SimilarityMatrix(int rn, int gn, int bn) {
        if (rn + gn + bn > 10) throw new IllegalArgumentException("band resolution must add up to 10 or less.");
        this.rn = rn;
        this.gn = gn;
        this.bn = bn;
        this.matrix = generateMatrix();
    }

    private float[][] generateMatrix() {
        float[][] a = new float[rn * gn * bn][rn * gn * bn];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a.length; j++) {
                // get the two colors to compare based on the matrix.
                int ir = i / (gn * bn);
                int ig = i / bn % gn;
                int ib = i % bn;
                Color c1 = new Color(ir * (256 / rn) + 128 / rn, ig * (256 / gn) + 128 / gn, ib * (256 / bn) + 128 / bn);
                Color c2 = new Color(jr * (256 / rn) + 128 / rn, jg * (256 / gn) + 128 / gn, jb * (256 / bn) + 128 / bn);

                // Do we need to find the center of the bin?

                // Calculate the L2 Distance for those two colors!
                int distance = 0;

                // Store that number in the similarity matrix! Should be 0-1
                a[i][j] = distance;
            }
        }
        return a;
    }

    public Matrix getMatrix() {
        return Matrix.Factory.importFromArray(matrix);
    }
}
