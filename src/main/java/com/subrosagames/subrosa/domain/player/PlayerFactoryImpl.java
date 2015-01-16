package com.subrosagames.subrosa.domain.player;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.subrosagames.subrosa.api.dto.PlayerDescriptor;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.account.AccountFactory;
import com.subrosagames.subrosa.domain.account.AddressNotFoundException;
import com.subrosagames.subrosa.domain.game.EnrollmentField;
import com.subrosagames.subrosa.domain.game.Game;
import com.subrosagames.subrosa.domain.image.ImageNotFoundException;
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
    @Autowired
    private AccountFactory accountFactory;

    @Override
    public Player createPlayerForGame(Game game, Account account, PlayerDescriptor playerDescriptor)
            throws PlayerValidationException, AddressNotFoundException, ImageNotFoundException
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

        processPlayerAttributes(playerEntity, playerDescriptor);
        playerRepository.createPlayer(playerEntity);

        injectDependencies(playerEntity);
        return playerEntity;
    }

    @Override
    public void processPlayerAttributes(PlayerEntity playerEntity, PlayerDescriptor playerDescriptor) throws ImageNotFoundException, AddressNotFoundException {
        for (EnrollmentField field : playerDescriptor.getEnrollmentFields()) {
            if (!playerDescriptor.getAttributes().containsKey(field.getFieldId())) {
                continue;
            }
            PlayerAttribute playerAttribute = playerRepository.getPlayerAttribute(playerEntity, field.getFieldId());
            if (playerAttribute == null) {
                playerAttribute = field.getType().newForAccount(playerEntity.getAccount(), playerDescriptor.getAttribute(field.getFieldId()));
                LOG.debug("Creating new player attribute ({}) {} => {} for player {}",
                        field.getType().name(), field.getFieldId(), playerAttribute.getValueRef(), playerEntity.getId());
                playerAttribute.setPrimaryKey(new PlayerAttributePk(playerEntity.getId(), field.getFieldId()));
                playerAttribute.setPlayer(playerEntity);
                playerEntity.setAttribute(field.getFieldId(), playerAttribute);
            } else {
                LOG.debug("Updating player attribute ({}) {} => {} for player {}",
                        field.getType().name(), field.getFieldId(), playerAttribute.getValueRef(), playerEntity.getId());
                field.getType().updateForAccount(playerEntity.getAccount(), playerDescriptor.getAttribute(field.getFieldId()), playerAttribute);
            }
        }
    }

    private void injectDependencies(PlayerEntity playerEntity) {
        playerEntity.setPlayerFactory(this);
        playerEntity.setPlayerRepository(playerRepository);
        // TODO got to be a better way of doing this...
        accountFactory.injectDependencies(playerEntity.getAccount());
    }

    @Override
    public Player getPlayer(Game game, Integer playerId) throws PlayerNotFoundException {
        PlayerEntity player = playerRepository.getPlayer(playerId);
        if (!player.getTeam().getGameId().equals(game.getId())) {
            throw new PlayerNotFoundException("No player " + playerId + " in game.");
        }
        injectDependencies(player);
        return player;
    }

}
