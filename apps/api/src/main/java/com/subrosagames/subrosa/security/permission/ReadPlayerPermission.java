package com.subrosagames.subrosa.security.permission;

import java.io.Serializable;

import org.apache.commons.lang.NotImplementedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.subrosagames.subrosa.domain.player.Player;

/**
 * Evaluates read player permissions.
 */
@Component
public class ReadPlayerPermission extends AbstractPermission {

    @Override
    public boolean isAllowed(Authentication authentication, Object target) {
        return isAdmin(authentication) || target instanceof Player
                && hasAccountId(authentication, ((Player) target).getAccount().getId());
    }

    @Override
    public boolean isAllowed(Authentication authentication, Serializable id, String type) {
        throw new NotImplementedException("Read player permission by type string is not implemented");
    }
}
