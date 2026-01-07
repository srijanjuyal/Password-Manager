package org.sri.passmanager;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class VaultLogin {

    private static final int ITERATIONS = 1048576;
    private static final int KEY_SIZE = 256;

    public static byte[] login(
            char[] masterPassword,
            byte[] salt,
            byte[] verifyIv,
            byte[] verifyCiphertext
    ) {

        try {
            // 1. Derive encryption key from master password
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

            // wipe password immediately
            Arrays.fill(masterPassword, '\0');

            // 2. Decrypt verification marker
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(
                    Cipher.DECRYPT_MODE,
                    new SecretKeySpec(encryptionKey, "AES"),
                    new GCMParameterSpec(128, verifyIv)
            );

            byte[] plaintext = cipher.doFinal(verifyCiphertext);
            String result = new String(plaintext, StandardCharsets.UTF_8);

            // 3. Check marker
            if (!"VAULT_OK".equals(result)) {
                Arrays.fill(encryptionKey, (byte) 0);
                throw new SecurityException("Wrong master password");
            }

            // SUCCESS → keep encryptionKey in memory
            return encryptionKey;

        } catch (AEADBadTagException e) {
            // authentication failed (wrong password or tampered data)
            throw new SecurityException("Wrong master password", e);

        } catch (Exception e) {
            throw new RuntimeException("Login failed", e);
        }
    }
}

