package com.subrosagames.subrosa.domain.player;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

        Player player = new Player();
        player.setAccount(account);
        player.setGame(game);
        player.setPlayerProfile(playerDescriptor.getPlayer());
        player.setTeam(teamEntity);
        player.setGameRole(GameRole.PLAYER);
        player.setKillCode(PlayerCodeGenerator.generate());
        processPlayerAttributes(player, playerDescriptor);
        player.assertValid();

        teamRepository.save(teamEntity);
        playerRepository.save(player);

        injectDependencies(player);
        return player;
    }

    @Override
    public void processPlayerAttributes(Player player, PlayerDescriptor playerDescriptor) throws ImageNotFoundException, AddressNotFoundException {
        LOG.debug("Ingesting player attributes: {}", playerDescriptor.getAttributes());
        for (EnrollmentField field : playerDescriptor.getEnrollmentFields()) {
            if (!playerDescriptor.getAttributes().containsKey(field.getFieldId())) {
                LOG.debug("Could not find enrollment field {} in supplied attributes: {}", field.getFieldId());
                continue;
            }
            PlayerAttribute playerAttribute = player.getAttributes().get(field.getFieldId());
            if (playerAttribute == null) {
                playerAttribute = field.getType().newForAccount(player.getAccount(), playerDescriptor.getAttribute(field.getFieldId()));
                LOG.debug("Creating new player attribute ({}) {} => {} for player {}",
                        field.getType().name(), field.getFieldId(), playerAttribute.getValueRef(), player.getId());
                playerAttribute.setPrimaryKey(new PlayerAttributePk(player.getId(), field.getFieldId()));
                playerAttribute.setPlayer(player);
                playerAttribute.setType(field.getType());
                player.setAttribute(field.getFieldId(), playerAttribute);
            } else {
                LOG.debug("Updating player attribute ({}) {} => {} for player {}",
                        field.getType().name(), field.getFieldId(), playerAttribute.getValueRef(), player.getId());
                field.getType().updateForAccount(player.getAccount(), playerDescriptor.getAttribute(field.getFieldId()), playerAttribute);
            }
        }
    }

    private Player injectDependencies(Player player) {
        player.setPlayerFactory(this);
        // TODO got to be a better way of doing this...
        accountFactory.injectDependencies(player.getAccount());
        return player;
    }

    @Override
    public Player getPlayer(Game game, Integer playerId) throws PlayerNotFoundException {
        return playerRepository.findByGameAndId(game, playerId)
                .map(this::injectDependencies)
                .orElseThrow(() -> new PlayerNotFoundException("No player " + playerId + " in game " + game.getId()));
    }

    @Override
    public List<? extends Player> getPlayers(Game game) {
        return playerRepository.findByGame(game);
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
        teamEntity = teamRepository.save(teamEntity);
        return teamEntity;
    }

    @Override
    public Team getTeam(Game game, Integer teamId) throws TeamNotFoundException {
        return teamRepository.findByGameAndId(game, teamId)
//                .map(this::injectDependencies)
                .orElseThrow(() -> new TeamNotFoundException("No team " + teamId + " in game " + game.getId()));
    }

    @Override
    public List<? extends Team> getTeams(Game game) {
        return teamRepository.findByGame(game);
    }
}
