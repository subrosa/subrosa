package com.subrosagames.subrosa.api.web;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.subrosagames.subrosa.api.NotAuthenticatedException;
import com.subrosagames.subrosa.api.dto.SessionRequest;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.account.AccountFactory;
import com.subrosagames.subrosa.domain.account.AccountNotFoundException;
import com.subrosagames.subrosa.domain.game.Game;
import com.subrosagames.subrosa.domain.game.GameFactory;
import com.subrosagames.subrosa.domain.token.TokenFactory;
import com.subrosagames.subrosa.domain.token.TokenType;
import com.subrosagames.subrosa.security.AuthenticationResponse;
import com.subrosagames.subrosa.security.SecurityHelper;

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

    @Autowired
    private TokenFactory tokenFactory;

    @Autowired
    private ConnectionFactoryLocator connectionFactoryLocator;

    @Autowired
    private UsersConnectionRepository usersConnectionRepository;

    /**
     * Get list of user's games.
     *
     * @return list of games
     * @throws NotAuthenticatedException if user is not authenticated
     */
    @RequestMapping(value = { "/user/game", "/user/game/" }, method = RequestMethod.GET)
    public List<? extends Game> getUsersGames() throws NotAuthenticatedException {
        LOG.debug("Getting games for the logged in user");
        if (!SecurityHelper.isAuthenticated()) {
            throw new NotAuthenticatedException("No logged in user found");
        }
        Account user = SecurityHelper.getAuthenticatedUser();
        return gameFactory.ownedBy(user);
    }


    /**
     * Create a session for the user associated with the given OAuth token.
     *
     * @param provider       OAuth provider
     * @param sessionRequest session request
     * @return authentication response
     * @throws NotAuthenticatedException if the access token could not be authenticated
     */
    @RequestMapping(value = { "/session/{provider}", "/session/{provider}/" }, method = RequestMethod.POST)
    public AuthenticationResponse createSession(@PathVariable("provider") String provider,
                                                @RequestBody SessionRequest sessionRequest) throws NotAuthenticatedException
    {
        LOG.debug("Creating a new {} session for user", provider);
        OAuth2ConnectionFactory<?> connectionFactory = (OAuth2ConnectionFactory<?>) connectionFactoryLocator.getConnectionFactory(provider);
        Connection<?> connection = connectionFactory.createConnection(new AccessGrant(sessionRequest.getAccessToken()));

        List<String> userEmails = usersConnectionRepository.findUserIdsWithConnection(connection);
        if (userEmails.size() == 0) {
            throw new NotAuthenticatedException("Provided access token does not correspond to a user");
        }
        Account account;
        try {
            account = accountFactory.getAccount(userEmails.get(0)); //take the first one if there are multiple userIds for this provider Connection
        } catch (AccountNotFoundException e) {
            throw new NotAuthenticatedException("No user exists for the given social auth");
        }
        updateUserFromProfile(connection, account);
        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setToken(tokenFactory.generateNewToken(account.getId(), TokenType.DEVICE_AUTH));
        return authenticationResponse;
    }

    private void updateUserFromProfile(Connection<?> connection, Account account) {

    }

}
