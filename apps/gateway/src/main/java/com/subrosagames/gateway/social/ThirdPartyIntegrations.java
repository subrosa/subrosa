package com.subrosagames.gateway.social;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;

/**
 * Base configuration class for application.
 */
@Configuration
@Profile("!unit-test")
public class ThirdPartyIntegrations {

    @Autowired
    private FacebookIntegration facebookIntegration;

    /**
     * Facebook connection factory.
     *
     * @return facebook connection factory
     */
    @Bean
    public ConnectionFactory facebookConnectionFactory() {
        return new FacebookConnectionFactory(facebookIntegration.getAppId(), facebookIntegration.getAppSecret());
    }

}
