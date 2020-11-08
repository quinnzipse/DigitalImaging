package hw3;

import pixeljelly.ops.ConvolutionOp;
import pixeljelly.ops.NullOp;
import pixeljelly.ops.PluggableImageOp;
import pixeljelly.utilities.SeperableKernel;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;

public class GaussianOp extends NullOp implements PluggableImageOp {

    private double alpha;
    private double sigma;

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public double getSigma() {
        return sigma;
    }

    public void setSigma(double sigma) {
        this.sigma = sigma;
    }

    public GaussianOp() {

    }

    public GaussianOp(double alpha, double sigma) {
        this.alpha = alpha;
        this.sigma = sigma;
    }

    @Override
    public BufferedImageOp getDefault(BufferedImage bufferedImage) {
        return new GaussianOp(2, 3);
    }

    @Override
    public String getAuthorName() {
        return "Quinn Zipse";
    }

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dest) {
        if (dest == null) {
            dest = createCompatibleDestImage(src, src.getColorModel());
        }

        float[] coefficients = getGaussianCoefficients(alpha, sigma);
        dest = (new ConvolutionOp(new SeperableKernel(coefficients, coefficients), false)).filter(src, dest);

        return dest;
    }

    private float[] getGaussianCoefficients(double alpha, double sigma) {
        int w = (int) Math.ceil(alpha * sigma);
        float[] result = new float[w * 2 + 1];
        for (int n = 0; n <= w; n++) {
            double coefficient = Math.exp(-(n * n) / (2 * sigma * sigma)) / (Math.sqrt(2 * Math.PI) * sigma);
            result[w + n] = (float) coefficient;
            result[w - n] = (float) coefficient;
        }
        return result;
    }
}
