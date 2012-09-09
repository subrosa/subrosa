package com.subrosagames.subrosa.service;

import com.subrosagames.subrosa.domain.account.Account;

/**
 * Encapsulates information for registering a new account.
 *
 * This includes the account data, along with a password for authenticating with that account.
 */
public class Registration {

    private Account account;
    private String password;

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
