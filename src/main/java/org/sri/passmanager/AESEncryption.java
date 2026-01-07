package org.sri.passmanager;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;

public class AESEncryption {

    private static final int IV_SIZE = 12;
    private static final int TAG_SIZE = 128;

    // Encrypts arbitrary bytes using AES-GCM
    public static byte[] encrypt(byte[] plaintext, byte[] encryptionKey) {
        try {
            byte[] iv = new byte[IV_SIZE];
            SecureRandom.getInstanceStrong().nextBytes(iv);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(
                    Cipher.ENCRYPT_MODE,
                    new SecretKeySpec(encryptionKey, "AES"),
                    new GCMParameterSpec(TAG_SIZE, iv)
            );

            byte[] ciphertext = cipher.doFinal(plaintext);

            // IV || ciphertext
            ByteBuffer buffer =
                    ByteBuffer.allocate(iv.length + ciphertext.length);
            buffer.put(iv);
            buffer.put(ciphertext);

            return buffer.array();

        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    // Decrypts data encrypted by encrypt()
    public static byte[] decrypt(byte[] encrypted, byte[] encryptionKey) {
        try {
            ByteBuffer buffer = ByteBuffer.wrap(encrypted);

            byte[] iv = new byte[IV_SIZE];
            buffer.get(iv);

            byte[] ciphertext = new byte[buffer.remaining()];
            buffer.get(ciphertext);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(
                    Cipher.DECRYPT_MODE,
                    new SecretKeySpec(encryptionKey, "AES"),
                    new GCMParameterSpec(TAG_SIZE, iv)
            );

            return cipher.doFinal(ciphertext);

        } catch (AEADBadTagException e) {
            throw new SecurityException("Data tampered or wrong key", e);
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed", e);
        }
    }
}
