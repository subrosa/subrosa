package com.subrosagames.subrosa.security;

import java.io.Serializable;

import com.subrosagames.subrosa.domain.PermissionTarget;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

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

        VIEW_ACCOUNT
                {
                    @Override
                    boolean evaluate(Authentication authentication, Serializable targetId, String targetType) {
                        return authenticationHasRole(authentication, "ADMIN")
                                || ((SubrosaUser) authentication.getPrincipal()).getAccount().getId().equals(targetId);
                    }
                };

        // CHECKSTYLE-ON: JavadocMethod

        private static boolean authenticationHasRole(Authentication authentication, String role) {
            return authentication.getAuthorities().contains(new SimpleGrantedAuthority(role));
        }

        abstract boolean evaluate(Authentication authentication, Serializable targetId, String targetType);

    }
}
