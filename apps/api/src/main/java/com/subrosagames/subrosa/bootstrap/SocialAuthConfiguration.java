package com.subrosagames.subrosa.bootstrap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;

import com.subrosagames.subrosa.domain.account.AccountRepository;
import com.subrosagames.subrosa.security.JpaUsersConnectionRepository;
import com.subrosagames.subrosa.security.SocialUserRepository;
import com.subrosagames.subrosa.security.SubrosaUser;

/**
 * Configures social integrations.
 */
@Configuration
@EnableSocial
public class SocialAuthConfiguration extends SocialConfigurerAdapter {

    @Autowired
    private ConnectionFactory facebookConnectionFactory;

    @Autowired
    private SocialUserRepository socialUserRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Bean
    public TextEncryptor getTextEncryptor() {
        return Encryptors.noOpText();
    }

    @Override
    public void addConnectionFactories(ConnectionFactoryConfigurer connectionFactoryConfigurer, Environment environment) {
        connectionFactoryConfigurer.addConnectionFactory(
                facebookConnectionFactory
        );
    }

    @Override
    public UserIdSource getUserIdSource() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null) {
                throw new IllegalStateException("Unable to get a ConnectionRepository: no user signed in");
            }
            return ((SubrosaUser) authentication.getPrincipal()).getUserId();
        };
    }

    @Override
    public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
        return new JpaUsersConnectionRepository(socialUserRepository, accountRepository);
    }

}
