package com.subrosagames.subrosa.security.permission;

import java.io.Serializable;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.subrosagames.subrosa.security.SubrosaUser;

/**
 * Common functionality for permissions evaluation.
 */
public abstract class AbstractPermission implements Permission {

    boolean isAdmin(Authentication authentication) {
        return authenticationHasRole(authentication, "ADMIN");
    }

    boolean authenticationHasRole(Authentication authentication, String role) {
        return authentication.getAuthorities().contains(new SimpleGrantedAuthority(role));
    }

    boolean hasAccountId(Authentication authentication, Serializable targetId) {
        return ((SubrosaUser) authentication.getPrincipal()).getAccount().getId().equals(targetId);
    }

}
