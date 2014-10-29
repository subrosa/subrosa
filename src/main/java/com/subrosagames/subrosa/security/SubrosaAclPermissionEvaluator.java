package com.subrosagames.subrosa.security;

import java.io.Serializable;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.subrosagames.subrosa.domain.PermissionTarget;

/**
 * Implements ACL-based permission decisions based on subrosa's roles and ownership rules.
 */
@Component
public class SubrosaAclPermissionEvaluator implements PermissionEvaluator {

    @Override
    public boolean hasPermission(Authentication authentication, Object target, Object permission) {
        if (!(target instanceof PermissionTarget)) {
            throw new IllegalStateException("Attempted to evaluate permission on a non-identifiable object");
        }
        return hasPermission(authentication, ((PermissionTarget) target).getId(), target.getClass().getSimpleName(), permission);
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return authentication.getPrincipal() instanceof SubrosaUser
                && SubrosaPermission.valueOf(permission.toString()).evaluate(authentication, targetId, targetType);
    }

    /**
     * Enumerates possible permissions, along with the implementation of their evaluations.
     */
    public enum SubrosaPermission {

        // CHECKSTYLE-OFF: JavadocMethod

        /**
         * View account permission.
         */
        READ_ACCOUNT
                {
                    @Override
                    boolean evaluate(Authentication authentication, Serializable targetId, String targetType) {
                        return isAdmin(authentication) || ("Account".equals(targetType) && hasAccountId(authentication, targetId));
                    }
                },
        /**
         * Update account permission.
         */
        WRITE_ACCOUNT
                {
                    @Override
                    boolean evaluate(Authentication authentication, Serializable targetId, String targetType) {
                        return isAdmin(authentication) || ("Account".equals(targetType) && hasAccountId(authentication, targetId));
                    }
                };

        // CHECKSTYLE-ON: JavadocMethod

        private static boolean isAdmin(Authentication authentication) {
            return authenticationHasRole(authentication, "ADMIN");
        }

        private static boolean authenticationHasRole(Authentication authentication, String role) {
            return authentication.getAuthorities().contains(new SimpleGrantedAuthority(role));
        }

        private static boolean hasAccountId(Authentication authentication, Serializable targetId) {
            return ((SubrosaUser) authentication.getPrincipal()).getAccount().getId().equals(targetId);
        }

        abstract boolean evaluate(Authentication authentication, Serializable targetId, String targetType);

    }
}
