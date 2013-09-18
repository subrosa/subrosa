package com.subrosagames.subrosa.api;

import java.util.List;

import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.account.AccountNotFoundException;
import com.subrosagames.subrosa.domain.game.Game;
import com.subrosagames.subrosa.domain.game.GameFactory;
import com.subrosagames.subrosa.domain.game.GameFactoryImpl;
import com.subrosagames.subrosa.security.SecurityHelper;
import com.subrosagames.subrosa.service.PaginatedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 */
@Controller
@RequestMapping("/user")
public class ApiUserController {

    private static final Logger LOG = LoggerFactory.getLogger(ApiUserController.class);

    @Autowired
    private GameFactory gameFactory;


    /**
     * Get the currently logged in user's account info.
     */
    @RequestMapping(value = { "", "/" }, method = RequestMethod.GET)
    @ResponseBody
    public Account getLoggedInUser() throws NotAuthenticatedException {
        LOG.debug("Getting account info for the currently logged in user");
        if (!SecurityHelper.isAuthenticated()) {
            throw new NotAuthenticatedException("No logged in user found");
        }
        return SecurityHelper.getAuthenticatedUser();
    }

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
