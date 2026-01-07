package org.sri.passmanager;

public record VaultData(
        byte[] salt,
        byte[] verifyIv,
        byte[] verifyCiphertext
) {
}
