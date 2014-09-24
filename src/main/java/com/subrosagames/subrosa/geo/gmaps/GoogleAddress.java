package com.subrosagames.subrosa.geo.gmaps;

import java.math.BigDecimal;

import org.apache.commons.lang.ObjectUtils;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class GoogleAddress {

    private String fullAddress;
    private String streetAddress;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private BigDecimal latitude;
    private BigDecimal longitude;

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
                .toString();
    }
}
