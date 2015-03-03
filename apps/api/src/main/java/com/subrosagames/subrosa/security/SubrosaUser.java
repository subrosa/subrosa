package com.subrosagames.subrosa.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.social.security.SocialUser;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.account.AccountRole;
import com.subrosagames.subrosa.domain.account.UserConnection;

/**
 * Overrides default spring security user to manipulate salted passwords handling.
 */
public class SubrosaUser extends SocialUser {

    private static final long serialVersionUID = 5565905400673524447L;

    private transient Account account;

    private UserConnection.Provider socialProvider;

    /**
     * Construct with given subrosa account.
     *
     * @param account subrosa account
     */
    public SubrosaUser(Account account) {
        super(account.getEmail(), account.getPassword(), Collections2.transform(account.getRoles(), new Function<AccountRole, GrantedAuthority>() {
            @Override
            public GrantedAuthority apply(AccountRole accountRole) {
                return new SimpleGrantedAuthority(accountRole.name());
            }
        }));
        this.account = account;
    }

    /**
     * Get the password portion of the stored salted password.
     *
     * @return password
     */
    public String getPassword() {
        String password = super.getPassword();
        return password.substring(128);
    }

    /**
     * Get the salt portion of the stored salted password.
     *
     * @return salt
     */
    public String getSalt() {
        String password = super.getPassword();
        return password.substring(0, 128);
    }

    public Account getAccount() {
        return account;
    }

    @Override
    public boolean equals(Object rhs) {
        return rhs instanceof SubrosaUser
                && super.equals(rhs);
    }

    @Override
    public int hashCode() {
        return getUsername().hashCode();
    }
}
