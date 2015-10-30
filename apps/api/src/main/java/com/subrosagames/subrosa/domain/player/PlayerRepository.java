package com.subrosagames.subrosa.domain.player;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.subrosagames.subrosa.domain.game.Game;
import com.subrosagames.subrosa.domain.player.persistence.PlayerAttribute;
import com.subrosagames.subrosa.domain.player.persistence.PlayerEntity;
import com.subrosagames.subrosa.domain.player.persistence.TargetEntity;
import com.subrosagames.subrosa.domain.player.persistence.TeamEntity;

/**
 * Persistence repository for game players.
 */
public interface PlayerRepository {

    @Query("SELECT p "
            + " FROM PlayerEntity p "
            + "     JOIN p.team t "
            + " WHERE t.game = :game")
    Page<PlayerEntity> findByGame(@Param("game") Game game, PageRequest pageRequest);

    /**
     * Persist a new target.
     *
     * @param targetEntity target entity
     */
    void createTarget(TargetEntity targetEntity);

    /**
     * Get the player for the given id.
     *
     * @param id player id
     * @return player
     */
    PlayerEntity getPlayer(Integer id);

    /**
     * Get the team for the given id.
     *
     * @param id team id
     * @return team
     */
    TeamEntity getTeam(Integer id);

    /**
     * Persist a new team.
     *
     * @param teamEntity team entity
     */
    void createTeam(TeamEntity teamEntity);

    /**
     * Persist a new player.
     *
     * @param playerEntity player entity
     * @throws PlayerValidationException if player is not valid for creation
     */
    void createPlayer(PlayerEntity playerEntity) throws PlayerValidationException;

    /**
     * Retrieve an existing player attribute.
     *
     * @param playerEntity player entity
     * @param key          attribute name
     * @return player attribute
     */
    PlayerAttribute getPlayerAttribute(PlayerEntity playerEntity, String key);

}
