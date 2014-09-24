package com.subrosagames.subrosa.api.admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.EnumMap;
import java.util.HashMap;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.test.annotation.Rollback;

import com.google.code.geocoder.model.GeocoderResult;
import com.subrosagames.subrosa.api.web.AbstractApiControllerTest;
import com.subrosagames.subrosa.domain.account.Address;
import com.subrosagames.subrosa.geo.gmaps.GoogleGeocoder;
import com.subrosagames.subrosa.geo.gmaps.MockGoogleGeocoder;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test {@link MigrationController}.
 */
public class MigrationControllerTest extends AbstractApiControllerTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private MigrationController migrationController;

    @Before
    public void setUp() throws Exception {
        GoogleGeocoder mockGoogleGeocoder = new MockGoogleGeocoder(new HashMap<String, GeocoderResult>() {
            {
                put("address1", MockGoogleGeocoder.resultForAddressComponents(new EnumMap<MockGoogleGeocoder.Component, String>(MockGoogleGeocoder.Component.class) {
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
        migrationController.setGoogleGeocoder(mockGoogleGeocoder);

    }

    @Test
    public void testSplitAddressesIntoFields() throws Exception {
        jdbcTemplate.update("INSERT INTO address (address_id, user_provided) VALUES 1535, 'address1'");

        perform(get("/admin/migrate/splitAddressesIntoFields"))
                .andExpect(status().isOk());

        Address address = entityManager.find(Address.class, 1535);
        assertEquals("123 Ivy Ln", address.getStreetAddress());
    }
}