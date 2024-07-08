package com.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Files {

    private Files() {
    }

    /**
     * Write content to a File.
     *
     * @param path,    path of the file.
     * @param content, content to add in the file.
     **/
    public static void writeFile(String path, String content, boolean appendMode) throws IOException {

        if (path != null) {

            Path filePath = Paths.get(path);
            File file = new File(filePath.toString());

            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream fos = new FileOutputStream(file,appendMode);
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos))) {

                bw.write(content);
                bw.newLine();
            }
        }
    }
}