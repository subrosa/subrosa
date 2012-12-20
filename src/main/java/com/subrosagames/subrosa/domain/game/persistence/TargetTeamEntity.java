package com.subrosagames.subrosa.domain.game.persistence;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import com.subrosagames.subrosa.domain.game.TargetTeam;
import com.subrosagames.subrosa.domain.image.Image;
import com.subrosagames.subrosa.domain.player.persistence.TeamEntity;

/**
 *
 */
@Entity
@Table(name = "target_team")
@DiscriminatorValue("TEAM")
@PrimaryKeyJoinColumn(name = "target_id")
public class TargetTeamEntity extends TargetEntity implements TargetTeam {

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "team_id")
    private TeamEntity target;

    public TeamEntity getTarget() {
        return target;
    }

    public void setTarget(TeamEntity target) {
        this.target = target;
    }

    @Override
    public Image getImage() {
        return target.getImage();
    }
}
