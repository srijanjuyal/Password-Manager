package org.sri.passmanager;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.util.List;

public class VaultStoreWriter {

    private static final String MAGIC = "VLT2";

    public static void save(String filePath, VaultData vaultData, List<PasswordEntry> entries) {

        try (DataOutputStream out =
                     new DataOutputStream(new FileOutputStream(filePath))) {

            // ===== HEADER =====
            out.write(MAGIC.getBytes(StandardCharsets.US_ASCII));

            // ===== VAULT METADATA =====
            // Salt
            out.writeInt(vaultData.salt().length);
            out.write(vaultData.salt());

            // Verification IV
            out.writeInt(vaultData.verifyIv().length);
            out.write(vaultData.verifyIv());

            // Verification ciphertext
            out.writeInt(vaultData.verifyCiphertext().length);
            out.write(vaultData.verifyCiphertext());

            // ===== PASSWORD ENTRIES =====
            out.writeInt(entries.size());

            for (PasswordEntry entry : entries) {

                // site
                byte[] site = entry.getSite().getBytes(StandardCharsets.UTF_8);
                out.writeInt(site.length);
                out.write(site);

                // username
                byte[] user = entry.getUsername().getBytes(StandardCharsets.UTF_8);
                out.writeInt(user.length);
                out.write(user);

                // encrypted password
                out.writeInt(entry.getEncryptedPassword().length);
                out.write(entry.getEncryptedPassword());
            }

            out.flush();

        } catch (IOException e) {
            throw new RuntimeException("Failed to save vault store", e);
        }
    }
}
