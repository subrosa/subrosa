package com.subrosagames.subrosa.api.web;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.security.SubrosaUser;

/**
 * Common functionality for API controllers.
 */
public class BaseApiController {

    Account getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ((SubrosaUser) authentication.getPrincipal()).getAccount();
    }

}
