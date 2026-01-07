package org.sri.passmanager;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
//import javax.crypto.spec.PBEKeySpec; // remove if not running
import java.util.Arrays;

public class VaultCreator {

    // Adjust if you want; higher = slower login, better protection
    private static final int ITERATIONS = 1048576;
    private static final int KEY_SIZE = 256;

    public static VaultData createVault(char[] masterPassword) {

        try {
            // 1. Generate random salt (stored, not secret)
            byte[] salt = new byte[16];
            SecureRandom.getInstanceStrong().nextBytes(salt);

            // 2. Derive encryption key from master password
            PBEKeySpec spec = new PBEKeySpec(
                    masterPassword,
                    salt,
                    ITERATIONS,
                    KEY_SIZE
            );

            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            byte[] encryptionKey =
                    factory.generateSecret(spec).getEncoded();

            // wipe password ASAP
            Arrays.fill(masterPassword, '\0');

            // 3. Encrypt verification string "VAULT_OK"
            byte[] iv = new byte[12];
            SecureRandom.getInstanceStrong().nextBytes(iv);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(
                    Cipher.ENCRYPT_MODE,
                    new SecretKeySpec(encryptionKey, "AES"),
                    new GCMParameterSpec(128, iv)
            );

            byte[] ciphertext = cipher.doFinal(
                    "VAULT_OK".getBytes(StandardCharsets.UTF_8)
            );

            // wipe key from memory after use
            Arrays.fill(encryptionKey, (byte) 0);

            // 4. Return everything that must be stored
            return new VaultData(salt, iv, ciphertext);

        } catch (Exception e) {
            throw new RuntimeException("Vault creation failed", e);
        }
    }
}