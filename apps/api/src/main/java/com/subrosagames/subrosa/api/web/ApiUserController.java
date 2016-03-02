package com.subrosagames.subrosa.api.web;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.subrosagames.subrosa.api.NotAuthenticatedException;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.account.AccountFactory;
import com.subrosagames.subrosa.domain.account.AccountNotFoundException;
import com.subrosagames.subrosa.domain.game.Game;
import com.subrosagames.subrosa.domain.game.GameFactory;
import com.subrosagames.subrosa.security.SubrosaUser;

/**
 * Controller for calls on authenticated user.
 */
@RestController
public class ApiUserController {

    private static final Logger LOG = LoggerFactory.getLogger(ApiUserController.class);

    @Autowired
    private GameFactory gameFactory;

    @Autowired
    private AccountFactory accountFactory;

    /**
     * Get list of user's games.
     *
     * @return list of games
     * @throws NotAuthenticatedException if user is not authenticated
     */
    @RequestMapping(value = { "/user/game", "/user/game/" }, method = RequestMethod.GET)
    public List<? extends Game> getUsersGames(@AuthenticationPrincipal SubrosaUser user) throws NotAuthenticatedException, AccountNotFoundException {
        LOG.debug("Getting games for the logged in user");
        if (user == null) {
            throw new NotAuthenticatedException("No logged in user found");
        }
        Account account = accountFactory.getAccount(user.getId());
        return gameFactory.ownedBy(account);
    }

}
