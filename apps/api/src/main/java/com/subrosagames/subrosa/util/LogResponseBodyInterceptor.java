package com.subrosagames.subrosa.util;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * Request interceptor whose purpose is to log the complete rendered response body.
 */
public class LogResponseBodyInterceptor implements HandlerInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(LogResponseBodyInterceptor.class);

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                           Object o, ModelAndView modelAndView)
            throws Exception
    {
        if (LOG.isDebugEnabled()) {
            final StringBuilder stringBuilder = new StringBuilder();
            final ServletOutputStream outputStream = new ServletOutputStream() {
                @Override
                public void write(int b) throws IOException {
                    stringBuilder.append((char) b);
                }

                @Override
                public void write(byte[] b, int off, int len) throws IOException {
                    stringBuilder.append(new String(b, off, len, "UTF-8"));
                }

                @Override
                public String toString() {
                    return stringBuilder.toString();
                }

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setWriteListener(WriteListener writeListener) {
                }
            };
            HttpServletResponseWrapper responseWrapper = new HttpServletResponseWrapper(httpServletResponse) {
                @Override
                public ServletOutputStream getOutputStream() {
                    return outputStream;
                }

                @Override
                public PrintWriter getWriter() throws IOException {
                    return new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                }
            };
            LOG.debug(responseWrapper.getOutputStream().toString());
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
    }

}
