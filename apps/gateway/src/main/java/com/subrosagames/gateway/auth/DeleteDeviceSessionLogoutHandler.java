package com.subrosagames.gateway.auth;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import com.subrosagames.gateway.token.Token;
import com.subrosagames.gateway.token.TokenRepository;
import com.subrosagames.subrosa.security.SubrosaUser;

/**
 * Logout handler that removes the device session tokens for the user.
 */
@Component
public class DeleteDeviceSessionLogoutHandler implements LogoutHandler {

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (authentication == null) {
            return;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof SubrosaUser) {
            // TODO ignores possibility that account is not found
            Account account = accountRepository.findOne(((SubrosaUser) principal).getId());
            List<Token> tokens = tokenRepository.findByAccount(account);
            tokenRepository.delete(tokens);
        }
    }
}
