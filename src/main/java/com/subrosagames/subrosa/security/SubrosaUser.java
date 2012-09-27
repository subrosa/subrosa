package com.subrosagames.subrosa.security;

import java.util.HashSet;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import com.subrosagames.subrosa.domain.account.Account;

/**
* Created with IntelliJ IDEA.
* User: jgore
* Date: 8/28/12
* Time: 2:29 午後
* To change this template use File | Settings | File Templates.
*/
public class SubrosaUser extends User {

    private Account account;

    public SubrosaUser(Account account) {
        super(account.getEmail(), account.getPassword(), new HashSet<GrantedAuthority>());
        this.account = account;
    }

    public Account getAccount() {
        return account;
    }

    public String getPassword() {
        String password = super.getPassword();
        return password.substring(128);
    }

    public String getSalt() {
        String password = super.getPassword();
        return password.substring(0, 128);
    }
}
