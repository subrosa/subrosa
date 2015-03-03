package com.subrosagames.subrosa.api.dto;

/**
 * Encapsulates information for registering a new account.
 * <p/>
 * This includes the account data, along with a password for authenticating with that account.
 */
public class RegistrationRequest {

    private AccountDescriptor account;
    private String password;

    public AccountDescriptor getAccount() {
        return account;
    }

    public void setAccount(AccountDescriptor account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
