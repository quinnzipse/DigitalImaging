package hw1;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class DigitalImageIO {
    public enum ImageType {INDEXED, PACKED, LINEAR_ARRAY, MULTIDIM_ARRAY}

    public static DigitalImage read(File file, ImageType type) throws IOException, IllegalFileFormatException {
        var scanner = new Scanner(file);

        var magicNumber = scanner.next();
        if (!magicNumber.equals("P3")) {
            throw new IllegalFileFormatException();
        }

        var width = scanner.nextInt();
        var height = scanner.nextInt();

        // ?????? No one knows on this one.
        var maxval = scanner.nextInt();

        DigitalImage outputImage;
        switch (type) {
            case INDEXED:
                outputImage = new IndexedDigitalImage(width, height);
                break;
            case LINEAR_ARRAY:
                outputImage = new LinearArrayDigitalImage(width, height, 3);
                break;
            case MULTIDIM_ARRAY:
                outputImage = new ArrayDigitalImage(width, height, 3);
                break;
            case PACKED:
                outputImage = new PackedPixelImage(width, height, 3);
                break;
            default:
                throw new IllegalFileFormatException();
        }

        // Read in all the samples.
        int x = 0;
        while (scanner.hasNextInt()) {
            var sample = scanner.nextInt();
            outputImage.setSample(x, x / 3, x % 3, sample);
        }

        scanner.close();

        return outputImage;
    }

    public static void write(File file, DigitalImage image) throws IOException {
        var fileWriter = new FileWriter(file);

        var sb = new StringBuilder();
        sb.append("P3\n");
        sb.append(image.getWidth());
        sb.append("\n");
        sb.append(image.getHeight());
        sb.append("\n");
        // This is the maxVal thing that know one understands.
        sb.append(255);

        // Write out all the samples.
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                for (int b = 0; b < image.getBands(); b++) {
                    sb.append(image.getSample(x, y, b));
                    sb.append(" ");
                }
                sb.append("\n");
            }
        }

        fileWriter.write(sb.toString());

        fileWriter.flush();
        fileWriter.close();
    }
}
