package com.subrosagames.subrosa.api;

import javax.annotation.Nullable;
import javax.validation.ConstraintViolation;
import java.util.Map;
import java.util.Set;

import com.subrosagames.subrosa.domain.DomainObjectNotFoundException;
import com.subrosagames.subrosa.domain.DomainObjectValidationException;
import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.subrosa.api.notification.GeneralCode;
import com.subrosa.api.notification.Notification;
import com.subrosa.api.notification.Severity;
import com.subrosa.api.response.NotificationList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 */
@ControllerAdvice
public class GlobalHandlers {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalHandlers.class);

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public NotificationList handleDomainObjectValidationException(DomainObjectValidationException e) {
        LOG.debug("Global exception handler: {}", e.getMessage());
        Set<? extends ConstraintViolation<?>> violations = e.getViolations();
        Notification notification = new Notification(
                GeneralCode.INVALID_REQUEST_ENTITY, Severity.ERROR,
                GeneralCode.INVALID_REQUEST_ENTITY.getDefaultMessage());
        Map<String, String> details = Maps.newHashMap();
        for (ConstraintViolation<?> violation : violations) {
            details.put(violation.getPropertyPath().toString(), violation.getMessage());
        }
        notification.setDetails(details);
        return new NotificationList(notification);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public NotificationList handleDomainObjectNotFoundException(DomainObjectNotFoundException e) {
        LOG.debug("Global exception handler: {}", e.getMessage());
        Notification notification = new Notification(
                GeneralCode.DOMAIN_OBJECT_NOT_FOUND, Severity.ERROR,
                GeneralCode.DOMAIN_OBJECT_NOT_FOUND.getDefaultMessage());
        return new NotificationList(notification);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public NotificationList handleNotAuthenticatedException(NotAuthenticatedException e) {
        LOG.debug("Global exception handler: {}", e.getMessage());
        Notification notification = new Notification(
                GeneralCode.FORBIDDEN, Severity.ERROR,
                GeneralCode.FORBIDDEN.getDefaultMessage());
        return new NotificationList(notification);
    }
}
