package com.subrosagames.subrosa.security.permission;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.subrosagames.subrosa.security.SubrosaUser;

/**
 * Common functionality for permissions evaluation.
 */
public abstract class AbstractPermission implements Permission {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractPermission.class);

    boolean isAdmin(Authentication authentication) {
        return authenticationHasRole(authentication, "ROLE_ADMIN");
    }

    boolean authenticationHasRole(Authentication authentication, String role) {
        return authentication.getAuthorities().contains(new SimpleGrantedAuthority(role));
    }

    boolean hasAccountId(Authentication authentication, Serializable targetId) {
        LOG.debug("principal is {}", authentication.getPrincipal());
        return ((SubrosaUser) authentication.getPrincipal()).getId().equals(targetId);
    }

}
