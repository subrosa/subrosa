package com.subrosagames.subrosa.geo.gmaps;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import com.google.code.geocoder.model.GeocoderResult;

import static org.junit.Assert.assertEquals;

/**
 * Test {@link GoogleGeocoder}.
 */
public class GoogleGeocoderTest {

    // CHECKSTYLE-OFF: JavadocMethod

    private GoogleGeocoder googleGeocoder;

    @Before
    public void setUp() throws Exception {
        googleGeocoder = new MockGoogleGeocoder(new HashMap<String, GeocoderResult>() {
            {
                put("my address", MockGoogleGeocoder.resultForAddressComponents(
                        new EnumMap<MockGoogleGeocoder.Component, String>(MockGoogleGeocoder.Component.class) {
                            {
                                put(MockGoogleGeocoder.Component.FULL_ADDRESS, "1010 Pamlico Drive, Cary, NC 27511, USA");
                                put(MockGoogleGeocoder.Component.NUMBER, "1010");
                                put(MockGoogleGeocoder.Component.ROUTE, "Pamlico Dr");
                                put(MockGoogleGeocoder.Component.CITY, "Cary");
                                put(MockGoogleGeocoder.Component.STATE, "NC");
                                put(MockGoogleGeocoder.Component.COUNTRY, "US");
                                put(MockGoogleGeocoder.Component.POSTAL_CODE, "27511");
                                put(MockGoogleGeocoder.Component.LATITUDE, "35.7709959");
                                put(MockGoogleGeocoder.Component.LONGITUDE, "-78.797066");
                            }
                        }));
            }
        });
    }

    @Test
    public void testExtractionOfAddressFromResponse() throws Exception {
        GoogleAddress address = googleGeocoder.geocode("my address");
        assertEquals(address.getFullAddress(), "1010 Pamlico Drive, Cary, NC 27511, USA");
        assertEquals(address.getStreetAddress(), "1010 Pamlico Dr");
        assertEquals(address.getCity(), "Cary");
        assertEquals(address.getState(), "NC");
        assertEquals(address.getCountry(), "US");
        assertEquals(address.getPostalCode(), "27511");
        assertEquals(address.getLatitude(), new BigDecimal("35.7709959"));
        assertEquals(address.getLongitude(), new BigDecimal("-78.797066"));
    }

    // CHECKSTYLE-ON: JavadocMethod

}
