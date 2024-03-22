package com.moviesbattle;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import static org.apache.commons.io.FileUtils.copyInputStreamToFile;

public class JsonUtils {

    public static String getMoviesJson() throws IOException {
        final InputStream moviesFile = JsonUtils.class.getClassLoader().getResourceAsStream("movies.json");
        final File file = new File("copy_movies.json");
        copyInputStreamToFile(moviesFile, file);

        try (FileReader reader = new FileReader(file)) {
            int charCount;
            StringBuilder content = new StringBuilder();
            while ((charCount = reader.read()) != -1) {
                content.append((char) charCount);
            }
            return content.toString();
        } catch (final IOException exception) {
            return null;
        } finally {
            file.delete();
        }
    }

}
