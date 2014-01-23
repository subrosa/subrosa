package com.subrosagames.subrosa.security;

import java.io.BufferedReader;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

/**
 * Handles authentication requests via JSON POST body.
 */
public class JsonUsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final Logger LOG = LoggerFactory.getLogger(JsonUsernamePasswordAuthenticationFilter.class);

    protected JsonUsernamePasswordAuthenticationFilter() {
        super("/j_spring_security_check");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        LoginRequest loginRequest = getLoginRequest(request);
        String username = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        if (username == null) {
            username = "";
        }
        if (password == null) {
            password = "";
        }
        username = username.trim();
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    private LoginRequest getLoginRequest(HttpServletRequest request) {
        try {
            BufferedReader reader = request.getReader();
            return new ObjectMapper().readValue(reader, LoginRequest.class);
        } catch (IOException e) {
            LOG.error("Failed to obtain request body reader", e);
        }
        return null;
    }

}
