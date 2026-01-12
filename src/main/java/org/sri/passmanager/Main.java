package org.sri.passmanager;

import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.File;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    private static final String VAULT_FILE = "vault.bin";

    private static byte[] encryptionKey = null; // in-memory session key
    private static VaultStore vaultStore = null;    // in-memory vault

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== PASSWORD MANAGER ===");
            System.out.println("1. Set / Create Master Password");
            System.out.println("2. Login");
            System.out.println("3. Add New Password");
            System.out.println("4. Read Stored Passwords");
            System.out.println("5. Exit");
            System.out.print("Choose option: ");

            String choice = scanner.nextLine();

            switch (choice) {

                case "1" -> createVault(scanner);
                case "2" -> login(scanner);
                case "3" -> addPassword(scanner);
                case "4" -> readPasswords();
                case "5" -> exitProgram();
                default -> System.out.println("Invalid option");
            }
        }
    }

    // ===== OPTION 1 =====
    private static void createVault(Scanner scanner) {
        if (new File(VAULT_FILE).exists()) {
            System.out.println("Vault already exists.");
            return;
        }

        System.out.print("Create master password: ");
        char[] masterPassword = scanner.nextLine().toCharArray();

        VaultData vaultData = VaultCreator.createVault(masterPassword);
        List<PasswordEntry> emptyEntries = new ArrayList<>();

        vaultStore = new VaultStore(vaultData, emptyEntries);
        VaultStoreWriter.save(VAULT_FILE, vaultData, emptyEntries);

        System.out.println("Vault created successfully.");
    }

    // ===== OPTION 2 =====
    private static void login(Scanner scanner) {
        if (!new File(VAULT_FILE).exists()) {
            System.out.println("No vault found. Create one first.");
            return;
        }

        if (encryptionKey != null) {
            System.out.println("Already logged in.");
            return;
        }

        vaultStore = VaultStoreReader.load(VAULT_FILE);

        System.out.print("Enter master password: ");
        char[] password = scanner.nextLine().toCharArray();

        try {
            encryptionKey = VaultLogin.login(
                    password,
                    vaultStore.getVaultData().salt(),
                    vaultStore.getVaultData().verifyIv(),
                    vaultStore.getVaultData().verifyCiphertext()
            );

            System.out.println("Login successful.");

        } catch (SecurityException e) {
            System.out.println("Wrong master password.");
            vaultStore = null;
        } finally {
            Arrays.fill(password, '\0');
        }
    }

    // ===== OPTION 3 =====
    private static void addPassword(Scanner scanner) {
        if (encryptionKey == null || vaultStore == null) {
            System.out.println("Login first.");
            return;
        }

        System.out.print("Site: ");
        String site = scanner.nextLine();

        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.print("Password: ");
        char[] passwordChars = scanner.nextLine().toCharArray();

        byte[] passwordBytes = new String(passwordChars).getBytes();

        byte[] encrypted =
                AESEncryption.encrypt(passwordBytes, encryptionKey);

        // wipe plaintext
        Arrays.fill(passwordChars, '\0');
        Arrays.fill(passwordBytes, (byte) 0);

        PasswordEntry entry =
                new PasswordEntry(site, username, encrypted);

        vaultStore.getEntries().add(entry);

        // persist entire vault
        VaultStoreWriter.save(
                VAULT_FILE,
                vaultStore.getVaultData(),
                vaultStore.getEntries()
        );

        System.out.println("Password stored securely.");
    }

    // ===== OPTION 4 =====
    private static void readPasswords() {
        if (encryptionKey == null || vaultStore == null) {
            System.out.println("Login first.");
            return;
        }

        if (vaultStore.getEntries().isEmpty()) {
            System.out.println("No stored passwords.");
            return;
        }

        for (PasswordEntry entry : vaultStore.getEntries()) {
            byte[] decrypted =
                    AESEncryption.decrypt(
                            entry.getEncryptedPassword(), encryptionKey);

            String password = new String(decrypted);

            System.out.println(
                    entry.getSite() + " | " +
                            entry.getUsername() + " | " +
                            password
            );

            Arrays.fill(decrypted, (byte) 0);
        }
    }

    // ===== OPTION 5 =====
    private static void exitProgram() {
        if (encryptionKey != null) {
            Arrays.fill(encryptionKey, (byte) 0);
        }
        System.out.println("Exiting. Encryption key wiped.");
        System.exit(0);
    }
}