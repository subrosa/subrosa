package com.subrosagames.gateway;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.subrosagames.gateway.auth.SubrosaPasswordEncoder;
import com.subrosagames.gateway.auth.SubrosaUserDetailsService;
import com.subrosagames.subrosa.api.notification.GeneralCode;
import com.subrosagames.subrosa.api.notification.Notification;
import com.subrosagames.subrosa.api.notification.NotificationList;
import com.subrosagames.subrosa.api.notification.Severity;

@SpringBootApplication
@EnableZuulProxy
@EnableJpaRepositories
@ControllerAdvice
public class GatewayApplication {

    private static final Logger LOG = LoggerFactory.getLogger(GatewayApplication.class);

    static final String ACCOUNT_AUTH_MANAGER = "account-auth-manager";

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    /**
     * Catch-all for exceptions.
     *
     * @param e exception
     * @return notification list
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public NotificationList handleException(Exception e) {
        LOG.debug("Global exception handler: {}", e.getMessage());
        Notification notification = new Notification(
                GeneralCode.INTERNAL_ERROR, Severity.ERROR, e.getMessage());
        return new NotificationList(notification);
    }

    @Configuration
    @EnableWebMvcSecurity
    public static class SecurityConfiguration extends WebSecurityConfigurerAdapter {

        @Resource
        private SubrosaUserDetailsService userDetailsService;

        @Resource
        private SubrosaPasswordEncoder passwordEncoder;

        @Bean(name = ACCOUNT_AUTH_MANAGER)
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .formLogin().disable()
                    .csrf().disable()
            ;
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth
                    .userDetailsService(userDetailsService)
                    .passwordEncoder(new PasswordEncoder() {
                        @Override
                        public String encode(CharSequence rawPassword) {
                            return passwordEncoder.encode(rawPassword);
                        }

                        @Override
                        public boolean matches(CharSequence rawPassword, String encodedPassword) {
                            return passwordEncoder.matches(rawPassword, encodedPassword);
                        }
                    })
            ;
        }
    }

    @Configuration
    @EnableAuthorizationServer
    public static class OAuth2Config extends AuthorizationServerConfigurerAdapter {

        static final String SUBROSA_REALM = "subrosa";

        @Resource(name = ACCOUNT_AUTH_MANAGER)
        private AuthenticationManager authenticationManager;

        @Resource
        private DataSource datastore;

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
            endpoints
                    .tokenStore(tokenStore())
                    .authenticationManager(authenticationManager);
        }

        @Override
        public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
            security
                    .realm(SUBROSA_REALM);
        }

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            clients
                    .inMemory()
                    .withClient("subrosa")
                    .secret("subrosa")
                    .authorizedGrantTypes("client_credentials", "authorization_code", "refresh_token", "password")
                    .authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT")
                    .scopes("read", "write", "trust");
        }

        @Bean
        public TokenStore tokenStore() {
            return new JdbcTokenStore(datastore);
        }
    }

}
