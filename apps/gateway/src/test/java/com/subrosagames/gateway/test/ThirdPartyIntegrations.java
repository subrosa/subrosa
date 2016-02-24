package com.subrosagames.gateway.test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionValues;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.UserProfileBuilder;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2ServiceProvider;

/**
 * Base configuration class for application.
 */
@Configuration
@Profile("unit-test")
public class ThirdPartyIntegrations {

    /**
     * Mock facebook connection factory.
     *
     * @return mock facebook connection factory
     */
    @Bean
    public ConnectionFactory facebookConnectionFactory() {
        return new MockFacebookConnectionFactory();
    }

    // CHECKSTYLE-OFF: JavadocType

    private static class MockFacebookServiceProvider implements OAuth2ServiceProvider<Facebook> {
        @Override
        public OAuth2Operations getOAuthOperations() {
            return null;
        }

        @Override
        public Facebook getApi(String accessToken) {
            return null;
        }
    }

    public static class MockFacebookAdapter implements ApiAdapter<Facebook> {

        public static final String EMAIL = "peter@framptonindustries.com";
        public static final String USERNAME = "peterframpton69";

        @Override
        public boolean test(Facebook api) {
            return false;
        }

        @Override
        public void setConnectionValues(Facebook api, ConnectionValues values) {
        }

        @Override
        public UserProfile fetchUserProfile(Facebook api) {
            UserProfileBuilder builder = new UserProfileBuilder();
            builder.setEmail(EMAIL);
            builder.setUsername(USERNAME);
            return builder.build();
        }

        @Override
        public void updateStatus(Facebook api, String message) {
        }
    }

    private static class MockFacebookConnectionFactory extends OAuth2ConnectionFactory<Facebook> {
        public MockFacebookConnectionFactory() {
            super("facebook", new MockFacebookServiceProvider(), new MockFacebookAdapter());
        }
    }

    // CHECKSTYLE-ON: JavadocType
}
