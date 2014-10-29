package com.subrosagames.subrosa.domain.location;

import java.util.List;

/**
 * A game zone, represented by an ordered collection of geographical points.
 */
public interface Zone {

    /**
     * Get list of geographical points.
     *
     * @return list of points
     */
    List<Point> getPoints();
}
