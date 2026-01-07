package org.sri.passmanager;

public class PasswordEntry {

    private final String site;
    private final String username;
    private final byte[] encryptedPassword;

    public PasswordEntry(String site, String username, byte[] encryptedPassword) {
        this.site = site;
        this.username = username;
        this.encryptedPassword = encryptedPassword;
    }

    public String getSite() {
        return site;
    }

    public String getUsername() {
        return username;
    }

    public byte[] getEncryptedPassword() {
        return encryptedPassword;
    }
}
