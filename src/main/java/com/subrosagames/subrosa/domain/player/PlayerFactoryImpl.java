package com.subrosagames.subrosa.domain.player;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.subrosagames.subrosa.api.dto.PlayerDescriptor;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.game.Game;
import com.subrosagames.subrosa.domain.player.persistence.PlayerAttribute;
import com.subrosagames.subrosa.domain.player.persistence.PlayerAttributePk;
import com.subrosagames.subrosa.domain.player.persistence.PlayerEntity;
import com.subrosagames.subrosa.domain.player.persistence.TeamEntity;

/**
 * Factory for game player objects.
 */
@Component
public class PlayerFactoryImpl implements PlayerFactory {

    private static final Logger LOG = LoggerFactory.getLogger(PlayerFactoryImpl.class);

    @Autowired
    private PlayerRepository playerRepository;

    @Override
    public Player createPlayerForGame(Game game, Account account, PlayerDescriptor playerDescriptor)
            throws PlayerValidationException
    {
        TeamEntity teamEntity = new TeamEntity();
        teamEntity.setGameId(game.getId());
        playerRepository.createTeam(teamEntity);

        PlayerEntity playerEntity = new PlayerEntity();
        playerEntity.setAccount(account);
        playerEntity.setTeam(teamEntity);
        playerEntity.setGameRole(GameRole.PLAYER);
        playerEntity.setName(playerDescriptor.getName());
        playerEntity.setKillCode(PlayerCodeGenerator.generate());
        playerRepository.createPlayer(playerEntity);

        Map<String, PlayerAttribute> playerAttributes = Maps.newHashMap();
        for (Map.Entry<String, String> entry : playerDescriptor.getAttributes().entrySet()) {
            PlayerAttribute playerAttribute = playerRepository.getPlayerAttribute(playerEntity, entry.getKey());
            if (playerAttribute == null) {
                LOG.debug("Creating new player attribute {} => {} for player {}", entry.getKey(), entry.getValue(), playerEntity.getId());
                playerAttribute = new PlayerAttribute();
                playerAttribute.setPrimaryKey(new PlayerAttributePk(playerEntity.getId(), entry.getKey()));
                playerAttribute.setPlayer(playerEntity);
                playerAttribute.setValue(entry.getValue());
            } else {
                LOG.debug("Updating player attribute {} => {} for player {}", playerAttribute.getPrimaryKey().getName(), playerAttribute.getValue(), playerEntity.getId());
                playerAttribute.setValue(entry.getValue());
            }
            playerAttributes.put(entry.getKey(), playerAttribute);
        }
        playerEntity.setAttributes(playerAttributes);

        return playerEntity;
    }

    @Override
    public Player getPlayer(Game game, Integer playerId) throws PlayerNotFoundException {
        PlayerEntity player = playerRepository.getPlayer(playerId);
        if (!player.getTeam().getGameId().equals(game.getId())) {
            throw new PlayerNotFoundException("No player " + playerId + " in game.");
        }
        return player;
    }

}
