package org.sri.moviebooking;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class StorageHandler {

    public void storeToFile(
            String encryptedText,
            Path filePath
    ) {
        try {

            // Write encrypted text to secondary storage
            Files.write(
                    filePath,
                    encryptedText.getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE
            );

        } catch (IOException e) {
            throw new RuntimeException("Failed to write encrypted data to storage", e);
        }
    }

    public String readFile(Path filePath) {

        try {
            // Read encrypted Base64 text from file
            String encryptedText = Files.readString(
                    filePath,
                    StandardCharsets.UTF_8
            );

            // return file encrypted text
            return encryptedText;

        } catch (IOException e) {
            throw new RuntimeException("Failed to read encrypted data from storage", e);
        }
    }
}