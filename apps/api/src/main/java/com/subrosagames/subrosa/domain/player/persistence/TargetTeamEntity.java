package com.subrosagames.subrosa.domain.player.persistence;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import com.subrosagames.subrosa.domain.image.Image;
import com.subrosagames.subrosa.domain.player.TargetTeam;
import lombok.Getter;
import lombok.Setter;

/**
 * Persisted targeted team.
 */
@Entity
@Table(name = "target_team")
@DiscriminatorValue("TEAM")
@PrimaryKeyJoinColumn(name = "target_id")
public class TargetTeamEntity extends TargetEntity implements TargetTeam {

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "team_id")
    @Getter
    @Setter
    private TeamEntity target;

    @Override
    public Image getImage() {
        return target.getImage();
    }

}
