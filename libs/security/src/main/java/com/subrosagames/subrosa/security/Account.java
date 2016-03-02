package com.subrosagames.subrosa.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

/**
 * TODO
 */
public interface Account {

    Integer getId();

    String getEmail();

    String getPhone();

    String getPassword();

    Collection<? extends GrantedAuthority> grantedAuthorities();

}
