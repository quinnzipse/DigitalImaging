package hw3;

import java.awt.image.ColorModel;

public class SimilarityMatrix {
    private int rn, gn, bn;
    private ColorModel colorModel;
    private int[][] matrix;

    public SimilarityMatrix(int rn, int gn, int bn, ColorModel colorModel) {
        if (rn + gn + bn > 10) throw new IllegalArgumentException("band resolution must add up to 10 or less.");
        this.rn = rn;
        this.gn = gn;
        this.bn = bn;
        this.colorModel = colorModel;
        this.matrix = generateMatrix();
    }

    private int[][] generateMatrix() {
        int[][] a = new int[rn * gn * bn][rn * gn * bn];
        for (int r = 0; r < rn; r++) {
            for (int g = 0; g < gn; g++) {
                for (int b = 0; b < bn; b++) {
                    // I = r' * (NgNb) + G'Nb + B'
                    int Ci = 0; // The color at the center of H[i]
                    int Cj = 0; // The color at the center of H[j]
                    a[r][g] = 1; // 1 - dist(C1, C2) / Max(A[i][j])
                }
            }
        }
        return a;
    }

    public int[][] getMatrix() {
        return matrix;
    }
}
