package hw3;

import org.ujmp.core.DenseMatrix;
import org.ujmp.core.Matrix;
import pixeljelly.scanners.Location;
import pixeljelly.scanners.RasterScanner;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;


public class ImageDatabase {
    public static void main(String[] args) throws Exception {
        if (args.length < 1) throw new IllegalArgumentException("Invalid number of arguments.");
        switch (args[0].toLowerCase()) {
            case "create":
                if (args.length != 7)
                    throw new IllegalArgumentException("Invalid number of arguments. Expected 7 got " + args.length);
//                var a = new SimilarityMatrix(Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
                createDatabase(Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]), args[4], args[5]);
                break;
            case "query":
                // java ImageDatabase query https://charity.cs.uwlax.edu/views/cs454/homeworks/hw3/red-frog.png db-1-1-1.txt query-result-1-1-1-rgb.html 150
                if (args.length != 5)
                    throw new IllegalArgumentException("Invalid number of arguments. Expected 7 got " + args.length);
                compare(args[1], args[2], args[3], Integer.parseInt(args[4]));
        }
    }

    private int xN, yN, zN; //rgb num bands.
    private SimilarityMatrix similarityMatrix;

    ImageDatabase(int xN, int yN, int zN) {
        this.xN = xN;
        this.yN = yN;
        this.zN = zN;

        similarityMatrix = new SimilarityMatrix(xN, yN, zN);
    }

    public int getNumBins() {
        return xN * yN * zN;
    }

    public float[] makeColorHistogram(BufferedImage src) {

        float[] histogram = new float[xN * yN * zN];

        WritableRaster raster = src.getRaster();

        // Iterate through each pixel and increment the corresponding histogram bin.
        for (Location pt : new RasterScanner(src, false)) {
            int red = raster.getSample(pt.col, pt.row, 0);
            int green = raster.getSample(pt.col, pt.row, 1);
            int blue = raster.getSample(pt.col, pt.row, 2);

            int xP = (int) Math.floor((red * xN) / 256.0);
            int yP = (int) Math.floor((green * yN) / 256.0);
            int zP = (int) Math.floor((blue * zN) / 256.0);

            histogram[xP * yN * zN + yP * zN + zP]++;
        }

        int totalPixels = src.getWidth() * src.getHeight();
        for (int i = 0; i < histogram.length; i++) {
            histogram[i] = histogram[i] / totalPixels;
        }

        return histogram;
    }

    public static void createDatabase(int xN, int yN, int zN, String srcFile, String destFile) throws IOException {
        FileWriter writer = new FileWriter(destFile);
        ImageDatabase database = new ImageDatabase(xN, yN, zN);

        writer.write(xN + " ");
        writer.write(yN + " ");
        writer.write(zN + "\n");

        Scanner scanner = new Scanner(new File(srcFile));

        while (scanner.hasNext()) {
            // Scan past user and thumbnail.
            String creator = scanner.next() + " ";
            String thumbnail = scanner.next() + " ";

            // Grab the pictures URL and turn it into a buffered image.
            try {
                URL picURL = new URL(scanner.next());
                BufferedImage img = ImageIO.read(picURL);

                float[] histogram = database.makeColorHistogram(img);

                StringBuilder sb = new StringBuilder();
                sb.append(creator);
                sb.append(thumbnail);
                sb.append(picURL.getPath());
                sb.append(" ");

                for (float h : histogram) {
                    sb.append(h);
                    sb.append(" ");
                }

                sb.append("\n");

                System.out.print("Histogram: " + sb.toString());

                writer.write(sb.toString());
                writer.flush();
            } catch (Exception e) {
                System.out.println("Skipping Image! Reason: " + e.getMessage());
            }
        }

        writer.close();
    }

    public static void compare(String imgURL, String src, String dest, int limit) throws IOException {
        Scanner in = new Scanner(new File(src));
        FileWriter out = new FileWriter(dest);

        // Read in the bands.
        ImageDatabase imageProps = new ImageDatabase(in.nextInt(), in.nextInt(), in.nextInt());

        // Get the image from the internet.
        URL url = new URL(imgURL);
        BufferedImage img = ImageIO.read(url);

        float[] srcHisto = imageProps.makeColorHistogram(img);
        Matrix srcMatrix = DenseMatrix.Factory.importFromArray(srcHisto);

        Matrix a = imageProps.similarityMatrix.getMatrix();

        // While we have more images...
        while (in.hasNext()) {
            // Get the information.
            String creator = in.next();
            String thumbnail = in.next();
            String imageURL = in.next();

            int bins = imageProps.getNumBins();
            float[][] histogram = new float[bins][bins];

            for (int i = 0; i < bins; i++) {
                histogram[i] = in.nextFloat();
            }

            // Process it.
            Matrix matrix = DenseMatrix.Factory.importFromArray(histogram);

            Matrix hdiff = srcMatrix.minus(matrix);

            Matrix outMatrix = hdiff.times(hdiff.transpose().times(a));

            float diff = outMatrix.floatValue();

            // Output how similar they are!
            System.out.println(diff);
        }

    }
}
