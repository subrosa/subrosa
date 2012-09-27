package com.subrosagames.subrosa.security;

import java.util.HashSet;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import com.subrosagames.subrosa.domain.account.Account;

public class SubrosaUser extends User {

    private Account account;

    public SubrosaUser(Account account) {
        super(account.getEmail(), account.getPassword(), new HashSet<GrantedAuthority>());
        this.account = account;
    }

    public Account getAccount() {
        return account;
    }

    /**
     * Get a user's unsalted password.
     * @return String password.
     */
    public String getPassword() {
        String password = super.getPassword();
        return password.substring(128);
    }

    /**
     * Get the salt used to create the password.
     * @return String salt.
     */
    public String getSalt() {
        String password = super.getPassword();
        return password.substring(0, 128);
    }
}
