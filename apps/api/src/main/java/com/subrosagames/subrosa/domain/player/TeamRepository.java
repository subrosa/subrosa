package com.subrosagames.subrosa.domain.player;

import java.util.List;
import java.util.Optional;

import com.subrosagames.subrosa.domain.DomainObjectRepository;
import com.subrosagames.subrosa.domain.game.Game;
import com.subrosagames.subrosa.domain.player.persistence.TeamEntity;

/**
 * TODO
 */
public interface TeamRepository extends DomainObjectRepository<TeamEntity, Integer> {

    List<TeamEntity> findByGame(Game game);

    Optional<TeamEntity> findByGameAndId(Game game, Integer teamId);
}
