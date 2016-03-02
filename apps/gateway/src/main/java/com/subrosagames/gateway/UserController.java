package com.subrosagames.gateway;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.subrosagames.gateway.auth.Account;
import com.subrosagames.gateway.auth.AccountRepository;
import com.subrosagames.subrosa.security.SubrosaUser;

@RestController
@EnableResourceServer
public class UserController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TokenStore tokenStore;

    @RequestMapping(value = { "/user", "/user/" }, method = RequestMethod.GET)
    public Account user(Principal user) {
        Authentication authentication = ((OAuth2Authentication) user).getUserAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof SubrosaUser) {
                return accountRepository.findOne(((SubrosaUser) principal).getId());
            }
        }
        return null;
    }

    @RequestMapping(value = "/oauth/revoke", method = RequestMethod.POST)
    public void revokeAccessToken(Principal user) {
        OAuth2Authentication oauth2 = (OAuth2Authentication) user;
        OAuth2AccessToken token = tokenStore.getAccessToken(oauth2);
        if (token != null) {
            tokenStore.removeAccessToken(token);
        }
    }

}
