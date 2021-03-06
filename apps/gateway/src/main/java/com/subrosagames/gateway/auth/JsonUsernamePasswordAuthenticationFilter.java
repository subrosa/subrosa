package com.subrosagames.gateway.auth;

import java.io.BufferedReader;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Handles authentication requests via JSON POST body.
 */
public class JsonUsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final Logger LOG = LoggerFactory.getLogger(JsonUsernamePasswordAuthenticationFilter.class);

    /**
     * Default constructor.
     */
    public JsonUsernamePasswordAuthenticationFilter() {
        super("/j_spring_security_check");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        LoginRequest loginRequest = getLoginRequest(request);
        String username;
        if (loginRequest.getEmail() != null) {
            username = loginRequest.getEmail();
        } else if (loginRequest.getPhone() != null) {
            username = loginRequest.getPhone();
        } else {
            username = "";
        }
        String password = loginRequest.getPassword();
        if (password == null) {
            password = "";
        }
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    private LoginRequest getLoginRequest(HttpServletRequest request) {
        try {
            BufferedReader reader = request.getReader();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            return objectMapper.readValue(reader, LoginRequest.class);
        } catch (JsonParseException e) {
            LOG.info("Encountered invalid JSON for authentication", e);
            throw new AuthenticationCredentialsNotFoundException("Invalid JSON body provided");
        } catch (JsonMappingException e) {
            LOG.info("Encountered bad JSON for authentication", e);
            throw new AuthenticationCredentialsNotFoundException("Invalid JSON body provided");
        } catch (IOException e) {
            LOG.error("Failed to obtain request body reader", e);
            throw new IllegalStateException(e);
        }
    }

}
