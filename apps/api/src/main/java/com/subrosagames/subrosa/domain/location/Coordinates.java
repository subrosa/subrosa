package com.subrosagames.subrosa.domain.location;

import java.math.BigDecimal;

/**
 * Represents a point on the Earth.
 */
public class Coordinates {

    private BigDecimal latitude;
    private BigDecimal longitude;

    /**
     * Construct with latitude and longitude.
     *
     * @param latitude  latitude
     * @param longitude longitude
     */
    public Coordinates(BigDecimal latitude, BigDecimal longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }
}
