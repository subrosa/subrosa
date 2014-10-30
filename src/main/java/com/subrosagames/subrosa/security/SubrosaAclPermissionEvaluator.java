package com.subrosagames.subrosa.security;

import java.io.Serializable;
import java.util.Map;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.subrosagames.subrosa.security.permission.Permission;

/**
 * Implements ACL-based permission decisions based on subrosa's roles and ownership rules.
 */
@Component
public class SubrosaAclPermissionEvaluator implements PermissionEvaluator {

    private Map<String, Permission> permissionMap;

    @Override
    public boolean hasPermission(Authentication authentication, Object target, Object permission) {
        boolean hasPermission = false;
        if (canHandle(authentication, target, permission)) {
            hasPermission = checkPermission(authentication, target, (String) permission);
        }
        return hasPermission;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        boolean hasPermission = false;
        if (canHandle(authentication, targetId, targetType, permission)) {
            hasPermission = checkPermission(authentication, targetId, targetType, (String) permission);
        }
        return hasPermission;
    }

    private boolean canHandle(Authentication authentication, Object target, Object permission) {
        return authentication != null && target != null && permission instanceof String && permissionMap.containsKey(permission);
    }

    private boolean canHandle(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return authentication != null && targetId != null && targetType != null && permission instanceof String && permissionMap.containsKey(permission);
    }

    private boolean checkPermission(Authentication authentication, Object target, String permissionKey) {
        Permission permission = permissionMap.get(permissionKey);
        return permission.isAllowed(authentication, target);
    }

    private boolean checkPermission(Authentication authentication, Serializable targetId, String targetType, String permissionKey) {
        Permission permission = permissionMap.get(permissionKey);
        return permission.isAllowed(authentication, targetId, targetType);
    }

    public void setPermissionMap(Map<String, Permission> permissionMap) {
        this.permissionMap = permissionMap;
    }

    Map<String, Permission> getPermissionMap() {
        return permissionMap;
    }
}
