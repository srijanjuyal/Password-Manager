package org.sri.passmanager;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class VaultStoreReader {

    private static final String MAGIC = "VLT2";

    public static VaultStore load(String filePath) {

        try (DataInputStream in =
                     new DataInputStream(new FileInputStream(filePath))) {

            // ===== HEADER =====
            byte[] magicBytes = new byte[4];
            in.readFully(magicBytes);
            String magic = new String(magicBytes, StandardCharsets.US_ASCII);

            // Read and verify magic header
            if (!MAGIC.equals(magic)) {
                throw new IllegalStateException("Invalid vault file");
            }

            // ===== VAULT METADATA =====
            // Read salt
            int saltLen = in.readInt();
            byte[] salt = new byte[saltLen];
            in.readFully(salt);

            // Read verification IV
            int ivLen = in.readInt();
            byte[] verifyIv = new byte[ivLen];
            in.readFully(verifyIv);

            // Read verification ciphertext
            int ctLen = in.readInt();
            byte[] verifyCiphertext = new byte[ctLen];
            in.readFully(verifyCiphertext);

            VaultData vaultData = new VaultData(salt, verifyIv, verifyCiphertext);

            // ===== PASSWORD ENTRIES =====
            int count = in.readInt();
            List<PasswordEntry> entries = new ArrayList<>();

            for (int i = 0; i < count; i++) {

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

            return new VaultStore(vaultData, entries);

        } catch (Exception e) {
            throw new RuntimeException("Failed to load vault store", e);
        }
    }
}
