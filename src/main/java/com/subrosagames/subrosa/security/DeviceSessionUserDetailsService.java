package com.subrosagames.subrosa.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import com.subrosagames.subrosa.domain.account.AccountRepository;
import com.subrosagames.subrosa.domain.token.Token;
import com.subrosagames.subrosa.domain.token.TokenFactory;
import com.subrosagames.subrosa.domain.token.TokenType;

/**
 *
 */
public class DeviceSessionUserDetailsService implements AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {

    @Autowired
    private TokenFactory tokenFactory;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken token) throws UsernameNotFoundException {
        Token sessionToken = tokenFactory.getToken((String) token.getPrincipal(), TokenType.DEVICE_AUTH);
        if (sessionToken != null) {
            return new SubrosaUser(accountRepository.getAccount(sessionToken.getOwner()));
        }
        throw new UsernameNotFoundException("No user found for auth token " + token.getPrincipal());
    }
}
