package services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ImageHandler {

    public static long operateOnTempFile(File file) throws IOException {
        return Files.size(file.toPath());
        //Files.deleteIfExists(file.toPath());
    }
}
