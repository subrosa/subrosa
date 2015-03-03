package com.subrosagames.subrosa.domain.location.persistence;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.subrosagames.subrosa.domain.location.Point;
import com.subrosagames.subrosa.domain.location.Zone;

/**
 * Persisted game zone.
 */
@Entity
@Table(name = "zone")
public class ZoneEntity implements Zone {

    @JsonIgnore
    @Id
    @Column(name = "zone_id")
    private Integer id;

    @OneToMany(fetch = FetchType.EAGER, targetEntity = PointEntity.class)
    @JoinTable(
            name = "zone_point",
            joinColumns = @JoinColumn(name = "zone_id"),
            inverseJoinColumns = @JoinColumn(name = "point_id")
    )
    private List<Point> points;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }
}
