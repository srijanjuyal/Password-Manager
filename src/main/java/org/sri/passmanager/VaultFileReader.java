package org.sri.passmanager;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class VaultFileReader {

    private static final String MAGIC = "VLT1";

    public static VaultData load(String filePath) {

        try (DataInputStream in =
                     new DataInputStream(new FileInputStream(filePath))) {

            // 1. Read and verify magic header
            byte[] magicBytes = new byte[4];
            in.readFully(magicBytes);
            String magic = new String(magicBytes, StandardCharsets.US_ASCII);

            if (!MAGIC.equals(magic)) {
                throw new IllegalStateException("Invalid vault file");
            }

            // 2. Read salt
            int saltLen = in.readInt();
            byte[] salt = new byte[saltLen];
            in.readFully(salt);

            // 3. Read verification IV
            int ivLen = in.readInt();
            byte[] iv = new byte[ivLen];
            in.readFully(iv);

            // 4. Read verification ciphertext
            int ctLen = in.readInt();
            byte[] ciphertext = new byte[ctLen];
            in.readFully(ciphertext);

            return new VaultData(salt, iv, ciphertext);

        } catch (IOException e) {
            throw new RuntimeException("Failed to read vault file", e);
        }
    }
}
