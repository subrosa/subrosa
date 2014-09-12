package com.subrosagames.subrosa.api.web;

import java.math.BigDecimal;
import java.util.Random;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.subrosagames.subrosa.api.dto.PlayerDescriptor;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.game.Game;
import com.subrosagames.subrosa.domain.game.GameFactory;
import com.subrosagames.subrosa.domain.game.GameNotFoundException;
import com.subrosagames.subrosa.domain.game.GameType;
import com.subrosagames.subrosa.domain.game.persistence.GameEntity;
import com.subrosagames.subrosa.domain.game.validation.GameValidationException;
import com.subrosagames.subrosa.domain.gamesupport.assassin.AssassinGame;
import com.subrosagames.subrosa.domain.gamesupport.assassin.AssassinGameAttributeType;
import com.subrosagames.subrosa.domain.gamesupport.assassin.OrdnanceType;
import com.subrosagames.subrosa.domain.player.PlayerValidationException;

/**
 * Controller for prototyping. Not for production.
 */
@Controller
public class ApiSandboxController {

    @Autowired
    private GameFactory gameFactory;

    private Random random = new Random();

    /**
     * Test endpoint, creates a random game.
     *
     * @return created game
     * @throws GameValidationException if something goes wrong
     * @throws GameNotFoundException   if game is not found
     */
    @RequestMapping(value = "/android")
    @ResponseBody
    public Game doIt() throws GameValidationException, GameNotFoundException, PlayerValidationException {
        //@RequestBody GameDescriptor gameDescriptor) {
//        GameEntity entity = gameDescriptor.getInfo();
//        List<GameHistory> events = gameDescriptor.getEvents();

//        LifecycleEntity lifecycleEntity = new LifecycleEntity();
//        lifecycleEntity.addScheduledEvent(EventMessage.MUTUAL_INTEREST_ASSIGNMENT, lifecycleEntity.getGameStart());

        GameEntity gameEntity = new AssassinGame();
        gameEntity.setName("game name" + random.nextLong());
        gameEntity.setUrl("game-url-" + random.nextLong());
        gameEntity.setDescription("This is a test.");
        gameEntity.setPrice(new BigDecimal("10.00"));
        gameEntity.setMaximumTeamSize(5);
        gameEntity.setGameType(GameType.ASSASSIN);
        gameEntity.setTimezone(TimeZone.getDefault().getDisplayName());
//        gameEntity.setRegistrationStart(new Timestamp(new Date().getTime() + 2500)); // SUPPRESS CHECKSTYLE MagicNumber
//        gameEntity.setRegistrationEnd(new Timestamp(new Date().getTime() + 5000)); // SUPPRESS CHECKSTYLE MagicNumber
//        gameEntity.setGameStart(new Timestamp(new Date().getTime() + 10000)); // SUPPRESS CHECKSTYLE MagicNumber
//        gameEntity.setGameEnd(new Timestamp(new Date().getTime() + 40000)); // SUPPRESS CHECKSTYLE MagicNumber

        Game game = gameEntity.create();

        game.setAttribute(AssassinGameAttributeType.ORDNANCE_TYPE, OrdnanceType.WATER_WEAPONS);

        Account account1 = new Account();
        account1.setId(3);
        PlayerDescriptor playerDescriptor1 = new PlayerDescriptor();
        playerDescriptor1.setName("player1");
        Account account2 = new Account();
        account2.setId(4);
        PlayerDescriptor playerDescriptor2 = new PlayerDescriptor();
        playerDescriptor2.setName("player2");
        Account account3 = new Account();
        account3.setId(5);
        PlayerDescriptor playerDescriptor3 = new PlayerDescriptor();
        playerDescriptor1.setName("player1");
        Account account4 = new Account();
        account4.setId(6);
        PlayerDescriptor playerDescriptor4 = new PlayerDescriptor();
        playerDescriptor1.setName("player1");


        game.addUserAsPlayer(account1, playerDescriptor1);
        game.addUserAsPlayer(account2, playerDescriptor2);
        game.addUserAsPlayer(account3, playerDescriptor3);
        game.addUserAsPlayer(account4, playerDescriptor4);

        return gameFactory.getGame(game.getId());
    }

}
