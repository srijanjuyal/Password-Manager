package org.sri.passmanager;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class PasswordFileWriter {

    public static void append(String filePath, PasswordEntry entry) {

        try (DataOutputStream out =
                     new DataOutputStream(new FileOutputStream(filePath, true))) {

            // site
            byte[] siteBytes = entry.getSite().getBytes(StandardCharsets.UTF_8);
            out.writeInt(siteBytes.length);
            out.write(siteBytes);

            // username
            byte[] userBytes = entry.getUsername().getBytes(StandardCharsets.UTF_8);
            out.writeInt(userBytes.length);
            out.write(userBytes);

            // encrypted password
            out.writeInt(entry.getEncryptedPassword().length);
            out.write(entry.getEncryptedPassword());

            out.flush();

        } catch (IOException e) {
            throw new RuntimeException("Failed to store password entry", e);
        }
    }
}