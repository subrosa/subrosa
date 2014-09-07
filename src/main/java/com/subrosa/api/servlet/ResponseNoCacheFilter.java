package com.subrosa.api.servlet;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet filter that sets response headers to prevent caching for GET requests.
 */
public class ResponseNoCacheFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
    throws IOException, ServletException
    {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        if (httpRequest.getMethod().equals("GET")) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            // Set standard HTTP/1.0 no-cache header.
            httpResponse.setHeader("Pragma", "no-cache");
            // Set standard HTTP/1.1 no-cache headers.
            httpResponse.setHeader("Cache-Control", "no-cache");
            // Set to expire far in the past.
            httpResponse.setDateHeader("Expires", 0);
        }
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}

}
