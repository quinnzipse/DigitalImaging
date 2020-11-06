package hw3;

import org.ujmp.core.Matrix;

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
//        for (int i = 0; i < a.length; i++) {
//            // Rc = R'(256/Nr)+128/Nr
//
//            Color Ci = new Color(); // The color at the center of H[i]
//            Color Cj = new Color(3, 3, 3); // The color at the center of H[j]
//            a[r][g] = 1; // 1 - dist(C1, C2) / Max(A[i][j])
//        }
        return a;
    }

    public Matrix getMatrix() {
        return Matrix.Factory.importFromArray(matrix);
    }
}
