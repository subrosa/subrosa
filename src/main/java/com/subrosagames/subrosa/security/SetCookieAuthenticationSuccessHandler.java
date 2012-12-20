package com.subrosagames.subrosa.security;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import com.subrosagames.subrosa.domain.token.TokenFactory;
import com.subrosagames.subrosa.domain.token.TokenType;

/**
 *
 */
public class SetCookieAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private TokenFactory tokenFactory;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        int accountId = ((SubrosaUser) authentication.getPrincipal()).getAccount().getId();
        response.addCookie(new Cookie("subrosa_auth_token", tokenFactory.generateNewToken(TokenType.DEVICE_AUTH, accountId)));
    }
}
