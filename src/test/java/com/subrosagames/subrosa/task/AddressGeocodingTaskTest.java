package com.subrosagames.subrosa.task;

import java.util.EnumMap;
import java.util.HashMap;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.code.geocoder.model.GeocoderResult;
import com.subrosagames.subrosa.bootstrap.GoogleIntegration;
import com.subrosagames.subrosa.domain.account.Address;
import com.subrosagames.subrosa.geo.gmaps.GoogleGeocoder;
import com.subrosagames.subrosa.geo.gmaps.MockGoogleGeocoder;
import com.subrosagames.subrosa.test.AbstractContextTest;

import static org.junit.Assert.assertEquals;

/**
 * Test {@link AddressGeocodingTask}.
 */
public class AddressGeocodingTaskTest extends AbstractContextTest {

    // CHECKSTYLE-OFF: JavadocMethod

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AddressGeocodingTask addressGeocodingTask;

    @Before
    public void setUp() throws Exception {
        GoogleGeocoder mockGoogleGeocoder = new MockGoogleGeocoder(new HashMap<String, GeocoderResult>() {
            {
                put("address1", MockGoogleGeocoder.resultForAddressComponents(
                        new EnumMap<MockGoogleGeocoder.Component, String>(MockGoogleGeocoder.Component.class) {
                            {
                                put(MockGoogleGeocoder.Component.FULL_ADDRESS, "this is formatted");
                                put(MockGoogleGeocoder.Component.NUMBER, "123");
                                put(MockGoogleGeocoder.Component.ROUTE, "Ivy Ln");
                                put(MockGoogleGeocoder.Component.CITY, "London");
                                put(MockGoogleGeocoder.Component.COUNTRY, "GB");
                                put(MockGoogleGeocoder.Component.POSTAL_CODE, "A17 2X3");
                            }
                        }));
            }
        });
        mockGoogleGeocoder.setGoogleIntegration(new GoogleIntegration());
        addressGeocodingTask.setGoogleGeocoder(mockGoogleGeocoder);
    }

    @Transactional
    @Test
    public void testSplitAddressesIntoFields() throws Exception {
        Address address = new Address();
        address.setUserProvided("address1");
        entityManager.persist(address);

        addressGeocodingTask.execute();

        address = entityManager.find(Address.class, address.getId());
        assertEquals("123 Ivy Ln", address.getStreetAddress());
    }

    // CHECKSTYLE-ON: JavadocMethod
}

