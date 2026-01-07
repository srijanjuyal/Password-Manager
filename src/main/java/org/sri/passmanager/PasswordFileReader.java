package org.sri.passmanager;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PasswordFileReader {

    public static List<PasswordEntry> readAll(String filePath) {

        List<PasswordEntry> entries = new ArrayList<>();

        try (DataInputStream in =
                     new DataInputStream(new FileInputStream(filePath))) {

            while (true) {
                // site
                int siteLen = in.readInt();
                byte[] siteBytes = new byte[siteLen];
                in.readFully(siteBytes);
                String site = new String(siteBytes, StandardCharsets.UTF_8);

                // username
                int userLen = in.readInt();
                byte[] userBytes = new byte[userLen];
                in.readFully(userBytes);
                String username = new String(userBytes, StandardCharsets.UTF_8);

                // encrypted password
                int passLen = in.readInt();
                byte[] encryptedPassword = new byte[passLen];
                in.readFully(encryptedPassword);

                entries.add(
                        new PasswordEntry(site, username, encryptedPassword)
                );
            }

        } catch (EOFException e) {
            // normal end of file
        } catch (IOException e) {
            throw new RuntimeException("Failed to read password file", e);
        }

        return entries;
    }
}