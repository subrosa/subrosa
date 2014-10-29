package com.subrosagames.subrosa.api.web;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.subrosagames.subrosa.api.NotAuthenticatedException;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.game.Game;
import com.subrosagames.subrosa.domain.game.GameFactory;
import com.subrosagames.subrosa.security.SecurityHelper;

/**
 * Controller for calls on authenticated user.
 */
@Controller
@RequestMapping("/user")
public class ApiUserController {

    private static final Logger LOG = LoggerFactory.getLogger(ApiUserController.class);

    @Autowired
    private GameFactory gameFactory;

    /**
     * Get list of user's games.
     *
     * @return list of games
     * @throws NotAuthenticatedException if user is not authenticated
     */
    @RequestMapping(value = { "/game", "/game/" }, method = RequestMethod.GET)
    @ResponseBody
    public List<? extends Game> getUsersGames() throws NotAuthenticatedException {
        LOG.debug("Getting games for the logged in user");
        if (!SecurityHelper.isAuthenticated()) {
            throw new NotAuthenticatedException("No logged in user found");
        }
        Account user = SecurityHelper.getAuthenticatedUser();
        return gameFactory.ownedBy(user);
    }
}
