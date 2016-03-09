package com.subrosagames.gateway;

import java.security.Principal;

import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableResourceServer
public class ClientController {

    @RequestMapping(value = { "/client", "/client/" }, method = RequestMethod.GET)
    public OAuth2Request client(Principal user) {
        return ((OAuth2Authentication) user).getOAuth2Request();
    }

}
