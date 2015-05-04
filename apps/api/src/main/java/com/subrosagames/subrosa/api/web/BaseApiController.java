package com.subrosagames.subrosa.api.web;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.subrosagames.subrosa.api.NotAuthenticatedException;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.security.SubrosaUser;

/**
 * Common functionality for API controllers.
 */
public class BaseApiController {

    Account getAuthenticatedUser() throws NotAuthenticatedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof SubrosaUser) {
            return ((SubrosaUser) authentication.getPrincipal()).getAccount();
        }
        throw new NotAuthenticatedException("Request is not authenticated");
    }

}
