package org.apache.coyote.page;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class PageMapper {

    private final static String STATIC = "static" + File.separator;
    private static final String START_OF_FILE_DIRECTORY = File.separator;

    public static Path getFilePath(final String url) {
        return getPath(START_OF_FILE_DIRECTORY + url + ".html");
    }

    public static Path getPath(String url) {
        return Paths.get(Objects.requireNonNull(
                PageMapper.class
                .getClassLoader()
                .getResource(STATIC + url))
                .getPath());
    }
}
