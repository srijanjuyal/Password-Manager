# Java Password Manager (Local, Encrypted)

A **local, file-based password manager** written in Java.  
All passwords are encrypted using **AES-256-GCM**, protected by a **single master password**.  
No server. No cloud. No plaintext storage.

This project is designed to demonstrate **correct cryptographic architecture**, not UI polish.

---

##  Security Model

- User sets **one master password**
- Master password is **never stored**
- A strong key is derived using **PBKDF2**
- That key is kept **only in memory after login**
- All stored passwords are encrypted using **AES-GCM**
- If the master password is wrong, nothing decrypts

If someone steals the file:
- They see only random binary data
- They must brute-force the master password offline

---

## 📁 File Structure

```text
├── vault.dat       # Unified encrypted storage (vault metadata + password entries)
└── src/main/java
    └── org/sri/passmanager/
        ├── Main.java
        ├── VaultCreator.java
        ├── VaultLogin.java
        ├── VaultData.java
        ├── VaultFileWriter.java
        ├── VaultFileReader.java
        ├── AESEncryption.java
        ├── PasswordEntry.java
        ├── PasswordFileWriter.java
        └── PasswordFileReader.java
```


---

##  Core Concepts

### 1. Unified Encrypted Storage (`vault.dat`)
- **Single file** containing all encrypted data
- File structure:
    1. **Vault metadata** (header):
        - Magic header (`"VLT1"`)
        - Random salt
        - Encrypted verification string (`"VAULT_OK"`) with IV
    2. **Password entries** (body):
        - Each entry contains:
            - Site
            - Username
            - Encrypted password (AES-GCM with fresh random IV)
- All data is encrypted and stored together in one file
- No plaintext passwords are written to disk

### 2. Vault Creation
- Generated **once** when first setting master password
- Creates unified storage file with vault metadata
- Verification blob ensures master password correctness

### 3. Login
- User enters master password
- PBKDF2 derives encryption key from master password + salt
- Verification blob is decrypted to verify password
- If successful → encryption key stays in memory for session

### 4. Password Storage
- Password entries are appended to the unified `vault.dat` file
- Every password uses a **fresh random IV** for encryption
- All entries are encrypted using the same session key

---

## 🔑 Cryptography Used

| Purpose | Algorithm |
|------|----------|
| Key derivation | PBKDF2WithHmacSHA256 |
| Encryption | AES-256-GCM |
| Integrity | Built-in AEAD authentication |
| Randomness | SecureRandom |

**Important rules followed:**
- PBKDF2 runs **only once per login**
- AES-GCM used for all encryption
- No hardcoded salts or IVs
- No ECB, CBC, or custom crypto

---

## ▶️ How to Run

### Requirements
- Java **17** (minimum)
- Maven or any Java IDE (IntelliJ / Eclipse / VS Code)

### Compile & Run
```bash
javac Main.java
java Main
```

## ⚠️ Limitations

This project **does not** include:

- GUI
- Clipboard protection
- Auto-lock timer
- Password search/edit/delete
- Secure password input masking
- Hardware-backed key storage

## ❗ Threat Model

This project **protects** against:

- File theft
- Offline brute-force attacks
- Accidental plaintext leaks 

It **does not protect** against:
- Malware / keyloggers
- Compromised OS or JVM
- Screen capture attacks
- Physical attacker with unlocked session

## 📜 Disclaimer

This project is for learning and personal use.  
Do not use it as-is for high-risk environments without further hardening and audit.