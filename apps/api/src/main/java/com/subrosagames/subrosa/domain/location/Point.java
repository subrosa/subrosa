package com.subrosagames.subrosa.domain.location;

import java.math.BigDecimal;

/**
 * A geographical point.
 */
public interface Point {

    /**
     * Get the latitude.
     *
     * @return latitude
     */
    BigDecimal getLatitude();

    /**
     * Get the longitude.
     *
     * @return longitude
     */
    BigDecimal getLongitude();

}
