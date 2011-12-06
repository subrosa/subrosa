package com.subrosa.web.view.il8n;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.i18n.AbstractLocaleResolver;

/**
 * Implementation of LocaleResolver, which works with the LocaleDetectionFilter to set
 * locale correctly for Lulu products.
 */
public class LocaleResolver extends AbstractLocaleResolver {

    /**
     * Determine the correct locale according to Lulu's rules by pulling the locale
     * from the request attributes.
     * @param request The http request.
     * @return The correct locale.
     */
    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        Locale locale = (Locale) request.getAttribute(LocaleDetectionFilter.REQUEST_LOCALE_ATTRIBUTE);
        if (locale != null) {
            return locale;
        }
        return getDefaultLocale();
    }

    /**
     * This class does not allow the locale to be changed mid-request.
     * @param request the http request
     * @param response the http response
     * @param locale the requested locale
     */
    @Override
    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
        throw new UnsupportedOperationException("Cannot change the url - use a different locale resolution strategy");
    }
}
