package com.subrosa.web.view.il8n;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Servlet filter to detect requested locale and add it to a request attribute.
 * This filter provides a single location to define and implement all location
 * rules for Lulu.
 */
public class LocaleDetectionFilter extends OncePerRequestFilter {

    /**
     * The name of the request attribute where the locale can be found.
     */
    public static final String REQUEST_LOCALE_ATTRIBUTE = "request_locale";

    private static final int LOCALE_PATH_SEGMENT = 1;

    /**
     * Implement locale detection. Currently, just looks at the URL used to
     * request the content. If a locale is specified, then the locale is set and
     * the request is forwarded to the same URL without the locale specified.
     * This allows request mapping to ignore country and language segments.
     *
     * @param request The http request
     * @param response The http response
     * @param filterChain the filter chain for this request.
     * @throws IOException thrown by future filters only.
     * @throws ServletException thrown by future filters only.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String url = request.getRequestURI().substring(
                request.getContextPath().length());
        String[] paths = url.split("/");
        List<String> newPaths = new ArrayList<String>(Arrays.asList(paths));
        String country = "US";
        String language = "en";
        String newUrl = url;
        if (newPaths.get(LOCALE_PATH_SEGMENT).length() < 3) {
            country = newPaths.get(LOCALE_PATH_SEGMENT);
            newPaths.remove(LOCALE_PATH_SEGMENT);
            if (newPaths.get(LOCALE_PATH_SEGMENT).length() < 3) {
                language = newPaths.get(LOCALE_PATH_SEGMENT);
                newPaths.remove(LOCALE_PATH_SEGMENT);
            } else {
                language = "en";
            }
            newUrl = StringUtils.collectionToDelimitedString(newPaths, "/");
        }
        request.setAttribute(REQUEST_LOCALE_ATTRIBUTE, new Locale(language,
                country));
        if (!newUrl.equals(url)) {
            request.getRequestDispatcher(newUrl).forward(request, response);
            return;
        }
        filterChain.doFilter(request, response);
    }

}
