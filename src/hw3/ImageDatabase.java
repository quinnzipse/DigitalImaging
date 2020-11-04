package hw3;

import jdk.jshell.spi.ExecutionControl;
import pixeljelly.features.Histogram;
import pixeljelly.scanners.Location;
import pixeljelly.scanners.RasterScanner;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.Scanner;

public class ImageDatabase {
    public static void main(String[] args) throws ExecutionControl.NotImplementedException, IOException {
        if (args.length < 1) throw new IllegalArgumentException("Invalid number of arguments.");
        switch (args[0].toLowerCase()) {
            case "create":
                if (args.length != 7)
                    throw new IllegalArgumentException("Invalid number of arguments. Expected 7 got " + args.length);
                var a = new SimilarityMatrix(Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]), ColorModel.getRGBdefault());

                break;
            case "query":
                ImageDatabase imgData = new ImageDatabase(args[3]);
                throw new ExecutionControl.NotImplementedException("This method has yet to be implemented");
                break;
        }
    }

    private int xN, yN, zN; //rgb num bands.
    private ColorModel cm;

    ImageDatabase(String srcFile) throws IOException {
        // TODO: Read from the database file.
        FileReader reader = new FileReader(srcFile);

        reader.close();
    }

    ImageDatabase(ColorModel cm, int xN, int yN, int zN, String destFile) throws IOException {
        // TODO: Create a database file.
        FileWriter writer = new FileWriter(destFile);

        this.cm = cm;
        this.xN = xN;
        this.yN = yN;
        this.zN = zN;

        writer.write(cm.getTransferType() + " ");
        writer.write(xN + " ");
        writer.write(yN + " ");
        writer.write(zN + "\n");

        writer.close();
    }

    public static ImageDatabase createDatabase(int xN, int yN, int zN, String fileName) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(fileName));

        while (scanner.hasNext()) {
            String url = scanner.nextLine();
        }
    }

    public int[] makeColorHistogram(BufferedImage src) throws UnsupportedEncodingException {
        if (!src.getColorModel().equals(ColorModel.getRGBdefault()))
            throw new UnsupportedEncodingException("Image must be RGB");

        int[] histogram = new int[xN * yN * zN];

        WritableRaster raster = src.getRaster();

        // Iterate through each pixel and increment the corresponding histogram bin.
        for (Location pt : new RasterScanner(src, false)) {
            int xP = raster.getSample(pt.col, pt.row, 0) * xN / 256;
            int yP = raster.getSample(pt.col, pt.row, 1) * yN / 256;
            int zP = raster.getSample(pt.col, pt.row, 2) * zN / 256;
            histogram[xP * yN * zN + yP * zN * zP]++;
        }

        return histogram;
    }
}
