package com.subrosa.web.view.il8n;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;

/**
 * Test the localeResolver.
 */
public class LocaleResolverTest {

    /**
     * Test the resolver.  Get the right locale from the request.
     */
    @Test
    public void testResolveLocale() {
        Locale locale = new Locale("US", "en");
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.doReturn(locale).when(request).getAttribute(LocaleDetectionFilter.REQUEST_LOCALE_ATTRIBUTE);

        LocaleResolver resolver = new LocaleResolver();

        assertEquals(locale, resolver.resolveLocale(request));
    }
}
