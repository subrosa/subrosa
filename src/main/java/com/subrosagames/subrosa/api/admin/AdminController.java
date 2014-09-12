package com.subrosagames.subrosa.api.admin;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.subrosagames.subrosa.domain.DomainObjectNotFoundException;
import com.subrosagames.subrosa.domain.DomainObjectValidationException;
import com.subrosagames.subrosa.domain.game.GameNotFoundException;
import com.subrosagames.subrosa.domain.game.GameRepository;
import com.subrosagames.subrosa.domain.game.persistence.GameEntity;
import com.subrosagames.subrosa.domain.location.Zone;
import com.subrosagames.subrosa.domain.location.persistence.LocationEntity;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private GameRepository gameRepository;

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

}
