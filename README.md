# Password Manager
A tool for storing passwords behind a super password.
Uses AES-GCM to encrypt the passwords.

## Explaining code structure

The entire code is stored in [./src/main/java/org/sri/passmanager/](./src/main/java/org/sri/passmanager/)

Following are the java files associated with the code:

- [Main.java](./src/main/java/org/sri/passmanager/Main.java) : Contains main function for the execution of the code


- [AESEncryption.java](./src/main/java/org/sri/passmanager/AESEncryption.java) : Contains two functions for encoding and decoding respectively

```java
    public String encrypt(String plaintext, String SECRET_KEY) { 
    // To encrypt the given plaintext and return the encrypted text
    }
    public String decrypt(String encryptedText, String SECRET_KEY) {
    // To decrypt the given encrypted text and return the plaintext
    }
 ```

- [StorageHandler.java](./src/main/java/org/sri/passmanager/StorageHandler.java) : Contains the functions to store to and from secondary storage

```java
    public void storeToFile(String encryptedText, Path filePath) {
    // To store to secondary storage
    }
    public void readFile(Path filePath) {
        // To read from secondary storage
    }
```
  
- [CLI.java](./src/main/java/org/sri/passmanager/CLI.java) : Contains code for command line interface