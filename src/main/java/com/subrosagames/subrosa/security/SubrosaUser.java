package com.subrosagames.subrosa.security;

import java.util.HashSet;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import com.subrosagames.subrosa.domain.account.Account;

import java.util.HashSet;

/**
 * Overrides default spring security user to manipulate salted passwords handling.
 */
public class SubrosaUser extends User {

    private Account account;

    /**
     * Construct with given subrosa account.
     * @param account subrosa account
     */
    public SubrosaUser(Account account) {
        super(account.getEmail(), account.getPassword(), new HashSet<GrantedAuthority>());
        this.account = account;
    }

    /**
     * Get the password portion of the stored salted password.
     * @return password
     */
    public String getPassword() {
        String password = super.getPassword();
        return password.substring(128);
    }

    /**
     * Get the salt portion of the stored salted password.
     * @return salt
     */
    public String getSalt() {
        String password = super.getPassword();
        return password.substring(0, 128);
    }

    public Account getAccount() {
        return account;
    }
}
