package com.subrosagames.subrosa.domain.player;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.game.Game;
import com.subrosagames.subrosa.domain.player.persistence.PlayerEntity;
import com.subrosagames.subrosa.domain.player.persistence.TeamEntity;

/**
 *
 */
@Component
public class PlayerFactoryImpl implements PlayerFactory {

    @Autowired
    private PlayerRepository playerRepository;

    @Override
    public Player createPlayerForGame(Game game, Account account) {
        TeamEntity teamEntity = new TeamEntity();
        teamEntity.setGameId(game.getId());
        playerRepository.createTeam(teamEntity);

        PlayerEntity playerEntity = new PlayerEntity();
        playerEntity.setAccount(account);
        playerEntity.setTeam(teamEntity);
        playerEntity.setGameRole(GameRole.PLAYER);
        playerEntity.setKillCode(PlayerCodeGenerator.generate());
        playerRepository.createPlayer(playerEntity);

        return new Player(playerEntity);
    }

    @Override
    public Player getPlayerForEntity(PlayerEntity entity) {
        Player player = new Player(entity);
        player.setPlayerRepository(playerRepository);
        return player;
    }
}
