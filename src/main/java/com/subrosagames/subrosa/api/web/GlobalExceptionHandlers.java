package com.subrosagames.subrosa.api.web;

import javax.validation.ConstraintViolation;
import java.io.EOFException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.subrosagames.subrosa.api.BadRequestException;
import com.subrosagames.subrosa.api.NotAuthenticatedException;
import com.subrosagames.subrosa.domain.DomainObjectNotFoundException;
import com.subrosagames.subrosa.domain.DomainObjectValidationException;
import com.google.common.collect.Maps;
import com.subrosa.api.notification.GeneralCode;
import com.subrosa.api.notification.Notification;
import com.subrosa.api.notification.Severity;
import com.subrosa.api.response.NotificationList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Implements global exception handling.
 */
@ControllerAdvice
public class GlobalExceptionHandlers {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandlers.class);

    /**
     * Handle {@link DomainObjectValidationException}.
     * @param e exception
     * @return notification list
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public NotificationList handleDomainObjectValidationException(DomainObjectValidationException e) {
        LOG.debug("Global exception handler: {}", e.getMessage());
        NotificationList notificationList = new NotificationList();
        Set<? extends ConstraintViolation<?>> violations = e.getViolations();
        for (ConstraintViolation<?> violation : violations) {
            Notification notification = createInvalidFieldNotification(violation);
            notificationList.addNotification(notification);
        }
        return notificationList;
    }

    private Notification createInvalidFieldNotification(final ConstraintViolation<?> violation) {
        final Notification notification = new Notification(
                GeneralCode.INVALID_FIELD_VALUE, Severity.ERROR,
                GeneralCode.INVALID_FIELD_VALUE.getDefaultMessage());
        notification.setDetails(new HashMap<String, String>(2) {{
            put("field", violation.getPropertyPath().toString());
            put("constraint", violation.getMessage());
        }});
        LOG.debug("Global exception handler domain object constraint violation: {} => {}", violation.getPropertyPath(), violation.getMessage());
        return notification;
    }

    /**
     * Handle {@link DomainObjectNotFoundException}.
     * @param e exception
     * @return notification list
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public NotificationList handleDomainObjectNotFoundException(DomainObjectNotFoundException e) {
        LOG.debug("Global exception handler: {}", e.getMessage());
        Notification notification = new Notification(
                GeneralCode.DOMAIN_OBJECT_NOT_FOUND, Severity.ERROR,
                e.getMessage());
        return new NotificationList(notification);
    }

    /**
     * Handle {@link com.subrosagames.subrosa.api.NotAuthenticatedException}.
     * @param e exception
     * @return notification list
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public NotificationList handleNotAuthenticatedException(NotAuthenticatedException e) {
        LOG.debug("Global exception handler: {}", e.getMessage());
        Notification notification = new Notification(
                GeneralCode.FORBIDDEN, Severity.ERROR,
                e.getMessage());
        return new NotificationList(notification);
    }

    /**
     * Handle {@link HttpMessageConversionException}.
     * @param e exception
     * @return notification list
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public NotificationList unwrapHttpMessageConversionException(HttpMessageConversionException e) {
        if (e.getCause() instanceof UnrecognizedPropertyException) {
            return handleUnrecognizedPropertyException((UnrecognizedPropertyException) e.getCause());
        } else if (e.getCause() instanceof EOFException) {
            return handleEOFException((EOFException) e.getCause());
        }
        throw e;
    }

    /**
     * Handle {@link EOFException}.
     * @param e exception
     * @return notification list
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    private NotificationList handleEOFException(EOFException e) {
        LOG.debug("Global exception handler: {}", e.getMessage());
        Notification notification = new Notification(
                GeneralCode.INVALID_REQUEST_ENTITY, Severity.ERROR,
                "Empty request body");
        return new NotificationList(notification);
    }

    /**
     * Handle {@link UnrecognizedPropertyException}.
     * @param e exception
     * @return notification list
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public NotificationList handleBadRequestException(BadRequestException e) {
        LOG.debug("Global exception handler: {}", e.getMessage());
        Notification notification = new Notification(
                GeneralCode.INVALID_REQUEST_ENTITY, Severity.ERROR,
                e.getMessage());
        return new NotificationList(notification);
    }

    /**
     * Handle {@link UnrecognizedPropertyException}.
     * @param e exception
     * @return notification list
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public NotificationList handleUnrecognizedPropertyException(UnrecognizedPropertyException e) {
        LOG.debug("Global exception handler: {}", e.getMessage());
        Notification notification = new Notification(
                GeneralCode.INVALID_REQUEST_ENTITY, Severity.ERROR,
                GeneralCode.INVALID_REQUEST_ENTITY.getDefaultMessage());
        Map<String, String> details = Maps.newHashMap();
        details.put(e.getUnrecognizedPropertyName(), "unrecognized property");
        notification.setDetails(details);
        return new NotificationList(notification);
    }
}