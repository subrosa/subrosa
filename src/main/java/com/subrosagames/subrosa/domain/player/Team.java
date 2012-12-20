package com.subrosagames.subrosa.domain.player;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.subrosagames.subrosa.domain.game.Participant;
import com.subrosagames.subrosa.domain.game.Target;
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

    @Override
    public String getName() {
        return teamEntity.getName();
    }

    @Override
    public List<? extends Target> getTargets() {
        return teamEntity.getTargets();
    }

    public List<? extends Player> getPlayers() {
        return Lists.transform(teamEntity.getPlayers(), new Function<PlayerEntity, Player>() {
            @Override
            public Player apply(@Nullable PlayerEntity input) {
                return new Player(input);
            }
        });
    }
}
