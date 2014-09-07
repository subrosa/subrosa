/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.subrosa.api.servlet;

import com.subrosa.api.servlet.ResponseNoCacheFilter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for ResponseNoCacheFilter.
 */
@RunWith(MockitoJUnitRunner.class)
public class ResponseNoCacheFilterTest {
    
    private ResponseNoCacheFilter instance;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private FilterChain chain;
    
    /**
     * Instantiate a new ResponseNoCacheFilter before each test.
     */
    @Before
    public void setUp() {
        instance = new ResponseNoCacheFilter();
    }

    /**
     * Test that a GET request has its headers set.
     */
    @Test
    public void getRequest() throws IOException, ServletException {
        when(request.getMethod()).thenReturn("GET");
        
        instance.doFilter(request, response, chain);

        verify(response).setHeader("Pragma", "no-cache");
        verify(response).setHeader("Cache-Control", "no-cache");
        verify(response).setDateHeader("Expires", 0);
        verify(chain).doFilter(request, response);
    }

    /**
     * Test that a POST request does not have its headers set.
     */
    @Test
    public void postRequest() throws IOException, ServletException {
        when(request.getMethod()).thenReturn("POST");
        
        instance.doFilter(request, response, chain);

        verify(response, never()).setHeader(anyString(), anyString());
        verify(response, never()).setDateHeader(anyString(), anyLong());
        verify(chain).doFilter(request, response);
    }

}
