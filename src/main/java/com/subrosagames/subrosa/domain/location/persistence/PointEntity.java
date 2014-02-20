package com.subrosagames.subrosa.domain.location.persistence;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnore;
import com.subrosagames.subrosa.domain.location.Point;

/**
 */
@Entity
@Table(name = "point")
public class PointEntity implements Point {

    @JsonIgnore
    @Id
    @Column(name = "point_id")
    private Integer id;

    @Column
    private BigDecimal latitude;

    @Column
    private BigDecimal longitude;

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
}
