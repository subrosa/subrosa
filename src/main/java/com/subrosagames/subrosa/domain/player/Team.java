package com.subrosagames.subrosa.domain.player;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.subrosagames.subrosa.domain.location.Location;
import com.subrosagames.subrosa.domain.player.persistence.PlayerEntity;
import com.subrosagames.subrosa.domain.player.persistence.TeamEntity;

/**
 * Model for Teams.
 */
public class Team implements Participant {

    private TeamEntity teamEntity;

    public Team(TeamEntity teamEntity) {
        this.teamEntity = teamEntity;
    }

    public Integer getId() {
        return teamEntity.getId();
    }

    @Override
    public String getName() {
        return teamEntity.getName();
    }

    @Override
    public List<? extends Target> getTargets() {
        return teamEntity.getTargets();
    }

    @Override
    public Target getTarget(int targetId) throws TargetNotFoundException {
        return null;  // TODO
    }

    @Override
    public void addTarget(Player target) {
        // TODO
    }

    @Override
    public void addTarget(Team target) {
        // TODO
    }

    @Override
    public void addTarget(Location target) {
        // TODO
    }

    public List<? extends Player> getPlayers() {
        return teamEntity.getPlayers();
    }
}
