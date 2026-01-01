package org.sri.moviebooking;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.KeySpec;
import java.util.Base64;

public class AESEncryption {

    private final String SALT = "SomeRandomSalt";

    public String encrypt(String plaintext, String SECRET_KEY) {
        try {
            // 1. Generate random IV (12 bytes for GCM)
            byte[] iv = new byte[12];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);

            // 2. Derive AES key using PBKDF2
            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            KeySpec spec = new PBEKeySpec(
                    SECRET_KEY.toCharArray(),
                    SALT.getBytes(StandardCharsets.UTF_8),
                    1048576,
                    256
            );

            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey =
                    new SecretKeySpec(tmp.getEncoded(), "AES");

            // 3. Initialize AES-GCM cipher
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec);

            // 4. Encrypt data
            byte[] ciphertext = cipher.doFinal(
                    plaintext.getBytes(StandardCharsets.UTF_8)
            );

            // 5. Concatenate IV + ciphertext
            ByteBuffer buffer = ByteBuffer.allocate(iv.length + ciphertext.length);
            buffer.put(iv);
            buffer.put(ciphertext);

            // 6. Base64 encode result
            return Base64.getEncoder().encodeToString(buffer.array());

        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    public String decrypt(String encryptedText, String SECRET_KEY) {
        try {
            // 1. Base64 decode
            byte[] decoded = Base64.getDecoder().decode(encryptedText);

            // 2. Extract IV (first 12 bytes)
            ByteBuffer buffer = ByteBuffer.wrap(decoded);
            byte[] iv = new byte[12];
            buffer.get(iv);

            // 3. Extract ciphertext + auth tag
            byte[] ciphertext = new byte[buffer.remaining()];
            buffer.get(ciphertext);

            // 4. Re-derive AES key using PBKDF2
            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            KeySpec spec = new PBEKeySpec(
                    SECRET_KEY.toCharArray(),
                    SALT.getBytes(StandardCharsets.UTF_8),
                    1048576, // MUST match encryption
                    256
            );

            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey =
                    new SecretKeySpec(tmp.getEncoded(), "AES");

            // 5. Initialize AES-GCM for decryption
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec);

            // 6. Decrypt and authenticate
            byte[] plaintext = cipher.doFinal(ciphertext);

            return new String(plaintext, StandardCharsets.UTF_8);

        } catch (AEADBadTagException e) {
            // Data was tampered with or wrong key/IV
            throw new SecurityException("Data integrity check failed", e);
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed", e);
        }
    }
}
