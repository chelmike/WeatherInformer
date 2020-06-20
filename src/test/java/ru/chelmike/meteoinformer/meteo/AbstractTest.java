package ru.chelmike.meteoinformer.meteo;

import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * A common ancestor for test classes
 *
 * @author Michael Ostrovsky
 */
public class AbstractTest {
    String readFile(String fileLocation) throws IOException {
        File file = ResourceUtils.getFile(fileLocation);
        return new String(Files.readAllBytes(file.toPath()));
    }
}
