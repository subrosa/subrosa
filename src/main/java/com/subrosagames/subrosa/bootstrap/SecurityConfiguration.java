package com.subrosagames.subrosa.bootstrap;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.dao.ReflectionSaltSource;
import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.subrosagames.subrosa.security.DeviceSessionUserDetailsService;
import com.subrosagames.subrosa.security.Http200LogoutSuccessHandler;
import com.subrosagames.subrosa.security.JsonResponseAuthenticationFailureHandler;
import com.subrosagames.subrosa.security.JsonResponseAuthenticationSuccessHandler;
import com.subrosagames.subrosa.security.JsonUsernamePasswordAuthenticationFilter;
import com.subrosagames.subrosa.security.SubrosaAclPermissionEvaluator;
import com.subrosagames.subrosa.security.SubrosaSha512PasswordEncoder;
import com.subrosagames.subrosa.security.SubrosaUserDetailsService;
import com.subrosagames.subrosa.security.permission.Permission;

/**
 * Security configuration.
 */
@Configuration
@EnableWebMvcSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    // CHECKSTYLE-OFF: JavadocMethod

    @Autowired
    private SubrosaUserDetailsService subrosaUserDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        AuthenticationEntryPoint entryPoint = new Http403ForbiddenEntryPoint();
        LogoutSuccessHandler logouthandler = new Http200LogoutSuccessHandler();
        http
                .csrf().disable()
                .addFilterAfter(jsonUsernamePasswordAuthenticationFilter(), ExceptionTranslationFilter.class)
                .addFilterBefore(deviceSessionAuthenticationFilter(), BasicAuthenticationFilter.class)
                .exceptionHandling().authenticationEntryPoint(entryPoint)
                .and()
                .logout().logoutSuccessHandler(logouthandler);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .authenticationProvider(authenticationProvider())
                .authenticationProvider(preAuthenticatedAuthenticationProvider());
    }

    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        return subrosaUserDetailsService;
    }

    private AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        provider.setSaltSource(saltSource());
        return provider;
    }

    private SaltSource saltSource() {
        ReflectionSaltSource saltSource = new ReflectionSaltSource();
        saltSource.setUserPropertyToUse("salt");
        return saltSource;
    }

    private Object passwordEncoder() {
        return new SubrosaSha512PasswordEncoder();
    }

    @Bean
    public AuthenticationProvider preAuthenticatedAuthenticationProvider() {
        PreAuthenticatedAuthenticationProvider provider = new PreAuthenticatedAuthenticationProvider();
        provider.setThrowExceptionWhenTokenRejected(true);
        provider.setPreAuthenticatedUserDetailsService(deviceSessionUserDetailsService());
        return provider;
    }

    @Bean
    public AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> deviceSessionUserDetailsService() {
        return new DeviceSessionUserDetailsService();
    }

    @Bean
    public Filter jsonUsernamePasswordAuthenticationFilter() throws Exception {
        JsonUsernamePasswordAuthenticationFilter filter = new JsonUsernamePasswordAuthenticationFilter();
        filter.setAuthenticationSuccessHandler(jsonTokenAuthenticationSuccessHandler());
        filter.setAuthenticationFailureHandler(jsonResponseAuthenticationFailureHandler());
        filter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/v1/session", "POST"));
        filter.setAuthenticationManager(authenticationManager());
        return filter;
    }

    @Bean
    public AuthenticationFailureHandler jsonResponseAuthenticationFailureHandler() {
        return new JsonResponseAuthenticationFailureHandler();
    }

    @Bean
    public AuthenticationSuccessHandler jsonTokenAuthenticationSuccessHandler() {
        return new JsonResponseAuthenticationSuccessHandler();
    }

    @Bean
    public Filter deviceSessionAuthenticationFilter() throws Exception {
        RequestHeaderAuthenticationFilter filter = new RequestHeaderAuthenticationFilter();
        filter.setExceptionIfHeaderMissing(false);
        filter.setContinueFilterChainOnUnsuccessfulAuthentication(true);
        filter.setPrincipalRequestHeader("X-SUBROSA-AUTH");
        filter.setAuthenticationManager(authenticationManager());
        return filter;
    }

    /**
     * Global method security configuration.
     */
    @Configuration
    @EnableGlobalMethodSecurity(prePostEnabled = true)
    public static class GlobalMethodSecurity extends GlobalMethodSecurityConfiguration {

        @Autowired
        private Permission readAccountPermission;
        @Autowired
        private Permission writeAccountPermission;
        @Autowired
        private Permission readGamePermission;
        @Autowired
        private Permission writeGamePermission;
        @Autowired
        private Permission readPlayerPermission;

        @Override
        protected MethodSecurityExpressionHandler createExpressionHandler() {
            DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
            SubrosaAclPermissionEvaluator permissionEvaluator = new SubrosaAclPermissionEvaluator();
            Map<String, Permission> permissionMap = new HashMap<>(10);
            permissionMap.put("READ_ACCOUNT", readAccountPermission);
            permissionMap.put("WRITE_ACCOUNT", writeAccountPermission);
            permissionMap.put("READ_GAME", readGamePermission);
            permissionMap.put("WRITE_GAME", writeGamePermission);
            permissionMap.put("READ_PLAYER", readPlayerPermission);
            permissionEvaluator.setPermissionMap(permissionMap);
            handler.setPermissionEvaluator(permissionEvaluator);
            return handler;
        }

    }

    // CHECKSTYLE-ON: JavadocMethod
}
