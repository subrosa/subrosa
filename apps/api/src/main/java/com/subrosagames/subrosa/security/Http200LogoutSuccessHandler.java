package com.subrosagames.subrosa.security;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

/**
 * Logout success handler that returns an empty HTTP 200 OK.
 */
public class Http200LogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException // SUPPRESS CHECKSTYLE RedundantThrowsCheck
    {
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
