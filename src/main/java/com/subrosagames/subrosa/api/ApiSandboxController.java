package com.subrosagames.subrosa.api;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.game.Game;
import com.subrosagames.subrosa.domain.game.GameFactory;
import com.subrosagames.subrosa.domain.game.GameNotFoundException;
import com.subrosagames.subrosa.domain.game.GameType;
import com.subrosagames.subrosa.domain.game.GameValidationException;
import com.subrosagames.subrosa.domain.gamesupport.assassin.AssassinGame;
import com.subrosagames.subrosa.domain.gamesupport.assassin.AssassinGameAttributeType;
import com.subrosagames.subrosa.domain.gamesupport.assassin.OrdnanceType;
import com.subrosagames.subrosa.domain.game.persistence.GameEntity;
import com.subrosagames.subrosa.domain.game.persistence.LifecycleEntity;
import com.subrosagames.subrosa.event.message.EventMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 */
@Controller
public class ApiSandboxController {

    @Autowired
    private GameFactory gameFactory;

    private Random random = new Random();


    /**
     * Test endpoint, creates a random game.
     * @return created game
     * @throws GameValidationException if something goes wrong
     */
    @RequestMapping(value = "/android")
    @ResponseBody
    public Game doIt() throws GameValidationException, GameNotFoundException {
        //@RequestBody GameDescriptor gameDescriptor) {
//        GameEntity entity = gameDescriptor.getInfo();
//        List<GameEvent> events = gameDescriptor.getEvents();

        LifecycleEntity lifecycleEntity = new LifecycleEntity();
        lifecycleEntity.setRegistrationStart(new Timestamp(new Date().getTime() + 2500)); // SUPPRESS CHECKSTYLE MagicNumber
        lifecycleEntity.setRegistrationEnd(new Timestamp(new Date().getTime() + 5000)); // SUPPRESS CHECKSTYLE MagicNumber
        lifecycleEntity.setGameStart(new Timestamp(new Date().getTime() + 10000)); // SUPPRESS CHECKSTYLE MagicNumber
        lifecycleEntity.setGameEnd(new Timestamp(new Date().getTime() + 40000)); // SUPPRESS CHECKSTYLE MagicNumber
        lifecycleEntity.addScheduledEvent(EventMessage.MUTUAL_INTEREST_ASSIGNMENT, lifecycleEntity.getGameStart());

        GameEntity gameEntity = new AssassinGame();
        gameEntity.setName("game name" + random.nextLong());
        gameEntity.setUrl("game-url-" + random.nextLong());
        gameEntity.setDescription("This is a test.");
        gameEntity.setPrice(new BigDecimal("10.00"));
        gameEntity.setMaximumTeamSize(5);
        gameEntity.setGameType(GameType.ASSASSIN);
        gameEntity.setTimezone(TimeZone.getDefault().getDisplayName());

        Game game = gameEntity.create();

        game.setAttribute(AssassinGameAttributeType.ORDNANCE_TYPE, OrdnanceType.WATER_WEAPONS);

        Account account1 = new Account();
        account1.setId(3);
        Account account2 = new Account();
        account2.setId(4);
        Account account3 = new Account();
        account3.setId(5);
        Account account4 = new Account();
        account4.setId(6);

        game.addUserAsPlayer(account1);
        game.addUserAsPlayer(account2);
        game.addUserAsPlayer(account3);
        game.addUserAsPlayer(account4);

        return gameFactory.getGame(game.getId());
    }

}
