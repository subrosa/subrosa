package com.subrosagames.subrosa.security.permission;

import java.io.Serializable;

import org.springframework.security.core.Authentication;

/**
 * Handler for permission evaluation.
 */
public interface Permission {

    /**
     * Return whether the given @{link Authentication} is able to act on the specified target object.
     *
     * @param authentication authentication
     * @param target         target
     * @return whether action is allowed
     */
    boolean isAllowed(Authentication authentication, Object target);

    /**
     * Return whether the given @{link Authentication} is able to act on a target of the given id and type.
     *
     * @param authentication authentication
     * @param id             id
     * @param type           type
     * @return whether action is allowed
     */
    boolean isAllowed(Authentication authentication, Serializable id, String type);
}
