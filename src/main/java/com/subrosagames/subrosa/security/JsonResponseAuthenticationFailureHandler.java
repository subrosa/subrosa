package com.subrosagames.subrosa.security;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.subrosagames.subrosa.api.notification.GeneralCode;
import com.subrosagames.subrosa.api.notification.Notification;
import com.subrosagames.subrosa.api.notification.Severity;
import com.subrosagames.subrosa.api.response.NotificationList;

/**
 * Handles failed authentication attempts via a JSON response.
 */
public class JsonResponseAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private static final Logger LOG = LoggerFactory.getLogger(JsonResponseAuthenticationFailureHandler.class);

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e)
            throws IOException, ServletException // SUPPRESS CHECKSTYLE RedundantThrowsCheck
    {
        LOG.debug("Authentication failure exception: {}", e.getMessage());

        Notification notification = new Notification(
                GeneralCode.FORBIDDEN, Severity.ERROR,
                e.getMessage());
        NotificationList notificationList = new NotificationList(notification);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(new ObjectMapper().writeValueAsString(notificationList));
    }
}
