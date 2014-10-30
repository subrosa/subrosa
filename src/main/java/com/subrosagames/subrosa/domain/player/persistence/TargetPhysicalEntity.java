package com.subrosagames.subrosa.domain.player.persistence;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import com.subrosagames.subrosa.domain.location.Location;
import com.subrosagames.subrosa.domain.location.persistence.LocationEntity;
import com.subrosagames.subrosa.domain.player.TargetPhysical;

/**
 * Persisted physical target.
 */
@Entity
@Table(name = "target_physical")
@DiscriminatorValue("PHYSICAL")
@PrimaryKeyJoinColumn(name = "target_id")
public class TargetPhysicalEntity extends TargetEntity implements TargetPhysical {

    @OneToOne(targetEntity = LocationEntity.class)
    @JoinColumn(name = "location_id")
    private Location target;

    public Location getTarget() {
        return target;
    }

    public void setTarget(Location target) {
        this.target = target;
    }
}
