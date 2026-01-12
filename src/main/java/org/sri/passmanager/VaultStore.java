package org.sri.passmanager;

import java.util.List;

public class VaultStore {

    private final VaultData vaultData;
    private final List<PasswordEntry> entries;

    public VaultStore(VaultData vaultData, List<PasswordEntry> entries) {
        this.vaultData = vaultData;
        this.entries = entries;
    }

    public VaultData getVaultData() {
        return vaultData;
    }

    public List<PasswordEntry> getEntries() {
        return entries;
    }
}
