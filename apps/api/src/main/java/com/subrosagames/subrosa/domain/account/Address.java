package com.subrosagames.subrosa.domain.account;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.subrosagames.subrosa.domain.location.Location;
import com.subrosagames.subrosa.domain.location.persistence.LocationEntity;
import com.subrosagames.subrosa.infrastructure.persistence.hibernate.BaseEntity;

/**
 * Used to represent an accounts addresses.
 */
@Entity
public class Address extends BaseEntity {

    @Id
    @SequenceGenerator(name = "addressSeq", sequenceName = "address_address_id_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "addressSeq")
    @Column(name = "address_id")
    private Integer id;

    @JsonIgnore
    @ManyToOne
    @JoinTable(
            name = "account_address",
            inverseJoinColumns = @JoinColumn(name = "account_id"),
            joinColumns = @JoinColumn(name = "address_id")
    )
    private Account account;

    @Column
    @NotBlank
    @Length(max = 128)
    private String label;

    @OneToOne(targetEntity = LocationEntity.class)
    @JoinColumn(name = "location_id")
    private Location location;

    @NotBlank
    @Column(name = "full_address")
    private String fullAddress;

    @Column(name = "user_provided")
    private String userProvided;

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

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
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
