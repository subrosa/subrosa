package com.subrosagames.subrosa.domain.player;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import com.subrosagames.subrosa.api.dto.PlayerDescriptor;
import com.subrosagames.subrosa.api.dto.TeamDescriptor;
import com.subrosagames.subrosa.domain.BaseDomainObjectFactory;
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
public class PlayerFactoryImpl extends BaseDomainObjectFactory implements PlayerFactory {

    private static final Logger LOG = LoggerFactory.getLogger(PlayerFactoryImpl.class);

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private AccountFactory accountFactory;

    @Override
    public Player createPlayerForGame(Game game, Account account, PlayerDescriptor playerDescriptor)
            throws PlayerValidationException, AddressNotFoundException, ImageNotFoundException
    {
        TeamEntity teamEntity = new TeamEntity();
        teamEntity.setGameId(game.getId());
        teamEntity.setName(playerDescriptor.getPlayer().getName());
        playerRepository.createTeam(teamEntity);

        PlayerEntity playerEntity = new PlayerEntity();
        playerEntity.setAccount(account);
        playerEntity.setPlayerProfile(playerDescriptor.getPlayer());
        playerEntity.setTeam(teamEntity);
        playerEntity.setGameRole(GameRole.PLAYER);
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

    @Override
    public List<? extends Player> getPlayers(Game game, Integer limit, Integer offset) {
        int pageNum = offset > 0 && limit > 0 ? offset / limit : 0;
        return playerRepository.findByGame(game, new PageRequest(pageNum, limit)).getContent();
    }

    @Override
    public Team createTeamForGame(Game game, TeamDescriptor teamDescriptor) {
        TeamEntity teamEntity = new TeamEntity();
        teamEntity.setGameId(game.getId());
        copyProperties(teamDescriptor, teamEntity);
        playerRepository.createTeam(teamEntity);
        return teamEntity;
    }

    @Override
    public Team getTeam(Game game, Integer teamId) throws TeamNotFoundException {
        TeamEntity team = playerRepository.getTeam(teamId);
        if (team == null || !game.getId().equals(team.getGameId())) {
            throw new TeamNotFoundException("No team " + teamId + " in game.");
        }
        return team;
    }

    @Override
    public List<? extends Team> getTeams(Game game) {
        return teamRepository.findByGame(game);
    }
}
