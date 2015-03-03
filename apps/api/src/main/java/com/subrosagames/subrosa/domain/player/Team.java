package com.subrosagames.subrosa.domain.player;

import com.subrosagames.subrosa.domain.image.Image;
import com.subrosagames.subrosa.domain.location.Location;
import com.subrosagames.subrosa.domain.player.persistence.TeamEntity;

/**
 * Model for Teams.
 */
public class Team extends TeamEntity implements Participant {

    @Override
    public Target getTarget(int targetId) throws TargetNotFoundException {
        return null;
    }

    @Override
    public void addTarget(Player target) {

    }

    @Override
    public void addTarget(Team target) {

    }

    @Override
    public void addTarget(Location target) {

    }

    @Override
    public Image getAvatar() {
        return null;
    }
}
