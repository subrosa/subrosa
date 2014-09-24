package com.subrosagames.subrosa.domain.account;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

/**
 * Used to represent an accounts addresses.
 */
@Entity
public class Address {

    @Id
    @SequenceGenerator(name = "addressSeq", sequenceName = "address_address_id_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "addressSeq")
    @Column(name = "address_id")
    private int addressId;

    @Enumerated(EnumType.STRING)
    @Column(name = "address_type")
    private AddressType addressType;

    @Column(name = "full_address")
    private String fullAddress;

    @Column(name = "user_provided")
    private String userProvided;

    @Column
    private Date created;

    @Column
    private Date modified;

    @Column(name = "street_address")
    private String streetAddress;

    @Column(name = "street_address_2")
    private String streetContinued;

    @Column
    private String city;

    @Column
    private String state;

    @Column
    private String country;

    @Column(name = "postal_code")
    private String postalCode;

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int id) {
        this.addressId = id;
    }

    public AddressType getAddressType() {
        return addressType;
    }

    public void setAddressType(AddressType addressType) {
        this.addressType = addressType;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getUserProvided() {
        return userProvided;
    }

    public void setUserProvided(String userProvided) {
        this.userProvided = userProvided;
    }

    public Date getCreated() {
        return created == null ? null : new Date(created.getTime());
    }

    public void setCreated(Date created) {
        this.created = created == null ? null : new Date(created.getTime());
    }

    public Date getModified() {
        return modified == null ? null : new Date(modified.getTime());
    }

    public void setModified(Date modified) {
        this.modified = modified == null ? null : new Date(modified.getTime());
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getStreetContinued() {
        return streetContinued;
    }

    public void setStreetContinued(String streetContinued) {
        this.streetContinued = streetContinued;
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
}
