package com.subrosagames.subrosa.security.permission;

import java.io.Serializable;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.subrosagames.subrosa.domain.account.Account;

/**
 * Evaluates read account permissions.
 */
@Component
public class ReadAccountPermission extends AbstractPermission {

    @Override
    public boolean isAllowed(Authentication authentication, Object target) {
        return isAdmin(authentication) || target instanceof Account && hasAccountId(authentication, ((Account) target).getId());
    }

    @Override
    public boolean isAllowed(Authentication authentication, Serializable id, String type) {
        return isAdmin(authentication) || "Account".equals(type) && hasAccountId(authentication, id);
    }
}
