package org.sri.passmanager;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class VaultFileWriter {

    private static final String MAGIC = "VLT1";

    public static void save(VaultData data, String filePath) {

        try (DataOutputStream out =
                     new DataOutputStream(new FileOutputStream(filePath))) {

            // 1. Write magic header
            out.write(MAGIC.getBytes(StandardCharsets.US_ASCII));

            // 2. Write salt
            out.writeInt(data.salt().length);
            out.write(data.salt());

            // 3. Write verification IV
            out.writeInt(data.verifyIv().length);
            out.write(data.verifyIv());

            // 4. Write verification ciphertext
            out.writeInt(data.verifyCiphertext().length);
            out.write(data.verifyCiphertext());

            out.flush();

        } catch (IOException e) {
            throw new RuntimeException("Failed to save vault file", e);
        }
    }
}
