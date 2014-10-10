package com.subrosagames.subrosa.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter that logs the response body for the request.
 */
public class LogResponseServletFilter extends OncePerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(LogResponseServletFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final CopyPrintWriter writer = new CopyPrintWriter(response.getWriter());
        filterChain.doFilter(request, new HttpServletResponseWrapper((HttpServletResponse) response) {
            @Override public PrintWriter getWriter() {
                return writer;
            }
        });
        LOG.debug(writer.getCopy());
    }

    static class CopyPrintWriter extends PrintWriter {

        private StringBuilder copy = new StringBuilder();

        public CopyPrintWriter(Writer writer) {
            super(writer);
        }

        @Override
        public void write(int c) {
            copy.append((char) c); // It is actually a char, not an int.
            super.write(c);
        }

        @Override
        public void write(char[] chars, int offset, int length) {
            copy.append(chars, offset, length);
            super.write(chars, offset, length);
        }

        @Override
        public void write(String string, int offset, int length) {
            copy.append(string, offset, length);
            super.write(string, offset, length);
        }

        public String getCopy() {
            return copy.toString();
        }

    }
}
