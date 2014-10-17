package com.subrosagames.subrosa.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.subrosagames.subrosa.domain.account.Account;

/**
 * Helper class for determining authentication status and retrieving authenticated user.
 */
public final class SecurityHelper {

    private SecurityHelper() {
    }

    /**
     * Whether current user is anonymous.
     *
     * @return whether anonymous
     */
    public static boolean isAnonymous() {
        return isAnonymous(SecurityContextHolder.getContext().getAuthentication());
    }

    /**
     * Whether specified authentication is anonymous.
     *
     * @param authentication authentication
     * @return whether anonymous
     */
    public static boolean isAnonymous(Authentication authentication) {
        return authentication.isAuthenticated() && authentication.getPrincipal() instanceof String;
    }

    /**
     * Whether current user is authenticated.
     *
     * @return whether authenticated
     */
    public static boolean isAuthenticated() {
        return isAuthenticated(SecurityContextHolder.getContext().getAuthentication());
    }

    /**
     * Whether specified authentication is authenticated.
     *
     * @param authentication authentication
     * @return whether authenticated
     */
    public static boolean isAuthenticated(Authentication authentication) {
        return authentication.isAuthenticated() && authentication.getPrincipal() instanceof SubrosaUser;
    }

    /**
     * Retrieve the currently logged in user.
     *
     * @return logged in user, or {@code null} if not authenticated.
     */
    public static Account getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!isAuthenticated(authentication)) {
            return null;
        }
        return ((SubrosaUser) authentication.getPrincipal()).getAccount();
    }

}
