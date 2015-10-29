package com.subrosagames.subrosa.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.account.AccountRepository;
import com.subrosagames.subrosa.domain.token.Token;
import com.subrosagames.subrosa.domain.token.TokenFactory;
import com.subrosagames.subrosa.domain.token.TokenType;

/**
 * User details service that retrieves users via a previously generated token rather than email.
 */
public class DeviceSessionUserDetailsService implements AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {

    /**
     * Header used for passing the authentication token.
     */
    public static final String SR_AUTH_HEADER = "X-SUBROSA-AUTH";

    private static final Logger LOG = LoggerFactory.getLogger(DeviceSessionUserDetailsService.class);

    @Autowired
    private TokenFactory tokenFactory;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken token) {
        Token sessionToken = tokenFactory.findToken((String) token.getPrincipal(), TokenType.DEVICE_AUTH);
        if (sessionToken != null) {
            LOG.debug("Device session token resolved to user " + sessionToken.getOwner());
            Account account = accountRepository.findOne(sessionToken.getOwner());
            if (account != null) {
                return new SubrosaUser(account);
            }
            LOG.debug("No account found for user {}", sessionToken.getOwner());
        }
        throw new UsernameNotFoundException("No user found for auth token " + token.getPrincipal());
    }

}
