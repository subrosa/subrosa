package com.subrosagames.subrosa.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.token.TokenRepository;

/**
 * Logout handler that removes the device session tokens for the user.
 */
@Component
public class DeleteDeviceSessionLogoutHandler implements LogoutHandler {

    @Autowired
    private TokenRepository tokenRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (authentication == null) {
            return;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof SubrosaUser) {
            Account account = ((SubrosaUser) principal).getAccount();
            tokenRepository.deleteTokensForAccount(account);
        }
    }
}
