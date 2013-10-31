package com.subrosagames.subrosa.domain.player;

import com.subrosagames.subrosa.domain.player.persistence.TargetEntity;
import com.subrosagames.subrosa.domain.player.persistence.PlayerEntity;
import com.subrosagames.subrosa.domain.player.persistence.TeamEntity;

/**
 *
 */
public interface PlayerRepository {

    void createTarget(TargetEntity targetEntity);

    PlayerEntity getPlayer(Integer id);

    TeamEntity getTeam(Integer id);

    void createTeam(TeamEntity teamEntity);

    void createPlayer(PlayerEntity playerEntity) throws PlayerValidationException;
}
