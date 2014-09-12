package com.subrosa.api.servlet;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Formatter;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subrosa.api.notification.GeneralCode;

import static javax.servlet.http.HttpServletResponse.SC_NOT_ACCEPTABLE;

/**
 * Servlet filter that checks for the existence of an Accept header in the
 * request, injecting one if it is absent and rejecting the request if the
 * header specifies an unsupported MIME type.
 * <p/>
 * <p>This implementation will first attempt to match the Accept header with the
 * content type of the request body such that the response will be marshalled in
 * the same format as the request. Otherwise, it will fall back to a default
 * that can be configured via the <code>defaultMimeType</code> init parameter.
 * By default, that fallback is JSON.</p>
 * <p/>
 * <p>The list of MIME types supported by the filter may be configured via the
 * <code>supportedMimeTypes</code> init parameter or by specifying an array of
 * MIME types.</p>
 * <p/>
 * <p>Developers may subclass this filter to provide custom logic for
 * determining the MIME type to inject or whether to reject a request with an
 * unsupported Accept header.  For example, certain URL paths may respond
 * with a specific format or be more lenient with the Accept header.</p>
 */
public class AcceptHeaderFilter implements Filter {

    // SUPPRESS CHECKSTYLE JavadocVariable NEXT 6 LINES
    protected static final String ACCEPT_HEADER = "Accept";
    protected static final String MIMETYPE_JSON = "application/json";
    protected static final String MIMETYPE_XML = "application/xml";
    protected static final String MIMETYPE_TEXT_XML = "text/xml";
    protected static final String DEFAULT_MIMETYPE_PARAM = "defaultMimeType";
    protected static final String SUPPORTED_MIMETYPES_PARAM = "supportedMimeTypes";

    private static final String JSON_406_FORMAT = "{\"notifications\": [{"
            + "\"code\": %s,\"severity\": \"ERROR\",\"text\": \"%s\","
            + "\"details\": {\"supportedTypes\": \"%s\"}}]}";

    private static final Logger LOG = LoggerFactory.getLogger(AcceptHeaderFilter.class);

    private String[] supportedMimeTypes = { MIMETYPE_JSON, MIMETYPE_XML, MIMETYPE_TEXT_XML };

    private String defaultMimeType = MIMETYPE_JSON;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String configMimeType = filterConfig.getInitParameter(DEFAULT_MIMETYPE_PARAM);
        if (configMimeType != null) {
            LOG.debug("Default MIME type set to {} per web.xml init-param", configMimeType);
            this.defaultMimeType = configMimeType;
        }

        String configSupportedMimeTypes = filterConfig.getInitParameter(SUPPORTED_MIMETYPES_PARAM);
        if (configSupportedMimeTypes != null) {
            setSupportedMimeTypesString(configSupportedMimeTypes);
            LOG.debug("Supported MIME types set to {} per web.xml init-param", StringUtils.join(supportedMimeTypes, ","));
        }
    }

    @Override
    public final void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException
    {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String incomingAcceptHeader = httpRequest.getHeader(ACCEPT_HEADER);
        LOG.debug("Accept header is '{}'", incomingAcceptHeader);

        String computedAcceptHeader = null;
        boolean emptyAccept = StringUtils.isEmpty(incomingAcceptHeader)
                || incomingAcceptHeader.equals("*/*");

        if (emptyAccept) {
            computedAcceptHeader = getAcceptMimeType(httpRequest);
        } else if (!isMimeTypeSupported(incomingAcceptHeader)) {
            if (rejectOnUnsupported(httpRequest)) {
                LOG.info("Request to {} has unsupported Accept header of '{}'. Responding with HTTP 406.",
                        httpRequest.getRequestURI(), incomingAcceptHeader);
                send406Json(response);
                return;
            } else {
                computedAcceptHeader = getAcceptMimeType(httpRequest);
            }
        }

        if (computedAcceptHeader != null) {
            LOG.debug("Wrapping request to inject 'Accept: {}'", computedAcceptHeader);
            httpRequest = new RequestWrapper(httpRequest, computedAcceptHeader);
        }

        chain.doFilter(httpRequest, response);
    }

    private void send406Json(ServletResponse response) throws IOException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setStatus(SC_NOT_ACCEPTABLE);
        httpResponse.setContentType(MIMETYPE_JSON);
        Writer out = httpResponse.getWriter();
        Formatter formatter = new Formatter(out);
        formatter.format(JSON_406_FORMAT, GeneralCode.NOT_ACCEPTABLE.getCode(),
                GeneralCode.NOT_ACCEPTABLE.getDefaultMessage(),
                StringUtils.join(supportedMimeTypes, ", "));
        out.flush();
        out.close();
    }


    /**
     * Compare a given accept header to the list of accepted mime types.
     *
     * @param requestMimeType accept header value to compare
     * @return true if acceptable, false if not
     */
    private boolean isMimeTypeSupported(String requestMimeType) {
        for (String mimeType : supportedMimeTypes) {
            if (requestMimeType.contains(mimeType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines the MIME type to inject into the Accept header when such
     * header is not present on the initial request. Subclasses should override
     * this method rather than doFilter to customize the MIME type. The default
     * implementation is to use the content type of the request body if the
     * request is a POST, else use the default as returned by
     * {@link #getDefaultMimeType()}.
     *
     * @param request the HttpServletRequest so it can be inspected
     * @return the MIME type to inject into the Accept header or
     * <code>null</code> if no header should be injected.
     */
    protected String getAcceptMimeType(HttpServletRequest request) {
        String method = request.getMethod();
        if (method.equals("POST")) {
            String contentType = request.getContentType();
            LOG.debug("POST request has Content-type of '{}'", contentType);
            if (contentType != null) {
                if (contentType.startsWith(MIMETYPE_JSON)) {
                    return MIMETYPE_JSON;
                } else if (contentType.startsWith(MIMETYPE_XML)
                        || contentType.startsWith(MIMETYPE_TEXT_XML)) {
                    return MIMETYPE_XML;
                }
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Will use default MIME type of '{}'", getDefaultMimeType());
        }
        return getDefaultMimeType();
    }

    /**
     * Indicates whether a request should be rejected if it has an Accept
     * header with an unsupported MIME type.  If this method returns true, the
     * filter will respond with a HTTP 406 error.  If false, the filter should
     * handle an unsupported Accept header as though it were empty.  The default
     * implementation always returns true.
     *
     * @param request the HttpServletRequest being inspected
     * @return true if the request should be rejected
     */
    protected boolean rejectOnUnsupported(HttpServletRequest request) {
        return true;
    }

    /**
     * Sets the MIME type to inject into the Accept header if one cannot be
     * determined from the content type of the request. The default value is
     * <code>application/json</code>. (This setter is public to permit
     * configuration via Spring rather than web.xml if desired.)
     *
     * @param mimeType the default MIME type to use
     */
    public void setDefaultMimeType(String mimeType) {
        this.defaultMimeType = mimeType;
    }

    /**
     * Returns the default MIME type to inject into the Accept header.
     *
     * @return the default MIME type
     */
    protected String getDefaultMimeType() {
        return this.defaultMimeType;
    }

    /**
     * Sets the supported MIME types for this filter.  The default is
     * <code>{application/json, application/xml, text/xml}</code>.
     *
     * @param supportedMimeTypes an array of MIME types to support
     */
    public void setSupportedMimeTypes(String[] supportedMimeTypes) {
        this.supportedMimeTypes = supportedMimeTypes.clone();
    }

    /**
     * Sets the supported MIME types as a comma- or whitespace-delimited string.
     *
     * @param supportedMimeTypesString the list of supported MIME types
     */
    public void setSupportedMimeTypesString(String supportedMimeTypesString) {
        if (StringUtils.isEmpty(supportedMimeTypesString)) {
            this.supportedMimeTypes = new String[0];
        } else {
            this.supportedMimeTypes = supportedMimeTypesString.split("[,\\s]+");
        }
    }

    @Override
    public void destroy() {
    }

    /**
     * Implementation of HttpServletRequestWrapper that adds an Accept header
     * to the wrapped request.
     */
    static class RequestWrapper extends HttpServletRequestWrapper {

        private String mimeType;

        RequestWrapper(HttpServletRequest request, String mimeType) {
            super(request);
            this.mimeType = mimeType;
        }

        @Override
        public String getHeader(String name) {
            return name.equalsIgnoreCase(ACCEPT_HEADER)
                    ? this.mimeType
                    : super.getHeader(name);
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        @Override
        public Enumeration getHeaderNames() {
            // Have to use raw types because that's what the Java EE API uses.
            List headerNames = Collections.list(super.getHeaderNames());
            if (!headerNames.contains(ACCEPT_HEADER)) {
                headerNames.add(ACCEPT_HEADER);
            }
            return Collections.enumeration(headerNames);
        }

        @SuppressWarnings("rawtypes")
        @Override
        public Enumeration getHeaders(String name) {
            if (name.equalsIgnoreCase(ACCEPT_HEADER)) {
                List<String> headers = new ArrayList<String>(1);
                headers.add(this.mimeType);
                return Collections.enumeration(headers);
            } else {
                return super.getHeaders(name);
            }
        }

    }
}
