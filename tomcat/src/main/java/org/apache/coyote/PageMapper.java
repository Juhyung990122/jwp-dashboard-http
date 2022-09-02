package org.apache.coyote;

import static org.apache.coyote.FileName.NOT_FOUND;
import static org.apache.coyote.FileName.findFileName;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class PageMapper {

    private static String STATIC = "static/";

    public String makeResponseBody(final String url) {
        if (isCustomFileRequest(url)) {
            try {
                String fileName = FileName.findFileName(url).getFileName();
                return readFile(getFilePath(fileName));
            } catch (URISyntaxException | IOException e) {
                throw new IllegalArgumentException("파일을 찾을 수 없습니다.");
            }
        }

        if(isFileRequest(url)){
            try {
                final String filePath = getFilePath(url);
                return readFile(filePath);
            } catch (URISyntaxException | IOException e) {
                throw new IllegalArgumentException("파일을 찾을 수 없습니다.");
            }
        }


        return "Hello world!";
    }

    public static boolean isCustomFileRequest(final String url){
        final FileName foundFileName = findFileName(url);
        return !foundFileName.getFileName().equals("") && !foundFileName.equals(NOT_FOUND);
    }

    private boolean isFileRequest(String url) {
        return !url.equals("/");
    }

    private String readFile(final String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }

    private String getFilePath(final String fileName) throws URISyntaxException {
        return Objects.requireNonNull(
                getClass()
                .getClassLoader()
                .getResource(STATIC + fileName))
                .getPath();
    }

}
