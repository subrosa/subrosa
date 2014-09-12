package com.subrosa.api.spring;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.subrosa.api.notification.GeneralCode;
import com.subrosa.api.notification.Notification;
import com.subrosa.api.notification.Severity;
import com.subrosa.api.response.NotificationList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static javax.servlet.http.HttpServletResponse.SC_METHOD_NOT_ALLOWED;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

/**
 * A handler for generic exceptions that may be thrown during an API call.
 */
public class ServiceExceptionResolver extends DefaultHandlerExceptionResolver {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceExceptionResolver.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception e) {
        if (e instanceof HttpMessageNotReadableException) {
            if (e.getCause() instanceof JsonMappingException) {
                return handleJsonMappingException((JsonMappingException) e.getCause(), request, response);
            } else {
                return handleHttpMessageNotReadableException(e, request, response);
            }
        } else if (e instanceof HttpRequestMethodNotSupportedException) {
            return handleHttpRequestMethodNotSupportedException(e, request, response);
        } else if (e instanceof FileNotFoundException) {
            return handleNotFoundException(e, request, response);
        } else if (e instanceof JsonMappingException) {
            return handleJsonMappingException(e, request, response);
        } else if (e instanceof HttpMediaTypeNotSupportedException) {
            return handleHttpMediaTypeNotSupportedException(e, request, response);
        } else {
            return handleGenericException(e, request, response);
        }
    }

    /**
     * Catch-all handler for HttpMessageNotReadableException.
     * @param ex Exception to handle
     * @param request The servlet request
     * @param httpServletResponse The servlet response
     * @return A {@link org.springframework.web.servlet.ModelAndView} containing an appropriate {@link com.subrosa.api.response.NotificationList}.
     */
    public ModelAndView handleHttpMessageNotReadableException(Exception ex, HttpServletRequest request, HttpServletResponse httpServletResponse) {
        LOG.error("Unexpected exception at URL: {}", request.getRequestURL(), ex);
        httpServletResponse.setStatus(SC_BAD_REQUEST);
        return wrapNotification(new Notification(GeneralCode.DESERIALIZATION_ERROR, Severity.ERROR, "Failed to deserialize request."));
    }
    /**
     * Catch-all handler for general exceptions.
     * @param ex Exception to handle
     * @param request The servlet request
     * @param httpServletResponse The servlet response
     * @return A {@link org.springframework.web.servlet.ModelAndView} containing an appropriate {@link com.subrosa.api.response.NotificationList}.
     */
    public ModelAndView handleGenericException(Exception ex, HttpServletRequest request, HttpServletResponse httpServletResponse) {
        LOG.error("Unexpected exception at URL: {}", request.getRequestURL(), ex);
        httpServletResponse.setStatus(SC_INTERNAL_SERVER_ERROR);
        return wrapNotification(new Notification(GeneralCode.INTERNAL_ERROR, Severity.ERROR, ex.getMessage()));
    }

    /**
     * Catch-all handler for unsupported type calls.
     * @param ex Exception to handle
     * @param request The servlet request
     * @param response The servlet response
     * @return A {@link org.springframework.web.servlet.ModelAndView} containing an appropriate {@link com.subrosa.api.response.NotificationList}.
     */
    public ModelAndView handleHttpRequestMethodNotSupportedException(Exception ex, HttpServletRequest request,
            HttpServletResponse response)
    {
        LOG.warn("HttpRequestMethodNotSupportedException encountered when handling URL: {}", request.getRequestURL(), ex);
        response.setStatus(SC_METHOD_NOT_ALLOWED);
        return wrapNotification(new Notification(GeneralCode.FORBIDDEN, Severity.ERROR, ex.getMessage()));
    }

    /**
     * Catch-all handler for {@link java.io.FileNotFoundException} exceptions.
     * @param ex  Exception to handle
     * @param httpServletRequest The servlet request
     * @param httpServletResponse The servlet response
     * @return A {@link org.springframework.web.servlet.ModelAndView} containing an appropriate {@link com.subrosa.api.response.NotificationList}.
     */
    public ModelAndView handleNotFoundException(Exception ex, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        LOG.error("FileNotFoundException encountered when handling URL: {}", httpServletRequest.getRequestURL());
        httpServletResponse.setStatus(SC_NOT_FOUND);
        return wrapNotification(new Notification(GeneralCode.FILE_NOT_FOUND,
                Severity.ERROR, ex.getMessage()));
    }

    /**
     * Catch-all handler for {@link JsonMappingException} exceptions.
     * @param ex Exception to handle
     * @param request The servlet request
     * @param httpServletResponse The servlet response
     * @return A {@link org.springframework.web.servlet.ModelAndView} containing an appropriate {@link com.subrosa.api.response.NotificationList}.
     */
    public ModelAndView handleJsonMappingException(Exception ex, HttpServletRequest request, HttpServletResponse httpServletResponse) {
        LOG.error("JsonMappingException encountered when handling URL: {}", request.getRequestURL(), ex);
        httpServletResponse.setStatus(SC_BAD_REQUEST);
        return wrapNotification(new Notification(GeneralCode.DESERIALIZATION_ERROR,
                Severity.ERROR, "Error deserializing request. Ensure that all values in your request are of the correct type."));
    }

    /**
     * Catch-all handler for {@link org.springframework.web.HttpMediaTypeNotSupportedException} exceptions.
     * @param ex Exception to handle
     * @param request The servlet request
     * @param httpServletResponse The servlet response
     * @return A {@link org.springframework.web.servlet.ModelAndView} containing an appropriate {@link com.subrosa.api.response.NotificationList}.
     */
    public ModelAndView handleHttpMediaTypeNotSupportedException(Exception ex, HttpServletRequest request, HttpServletResponse httpServletResponse) {
        LOG.error("HttpMediaTypeNotSupportedException encountered when handling URL: {}", request.getRequestURL(), ex);
        httpServletResponse.setStatus(SC_BAD_REQUEST);
        return wrapNotification(new Notification(GeneralCode.DESERIALIZATION_ERROR,
                Severity.ERROR, "Error deserializing request. Please check that your media type header is set to a valid MIME type."));
    }

    private ModelAndView wrapNotification(Notification n) {
        NotificationList nl = new NotificationList(n);
        ModelAndView mv = new ModelAndView();
        mv.addObject(nl);
        return mv;
    }

}
