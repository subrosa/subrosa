package com.subrosagames.subrosa.security;

import org.springframework.social.security.SocialUser;

import lombok.Getter;


/**
 * Overrides default spring security user to manipulate salted passwords handling.
 */
@Getter
public class SubrosaUser extends SocialUser {

    private static final long serialVersionUID = 5565905400673524447L;

    private final Integer id;
    private final String email;
    private final String phone;

    /**
     * Construct with given subrosa account.
     *
     * @param account subrosa account
     */
    public SubrosaUser(Account account) {
        super(account.getEmail(), account.getPassword(), account.grantedAuthorities());
        id = account.getId();
        email = account.getEmail();
        phone = account.getPhone();
    }

}
