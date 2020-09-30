import hw1.DigitalImage;

import java.io.File;
import java.io.IOException;
import java.util.IllegalFormatException;

public class DigitalImageIO {
    public enum ImageType { INDEXED, PACKED, LINEAR_ARRAY, MULTIDIM_ARRAY };
    public static DigitalImage read(File file, ImageType type) throws IOException, IllegalFormatException {
        return null;
    }
    public static void write( File file, DigitalImage image ) throws IOException {

    }
}