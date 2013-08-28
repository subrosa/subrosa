package com.subrosagames.subrosa.service;

import com.subrosagames.subrosa.domain.game.Lifecycle;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.account.AccountRepository;
import com.subrosagames.subrosa.domain.game.GameFactory;
import com.subrosagames.subrosa.domain.game.GameRepository;
import com.subrosagames.subrosa.domain.game.GameValidationException;
import com.subrosagames.subrosa.domain.game.persistence.GameEntity;
import com.subrosagames.subrosa.domain.game.persistence.LifecycleEntity;

/**
 * Test {@link GameService}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/test-context.xml")
public class GameServiceTest {

    // CHECKSTYLE-OFF: JavadocMethod

    @Autowired
    private GameService gameService;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameFactory gameFactory;

    @Autowired
    private AccountRepository accountRepository;

//    @Autowired
//    JdbcTemplate jdbcTemplate;

    @Before
    public void before() {
    }

    @Test
    public void createNewGameAndTestWorkflow() throws GameValidationException {

        // game master registers an account
        Account gameMaster = new Account();
        gameMaster.setEmail("gamemaster@test.com");
        gameMaster = accountRepository.create(gameMaster, "gamemaster");

        // game master creates an assassins game
        GameEntity gameEntity = new GameEntity();
        Lifecycle lifecycle = new LifecycleEntity();
//        Game game = gameFactory.createGame(gameEntity, gameLifecycle);

        // 1st game player registers
        // and attempts to join - failure

        // registration time begins
        // attempts to join - success

        // 2nd game player registers
        // 3rd game player registers
        // and joins
        // and joins

        // registration time ends
        // 4th player registers and fails to join

        // game begins with round robin assignment ( 1->2->3 )

        // player 1 attempts to kill player 3

        // player 1 kills player 2

        // player 3 kills player 1

        // game ends



//        Player player = gameService.enrollInGame(accountRepository.getAccount(100), game);
//        String code = player.getSecretCode();
//        Player player2 = gameService.enrollInGame(accountRepository.getAccount(101), game);
//        gameService.enrollInGame(accountRepository.getAccount(102), game);
//
//        gameService.handleTargetContact(player2, code);
//
//        List<? extends Participant> players = game.getPlayers();



//        Account account100 = accountRepository.getAccount(101);
//        Account account101 = accountRepository.getAccount(101);
//        Account account102 = accountRepository.getAccount(102);
//        Participant player1 = game.enrollPlayer(account100);
//        Participant player2 = game.enrollPlayer(account101);
//        Participant player3 = game.enrollPlayer(account102);
    }

    // CHECKSTYLE-ON: JavadocMethod
}
