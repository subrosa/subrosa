package com.subrosagames.subrosa.security;

import com.subrosagames.subrosa.domain.PermissionTarget;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.io.Serializable;

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

    public enum SubrosaPermission {

        VIEW_ACCOUNT {
            @Override
            boolean evaluate(Authentication authentication, Serializable targetId, String targetType) {
                return authenticationHasRole(authentication, "ADMIN")
                        || ((SubrosaUser) authentication.getPrincipal()).getAccount().getId().equals(targetId);
            }
        }
        ;

        private static boolean authenticationHasRole(Authentication authentication, String role) {
            return authentication.getAuthorities().contains(new SimpleGrantedAuthority(role));
        }

        abstract boolean evaluate(Authentication authentication, Serializable targetId, String targetType);

    }
}
