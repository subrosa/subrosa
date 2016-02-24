package com.subrosagames.gateway.auth;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.subrosagames.gateway.token.TokenFactory;
import com.subrosagames.gateway.token.TokenType;

/**
 * Authentication success handler that sets an auth token cookie for use by mobile devices.
 */
public class JsonResponseAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private TokenFactory tokenFactory;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException // SUPPRESS CHECKSTYLE RedundantThrowsCheck
    {
        Account account = (Account) authentication.getPrincipal();
        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setToken(tokenFactory.generateNewToken(account, TokenType.DEVICE_AUTH));
        response.getOutputStream().print(new ObjectMapper().writeValueAsString(authenticationResponse));
    }
}
