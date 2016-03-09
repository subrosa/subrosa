package com.subrosagames.gateway;

import java.security.Principal;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.security.auth.login.AccountNotFoundException;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
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

import com.subrosagames.gateway.auth.Account;
import com.subrosagames.gateway.auth.AccountRepository;
import com.subrosagames.gateway.auth.SessionRequest;
import com.subrosagames.gateway.token.TokenFactory;
import com.subrosagames.subrosa.security.SubrosaUser;

@RestController
public class SocialController {

    private static final Logger LOG = LoggerFactory.getLogger(SocialController.class);

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TokenFactory tokenFactory;

    @Autowired
    private ConnectionFactoryLocator connectionFactoryLocator;

    @Autowired
    private UsersConnectionRepository usersConnectionRepository;

    @Autowired
    private AuthorizationServerTokenServices tokenServices;

    /**
     * Create a session for the user associated with the given OAuth token.
     *
     * @param provider       OAuth provider
     * @param sessionRequest session request
     * @return authentication response
     */
    @RequestMapping(value = { "/session/{provider}", "/session/{provider}/" }, method = RequestMethod.POST)
    @Transactional
    public OAuth2AccessToken createSession(@PathVariable("provider") String provider,
                                           @RequestBody SessionRequest sessionRequest,
                                           Principal principal) throws AccountNotFoundException
    {
        LOG.debug("Creating a new {} session for user", provider);
        OAuth2ConnectionFactory<?> connectionFactory = (OAuth2ConnectionFactory<?>) connectionFactoryLocator.getConnectionFactory(provider);
        Connection<?> connection = connectionFactory.createConnection(new AccessGrant(sessionRequest.getAccessToken()));

        List<String> userEmails = usersConnectionRepository.findUserIdsWithConnection(connection);
        LOG.debug("Found {} emails for oauth connection: {}", userEmails.size(), String.join(",", userEmails));
        if (userEmails.size() == 0) {
            throw new AccountNotFoundException("Provided access token does not correspond to a user");
        }
        Account account = accountRepository.findOneByEmail(userEmails.get(0)) //take the first one if there are multiple userIds for this provider Connection
                .orElseThrow(() -> new AccountNotFoundException("no user for the given social auth"));
        LOG.debug("Resolved email {} to account {}", userEmails.get(0), account);
        updateUserFromProfile(connection, account);

        OAuth2Request req = ((OAuth2Authentication) principal).getOAuth2Request();
        Map<String, String> requestParameters = new HashMap<>();
        Collection<? extends GrantedAuthority> authorities = account.grantedAuthorities();
        OAuth2Request oAuth2Request = new OAuth2Request(
                requestParameters, req.getClientId(), authorities, true,
                req.getScope(), req.getResourceIds(), null, req.getResponseTypes(),
                req.getExtensions());
        User user = new SubrosaUser(account);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user, null, authorities);

        OAuth2Authentication authentication = new OAuth2Authentication(oAuth2Request, authenticationToken);
        return tokenServices.createAccessToken(authentication);
    }

    private void updateUserFromProfile(Connection<?> connection, Account account) {

    }

}
