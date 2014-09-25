package com.subrosagames.subrosa.api.admin;

import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.subrosagames.subrosa.domain.DomainObjectNotFoundException;
import com.subrosagames.subrosa.domain.DomainObjectValidationException;
import com.subrosagames.subrosa.domain.account.Address;
import com.subrosagames.subrosa.domain.game.GameNotFoundException;
import com.subrosagames.subrosa.domain.game.GameRepository;
import com.subrosagames.subrosa.domain.game.persistence.GameEntity;
import com.subrosagames.subrosa.domain.location.Zone;
import com.subrosagames.subrosa.domain.location.persistence.LocationEntity;
import com.subrosagames.subrosa.geo.gmaps.GoogleAddress;
import com.subrosagames.subrosa.geo.gmaps.GoogleGeocoder;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Contains the triggers for various data migrations.
 */
@Controller
@RequestMapping("/admin/migrate")
public class MigrationController {

    private static final Logger LOG = LoggerFactory.getLogger(MigrationController.class);

    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private GoogleGeocoder googleGeocoder;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @RequestMapping("/splitAddressesIntoFields")
    @ResponseBody
    public String splitAddressesIntoFields() throws Exception {
        List<Address> addresses = jdbcTemplate.query("SELECT * FROM address WHERE user_provided IS NOT NULL",
                new BeanPropertyRowMapper<Address>(Address.class) {
                    @Override
                    protected Object getColumnValue(ResultSet rs, int index, PropertyDescriptor pd) throws SQLException {
                        return super.getColumnValue(rs, index, pd);
                    }
                });
        for (Address address : addresses) {
            LOG.debug("Geocoding user-provided address <{}> for address id {}", address.getUserProvided(), address.getAddressId());
            GoogleAddress googleAddress = googleGeocoder.geocode(address.getUserProvided());
            LOG.debug("Found address {}", googleAddress.toString());
            address.setFullAddress(googleAddress.getFullAddress());
            address.setStreetAddress(googleAddress.getStreetAddress());
            address.setCity(googleAddress.getCity());
            address.setState(googleAddress.getState());
            address.setCountry(googleAddress.getCountry());
            address.setPostalCode(googleAddress.getPostalCode());
            jdbcTemplate.update("UPDATE address SET " +
                            " full_address = ?, street_address = ?, city = ?, " +
                            " state = ?, country = ?, postal_code = ?" +
                            " WHERE address_id = ?",
                    googleAddress.getFullAddress(), googleAddress.getStreetAddress(), googleAddress.getCity(),
                    googleAddress.getState(), googleAddress.getCountry(), googleAddress.getPostalCode(),
                    address.getAddressId());
        }
        return "OK";
    }

    @RequestMapping("/populateGameLocations")
    @ResponseBody
    public String populateGameLocations() throws DomainObjectNotFoundException, DomainObjectValidationException {
        List<GameEntity> games = gameRepository.list(0, 0, "zones");
        for (GameEntity game : games) {
            Point centroid = getGameCentroid(game);
            if (centroid != null) {
                LocationEntity location = new LocationEntity();
                location.setLatitude(new BigDecimal(centroid.getCoordinate().x));
                location.setLongitude(new BigDecimal(centroid.getCoordinate().y));
                location.setApproximate(true);
                gameRepository.create(location);
                game.setLocation(location);
                gameRepository.update(game);
            }
        }
        return "OK";
    }

    private Point getGameCentroid(GameEntity game) throws GameNotFoundException {
        GeometryFactory geometryFactory = new GeometryFactory();
        List<Zone> zones = gameRepository.getZonesForGame(game.getUrl());
        for (Zone zone : zones) {
            Coordinate[] coordinates = new Coordinate[zone.getPoints().size() + 1];
            List<com.subrosagames.subrosa.domain.location.Point> points = zone.getPoints();
            int i = 0;
            for (com.subrosagames.subrosa.domain.location.Point point : points) {
                coordinates[i++] = new Coordinate(point.getLatitude().doubleValue(), point.getLongitude().doubleValue());
            }
            coordinates[i] = new Coordinate(zone.getPoints().get(0).getLatitude().doubleValue(), zone.getPoints().get(0).getLongitude().doubleValue());
            Polygon polygon = geometryFactory.createPolygon(geometryFactory.createLinearRing(coordinates), null);
            return polygon.getCentroid();
        }
        return null;
    }

    public void setGameRepository(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public void setGoogleGeocoder(GoogleGeocoder googleGeocoder) {
        this.googleGeocoder = googleGeocoder;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
