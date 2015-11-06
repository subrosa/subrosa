package com.subrosagames.subrosa.domain.player;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.subrosagames.subrosa.domain.DomainObjectRepository;
import com.subrosagames.subrosa.domain.game.Game;

/**
 * Persistence repository for game players.
 */
public interface PlayerRepository extends DomainObjectRepository<Player, Integer> {

    List<Player> findByGame(Game game);

    Page<Player> findByGame(Game game, Pageable pageable);

    Optional<Player> findByGameAndId(Game game, Integer id);

}
