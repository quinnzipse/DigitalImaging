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
                Color c1 = new Color(i / (gn * bn), i / bn % gn, i % bn);
                Color c2 = new Color(j / (gn * bn), j / bn % gn, j % bn);
            }
        }
        return a;
    }

    public Matrix getMatrix() {
        return Matrix.Factory.importFromArray(matrix);
    }
}
