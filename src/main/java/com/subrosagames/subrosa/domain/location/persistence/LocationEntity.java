package com.subrosagames.subrosa.domain.location.persistence;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.subrosagames.subrosa.domain.location.Location;

/**
 *
 */
@Entity
@Table(name = "location")
public class LocationEntity implements Location {

    @Id
    @SequenceGenerator(name = "locationSeq", sequenceName = "location_location_id_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "locationSeq")
    @Column(name = "location_id")
    private Integer id;

    @Column
    private BigDecimal latitude;

    @Column
    private BigDecimal longitude;

    @Column
    private Boolean approximate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Boolean getApproximate() {
        return approximate;
    }

    public void setApproximate(Boolean approximate) {
        this.approximate = approximate;
    }
}
