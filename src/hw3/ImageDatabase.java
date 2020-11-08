package hw3;

import org.ujmp.core.DenseMatrix;
import org.ujmp.core.Matrix;
import org.ujmp.core.collections.composite.SortedListSet;
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
                createDatabase(Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]), args[4], args[5]);
                break;
            case "query":
                // java ImageDatabase query https://charity.cs.uwlax.edu/views/cs454/homeworks/hw3/red-frog.png db-1-1-1.txt query-result-1-1-1-rgb.html 150
                if (args.length != 5)
                    throw new IllegalArgumentException("Invalid number of arguments. Expected 7 got " + args.length);
                compare(args[1], args[2], args[3], Integer.parseInt(args[4]));
        }
    }

    private final int xN;
    private final int yN;
    private final int zN; //rgb num bands.
    private final int bins;
    private final Matrix similarityMatrix;

    ImageDatabase(int xN, int yN, int zN) {
        this.xN = (int) Math.pow(2, xN);
        this.yN = (int) Math.pow(2, yN);
        this.zN = (int) Math.pow(2, zN);
        this.bins = this.xN * this.yN * this.zN;

        similarityMatrix = (new SimilarityMatrix(xN, yN, zN)).getMatrix();
    }

    public double[] makeColorHistogram(BufferedImage src) {

        double[] histogram = new double[bins];

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

        // Normalize the histogram
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

                double[] histogram = database.makeColorHistogram(img);

                StringBuilder sb = new StringBuilder();
                sb.append(creator);
                sb.append(thumbnail);
                sb.append(picURL.getPath());
                sb.append(" ");

                for (double h : histogram) {
                    if (h == 0) sb.append("0");
                    else sb.append(String.format("%.15f", h));
                    sb.append(" ");
                }

                sb.append("\n");

                writer.write(sb.toString());
                writer.flush();
            } catch (Exception e) {
                System.out.println("Skipping Image! Reason: " + e.getMessage());
            }
        }

        writer.close();
    }

    public static void compare(String imgURL, String src, String dest, int limit) throws IOException {
        SortedListSet<ImageSimilarity> similarityList = new SortedListSet<>();
        Scanner in = new Scanner(new File(src));
        FileWriter out = new FileWriter(dest);

        // Read in the bands.
        int rBins = in.nextInt();
        int gBins = in.nextInt();
        int bBins = in.nextInt();
        ImageDatabase imageProps = new ImageDatabase(rBins, gBins, bBins);

        // Get the image from the internet.
        URL url = new URL(imgURL);
        BufferedImage img = ImageIO.read(url);

        // Make the image to compare into a color histogram!
        double[] srcHisto = imageProps.makeColorHistogram(img);

        // convert the histogram to a matrix.
        Matrix h1 = createNx1(imageProps.bins, srcHisto);

        // While we have more images...
        while (in.hasNext()) {
            // Get the information.
            String creator = in.next();
            String thumbnail = in.next();
            String imageURL = in.next();

            // Create the matrix for the specific histogram!
            Matrix h2 = DenseMatrix.Factory.zeros(imageProps.bins, 1);

            for (int i = 0; i < imageProps.bins; i++) {
                h2.setAsFloat(in.nextFloat(), i, 0);
            }

            Matrix hdiff = h1.minus(h2);

            // Calculate the similarity between the images.
            Matrix outMatrix = imageProps.similarityMatrix.mtimes(hdiff).mtimes(hdiff.transpose());

            // This is the distance!
            double diff = Math.abs(outMatrix.toDoubleArray()[0][0]);

            similarityList.add(new ImageSimilarity(diff, creator, thumbnail, imageURL));
        }

        out.write("<html>\n<head>\n<title>Pictures!</title>\n<link href=\"style.css\" rel=\"stylesheet\">\n</head>\n<body>");
        out.write("<img class=\"query\" src=\"" + imgURL + "\">");
        for (ImageSimilarity similarity : similarityList.subList(0, limit)) {
            out.write("<div class=\"img\"><a href=\"" + similarity.author + "\" " +
                    "class=\"flickr\"></a><a href=\"" + similarity.image + "\">" +
                    "<img src=\"" + similarity.thumbnail + "\"></a>" +
                    "<div class=\"distance\">" + String.format("%.6f", similarity.distance) + "</div></div>");
        }
        out.write("</body></html>");
        out.flush();
        out.close();
        in.close();
    }

    private static Matrix createNx1(int n, double[] srcHistogram) {
        Matrix h = DenseMatrix.Factory.zeros(n, 1);

        for (int i = 0; i < srcHistogram.length; i++) {
            h.setAsDouble(srcHistogram[i], i, 0);
        }

        return h;
    }

    private static class ImageSimilarity implements Comparable<ImageSimilarity> {
        private double distance;
        private final String author;
        private final String thumbnail;
        private final String image;

        public ImageSimilarity(String author, String thumbnail, String image) {
            this(0, author, thumbnail, image);
        }

        public ImageSimilarity(double distance, String author, String thumbnail, String image) {
            this.author = author;
            this.distance = distance;
            this.thumbnail = thumbnail;
            this.image = image;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }

        @Override
        public int compareTo(ImageSimilarity o) {
            if (distance - o.distance < 0) return -1;
            else if (distance - o.distance == 0) return 0;
            return 1;
        }
    }
}
