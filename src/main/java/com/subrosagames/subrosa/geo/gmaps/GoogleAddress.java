package com.subrosagames.subrosa.geo.gmaps;

import java.math.BigDecimal;

import com.google.code.geocoder.model.GeocoderLocationType;
import com.google.common.base.MoreObjects;

/**
 * POJO for holding address information from google's geocoding API.
 *
 * @see GoogleGeocoder
 */
public class GoogleAddress {

    private String fullAddress;
    private String streetAddress;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private boolean partialMatch;
    private GeocoderLocationType locationType;

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
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

    public boolean isPartialMatch() {
        return partialMatch;
    }

    public void setPartialMatch(boolean partialMatch) {
        this.partialMatch = partialMatch;
    }

    public GeocoderLocationType getLocationType() {
        return locationType;
    }

    public void setLocationType(GeocoderLocationType locationType) {
        this.locationType = locationType;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("fullAddress", fullAddress)
                .add("streetAddress", streetAddress)
                .add("city", city)
                .add("state", state)
                .add("country", country)
                .add("postalCode", postalCode)
                .add("latitude", latitude)
                .add("longitude", longitude)
                .add("partialMatch", partialMatch)
                .add("locationType", locationType)
                .toString();
    }

}
