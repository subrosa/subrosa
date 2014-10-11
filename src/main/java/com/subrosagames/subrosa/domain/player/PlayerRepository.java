package com.subrosagames.subrosa.domain.player;

import com.subrosagames.subrosa.domain.player.persistence.PlayerAttribute;
import com.subrosagames.subrosa.domain.player.persistence.PlayerEntity;
import com.subrosagames.subrosa.domain.player.persistence.TargetEntity;
import com.subrosagames.subrosa.domain.player.persistence.TeamEntity;

/**
 * Persistence repository for game players.
 */
public interface PlayerRepository {

    void createTarget(TargetEntity targetEntity);

    PlayerEntity getPlayer(Integer id);

    TeamEntity getTeam(Integer id);

    void createTeam(TeamEntity teamEntity);

    void createPlayer(PlayerEntity playerEntity) throws PlayerValidationException;

    /**
     * Creates or updates, as appropriate, the given player attribute.
     *
     * @param playerEntity player entity
     * @param key          attribute name
     * @param value        attribute value
     * @return player attribute
     */
    PlayerAttribute setPlayerAttribute(PlayerEntity playerEntity, String key, String value);

    /**
     * Retrieve an existing player attribute.
     *
     * @param playerEntity player entity
     * @param key          attribute name
     * @return player attribute
     */
    PlayerAttribute getPlayerAttribute(PlayerEntity playerEntity, String key);
}
