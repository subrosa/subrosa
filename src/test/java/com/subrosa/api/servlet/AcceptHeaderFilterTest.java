package com.subrosa.api.servlet;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for AcceptHeaderFilter.
 */
@RunWith(MockitoJUnitRunner.class)
public class AcceptHeaderFilterTest {

    // CHECKSTYLE-OFF: JavadocMethod

    private AcceptHeaderFilter instance;
    @Mock
    private FilterConfig filterConfig;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain chain;
    @Captor
    private ArgumentCaptor<HttpServletRequest> requestCaptor;

    /**
     * Instantiates the instance of AcceptHeaderFilter to test.
     */
    @Before
    public void setUp() {
        instance = new AcceptHeaderFilter();
    }

    /**
     * Test init method when the defaultMimeType init parameter is not set.
     */
    @Test
    public void initNoParam() {
        when(filterConfig.getInitParameter(AcceptHeaderFilter.DEFAULT_MIMETYPE_PARAM))
                .thenReturn(null);

        instance.init(filterConfig);
        String result = instance.getDefaultMimeType();
        assertEquals(AcceptHeaderFilter.MIMETYPE_JSON, result);
    }

    /**
     * Test init method when the defaultMimeType init parameter is set.
     */
    @Test
    public void initWithParam() {
        when(filterConfig.getInitParameter(AcceptHeaderFilter.DEFAULT_MIMETYPE_PARAM))
                .thenReturn("application/xml");

        instance.init(filterConfig);
        String result = instance.getDefaultMimeType();
        assertEquals("application/xml", result);
    }

    /**
     * Test getAcceptMimeType method when passed a GET request. Expected result
     * is JSON.
     */
    @Test
    public void getAcceptMimeTypeWithGetRequest() {
        when(request.getMethod()).thenReturn("GET");

        String result = instance.getAcceptMimeType(request);
        assertEquals(AcceptHeaderFilter.MIMETYPE_JSON, result);
    }

    /**
     * Test getAcceptMimeType method when passed a POST request. Expected result
     * is whatever is in the Content-type of the request.
     */
    @Test
    public void getAcceptMimeTypeWithPostRequest() {
        when(request.getMethod()).thenReturn("POST");
        when(request.getContentType()).thenReturn("application/xml; charset=UTF-8");

        String result = instance.getAcceptMimeType(request);
        assertEquals("application/xml", result);
    }

    /**
     * Test getAcceptMimeType method when passed a POST request without a
     * Content-type header. Expected result is JSON.
     */
    @Test
    public void getAcceptMimeTypeWithNoContentType() {
        when(request.getMethod()).thenReturn("POST");
        when(request.getContentType()).thenReturn(null);

        String result = instance.getAcceptMimeType(request);
        assertEquals(AcceptHeaderFilter.MIMETYPE_JSON, result);
    }

    /**
     * Test getAcceptMimeType method when passed a POST request with a
     * Content-type that isn't XML or JSON. Expected result is JSON.
     */
    @Test
    public void getAcceptMimeTypeWithOtherContentType() {
        when(request.getMethod()).thenReturn("POST");
        when(request.getContentType()).thenReturn("multipart/form-data");

        String result = instance.getAcceptMimeType(request);
        assertEquals(AcceptHeaderFilter.MIMETYPE_JSON, result);
    }

    /**
     * Test doFilter method with a request that includes an Accept header.
     * Expect that the filter chain is called with the original request.
     */
    @Test
    public void doFilterWithExistingAccept() throws Exception {
        when(request.getHeader(AcceptHeaderFilter.ACCEPT_HEADER)).thenReturn("application/xml");

        instance.doFilter(request, response, chain);
        verify(chain).doFilter(request, response);
    }

    /**
     * Test doFilter method with a request lacking an Accept header. Expect that
     * the filter chain is called with a wrapped request which returns an
     * Accept header of JSON.
     */
    @Test
    public void doFilterWithoutAccept() throws Exception {
        when(request.getHeader(AcceptHeaderFilter.ACCEPT_HEADER)).thenReturn(null);
        when(request.getMethod()).thenReturn("GET");

        instance.doFilter(request, response, chain);

        verify(chain).doFilter(requestCaptor.capture(), eq(response));
        assertNotSame(request, requestCaptor.getValue());
        assertEquals(AcceptHeaderFilter.MIMETYPE_JSON,
                requestCaptor.getValue().getHeader(AcceptHeaderFilter.ACCEPT_HEADER));
    }

    /**
     * Test doFilter method with a request lacking an Accept header where the
     * getAcceptMimeType method returns null, as might happen in subclasses
     * which override getAcceptMimeType. Simulated here by setting the default
     * to null. Expect that the filter does not wrap the request.
     */
    @Test
    public void doFilterWithNullDefault() throws Exception {
        when(request.getHeader(AcceptHeaderFilter.ACCEPT_HEADER)).thenReturn(null);
        when(request.getMethod()).thenReturn("GET");

        instance.setDefaultMimeType(null);
        instance.doFilter(request, response, chain);
        verify(chain).doFilter(request, response);
    }

    /**
     * Test doFilter method with a request with an unsupported MIME type in the
     * Accept header. Expect that the filter rejects the request and sends a
     * HTTP 406 with a notification list in the response.
     */
    @Test
    public void doFilterWithUnsupportedType() throws Exception {
        when(request.getHeader(AcceptHeaderFilter.ACCEPT_HEADER)).thenReturn("bogus/type");
        when(request.getMethod()).thenReturn("GET");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        when(response.getWriter()).thenReturn(new PrintWriter(baos));

        instance.doFilter(request, response, chain);
        verify(chain, never()).doFilter(any(HttpServletRequest.class), any(HttpServletResponse.class));
        verify(response).setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
        String responseBody = baos.toString();
        String mimeTypeString = AcceptHeaderFilter.MIMETYPE_JSON + ", "
                + AcceptHeaderFilter.MIMETYPE_XML + ", "
                + AcceptHeaderFilter.MIMETYPE_TEXT_XML;
        assertTrue("Response body lists supported types", responseBody.contains(mimeTypeString));
    }

    /**
     * Test doFilter method with a request with an unsupported MIME type in the
     * Accept header, first configuring the supported MIME types.
     * Expect that the filter rejects the request and sends a
     * HTTP 406 with a notification list in the response.
     */
    @Test
    public void doFilterWithUnsupportedTypeAndCustomSupported() throws Exception {
        when(filterConfig.getInitParameter(AcceptHeaderFilter.SUPPORTED_MIMETYPES_PARAM))
                .thenReturn("application/json,application/xml, image/*  foo/bar");
        when(request.getHeader(AcceptHeaderFilter.ACCEPT_HEADER)).thenReturn("bogus/type");
        when(request.getMethod()).thenReturn("GET");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        when(response.getWriter()).thenReturn(new PrintWriter(baos));

        instance.init(filterConfig);
        instance.doFilter(request, response, chain);
        verify(chain, never()).doFilter(any(HttpServletRequest.class), any(HttpServletResponse.class));
        verify(response).setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
        String responseBody = baos.toString();
        String mimeTypeString = "application/json, application/xml, image/*, foo/bar";
        assertTrue("Response body lists supported types", responseBody.contains(mimeTypeString));
    }

    /**
     * Test doFilter method with a request with an unsupported MIME type in the
     * Accept header, with the rejectOnUnsupported error overridden to return
     * false. Expect that the filter wraps the request as though the Accept
     * header were absent.
     */
    @Test
    public void doFilterWithUnsupportedTypeNoReject() throws Exception {
        when(request.getHeader(AcceptHeaderFilter.ACCEPT_HEADER)).thenReturn("bogus/type");
        when(request.getMethod()).thenReturn("GET");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        when(response.getWriter()).thenReturn(new PrintWriter(baos));

        instance = new AcceptHeaderFilter() {
            @Override
            protected boolean rejectOnUnsupported(HttpServletRequest req) {
                return false;
            }
        };
        instance.doFilter(request, response, chain);
        verify(chain).doFilter(requestCaptor.capture(), eq(response));
        assertNotSame(request, requestCaptor.getValue());
        assertEquals(AcceptHeaderFilter.MIMETYPE_JSON,
                requestCaptor.getValue().getHeader(AcceptHeaderFilter.ACCEPT_HEADER));
    }

    // CHECKSTYLE-ON: JavadocMethod

}
