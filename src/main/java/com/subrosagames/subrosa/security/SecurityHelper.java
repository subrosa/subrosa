package com.subrosagames.subrosa.security;

import com.subrosagames.subrosa.domain.account.Account;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Helper class for determining authentication status and retrieving authenticated user.
 */
public final class SecurityHelper {

    private SecurityHelper() { }

    public static boolean isAnonymous() {
        return isAnonymous(SecurityContextHolder.getContext().getAuthentication());
    }

    public static boolean isAnonymous(Authentication authentication) {
        return authentication.isAuthenticated() && authentication.getPrincipal() instanceof String;
    }

    public static boolean isAuthenticated() {
        return isAuthenticated(SecurityContextHolder.getContext().getAuthentication());
    }

    public static boolean isAuthenticated(Authentication authentication) {
        return authentication.isAuthenticated() && authentication.getPrincipal() instanceof SubrosaUser;
    }

    public static Account getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!isAuthenticated(authentication)) {
            return null;
        }
        return ((SubrosaUser) authentication.getPrincipal()).getAccount();
    }

}
