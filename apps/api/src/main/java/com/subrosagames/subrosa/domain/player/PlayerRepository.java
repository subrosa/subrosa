package com.subrosagames.subrosa.domain.player;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.subrosagames.subrosa.domain.DomainObjectRepository;
import com.subrosagames.subrosa.domain.game.Game;
import com.subrosagames.subrosa.domain.player.persistence.PlayerEntity;

/**
 * Persistence repository for game players.
 */
public interface PlayerRepository extends DomainObjectRepository<PlayerEntity, Integer> {

    @Query("SELECT p "
            + " FROM PlayerEntity p "
            + "     JOIN p.team t "
            + " WHERE t.game = :game")
    Page<PlayerEntity> findByGame(@Param("game") Game game, Pageable pageable);

    @Query("SELECT p "
            + " FROM PlayerEntity p "
            + "     JOIN p.team t "
            + "     WHERE t.game = :game AND p.id = :id")
    Optional<PlayerEntity> findByGameAndId(@Param("game") Game game, @Param("id") Integer id);

}
