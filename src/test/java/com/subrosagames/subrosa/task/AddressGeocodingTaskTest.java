package com.subrosagames.subrosa.task;

import java.util.EnumMap;
import java.util.HashMap;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import com.google.code.geocoder.model.GeocoderResult;
import com.subrosagames.subrosa.domain.account.Address;
import com.subrosagames.subrosa.geo.gmaps.GoogleGeocoder;
import com.subrosagames.subrosa.geo.gmaps.MockGoogleGeocoder;
import com.subrosagames.subrosa.test.util.ForeignKeyDisablingTestListener;

import static org.junit.Assert.assertEquals;

/**
 * Test {@link AddressGeocodingTask}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/test-context.xml" })
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        ForeignKeyDisablingTestListener.class
})
public class AddressGeocodingTaskTest {

    // CHECKSTYLE-OFF: JavadocMethod

    @Autowired
    private JdbcTemplate jdbcTemplate;

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
        addressGeocodingTask.setGoogleGeocoder(mockGoogleGeocoder);
    }

    @Test
    public void testSplitAddressesIntoFields() throws Exception {
        jdbcTemplate.update("INSERT INTO address (address_id, user_provided) VALUES 1535, 'address1'");

        addressGeocodingTask.execute();

        Address address = entityManager.find(Address.class, 1535);
        assertEquals("123 Ivy Ln", address.getStreetAddress());
    }

    // CHECKSTYLE-ON: JavadocMethod
}

