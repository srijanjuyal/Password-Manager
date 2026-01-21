# Java Password Manager (Local, Encrypted)

A **local, file-based password manager** written in Java.  
All passwords are encrypted using **AES-256-GCM** and using password based key derivation function **PBKDF2**, protected by a **single master password**.  
No server. No cloud. No plaintext storage.

---

##  Security Model

- User sets **one master password**
- Master password is **never stored**
- A strong key is derived using **PBKDF2**
- That key is kept **only in memory after login**
- All stored passwords are encrypted using **AES-GCM**
- If the master password is wrong, nothing decrypts

If someone steals the files:
- They see only random binary data
- They must brute-force the master password offline

---

## 📁 File Structure

```text
├── vault.bin       # Unified password storage file
└── src/main/java
    └── org/sri/passmanager/
        ├── Main.java
        ├── VaultCreator.java
        ├── VaultLogin.java
        ├── VaultData.java
        ├── VaultStore.java
        ├── VaultStoreReader.java
        ├── VaultStoreWriter.java
        ├── AESEncryption.java
        └── PasswordEntry.java
```


---

##  Core Concepts

### 1. Vault Creation and Password Storage (`vault.bin`)
- Generated **once** and later updated with passwords
- Stores (to verify Master Password):
    - Random salt
    - Encrypted verification string (`"VAULT_OK"`)
- Each password entry contains:
    - Site
    - Username
    - Encrypted password (AES-GCM)
- Every password uses a **fresh random IV**
- No plaintext passwords are written to disk

### 2. Login
- User enters master password
- PBKDF2 derives encryption key
- Verification blob is decrypted
- If successful → encryption key stays in memory

### 3. Exit
- Wipes the encryption key from memory

---

## 🔑 Cryptography Used

| Purpose        | Algorithm                    |
|----------------|------------------------------|
| Key derivation | PBKDF2WithHmacSHA256         |
| Encryption     | AES-256-GCM                  |
| Integrity      | Built-in AEAD authentication |
| Randomness     | SecureRandom                 |

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