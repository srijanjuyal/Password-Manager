package org.sri.passmanager;

import java.nio.file.Path;
import java.util.Scanner;
import java.util.Arrays;
import java.io.File;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    private static final String VAULT_FILE = "vault.dat";

    private String SECRET_KEY;
    private Path filePath;

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        VaultData vaultData;
//        CLI cli = new CLI();

        // ===== CREATE VAULT IF NOT EXISTS =====
        File file = new File(VAULT_FILE);
        if (!file.exists()) {
            System.out.println("No vault found. Creating new vault.");

            System.out.print("Create master password: ");
            char[] masterPassword = scanner.nextLine().toCharArray();

            vaultData = VaultCreator.createVault(masterPassword);
            VaultFileWriter.save(vaultData, VAULT_FILE);

            System.out.println("Vault created and saved.\n");
        }

        // ===== LOAD VAULT =====
        vaultData = VaultFileReader.load(VAULT_FILE);

        // ===== LOGIN =====
        System.out.print("Enter master password to login: ");
        char[] loginPassword = scanner.nextLine().toCharArray();

        byte[] encryptionKey;

        try {
            encryptionKey = VaultLogin.login(
                    loginPassword,
                    vaultData.salt(),
                    vaultData.verifyIv(),
                    vaultData.verifyCiphertext()
            );

            System.out.println("Login successful.");
            System.out.println("Encryption key is now in memory.");

            // ===== NEXT STEPS WILL USE encryptionKey =====
            // encryptPassword(encryptionKey, ...)
            // decryptPassword(encryptionKey, ...)

        } catch (SecurityException e) {
            System.out.println("Login failed: wrong password");
            return;
        } finally {
            Arrays.fill(loginPassword, '\0');
        }

        // ===== LOGOUT =====
        System.out.println("Exiting application.");
        Arrays.fill(encryptionKey, (byte) 0);
        System.out.println("Encryption key wiped from memory.");

    }
}