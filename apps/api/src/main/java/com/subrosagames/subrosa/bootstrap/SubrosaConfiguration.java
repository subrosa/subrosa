package com.subrosagames.subrosa.bootstrap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.subrosagames.subrosa.domain.file.FileStorer;
import com.subrosagames.subrosa.domain.file.FilesystemFileStorer;
import com.subrosagames.subrosa.security.SubrosaAclPermissionEvaluator;
import com.subrosagames.subrosa.security.permission.Permission;
import com.subrosagames.subrosa.util.bean.HibernateAwareObjectMapper;

/**
 * Base configuration class for application.
 */
@Configuration
@EnableJpaRepositories(
        basePackageClasses = com.subrosagames.subrosa.domain.Marker.class,
        repositoryBaseClass = com.subrosagames.subrosa.domain.DomainObjectRepositoryImpl.class
)
public class SubrosaConfiguration {

    @Autowired
    private SubrosaFiles subrosaFiles;

    /**
     * Multipart request resolver configured with max upload size.
     *
     * @return multipart resolver
     */
    @Bean
    public MultipartResolver getMultipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
//        multipartResolver.setMaxUploadSize(subrosaFiles.getMaxUploadSize());
        return multipartResolver;
    }

    @Bean
    public FileStorer fsFileStorer(SubrosaFiles subrosaFiles) {
        return new FilesystemFileStorer(subrosaFiles);
    }

    @Configuration
    @EnableRedisHttpSession
    public static class SessionConfiguration {
    }

    @Configuration
    @EnableResourceServer
    @Profile("!unit-test")
    protected static class ResourceServer extends ResourceServerConfigurerAdapter {

        @Resource
        private RedisConnectionFactory redisConnectionFactory;

        @Override
        public void configure(HttpSecurity http) throws Exception {
            http
                    .authorizeRequests()
                    .anyRequest().permitAll();
        }

        @Override
        public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
            resources
                    .tokenStore(new RedisTokenStore(redisConnectionFactory));
        }

    }

    @Configuration
    @EnableWebSecurity
    @EnableGlobalMethodSecurity(prePostEnabled = true)
    public static class MethodSecurityConfiguration extends GlobalMethodSecurityConfiguration {

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

    @Configuration
    @EnableWebMvc
    public static class ServletConfiguration extends WebMvcConfigurerAdapter {

        @Override
        public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
            converters.add(jacksonMessageConverter());
        }

        private MappingJackson2HttpMessageConverter jacksonMessageConverter() {
            MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
            HibernateAwareObjectMapper objectMapper = new HibernateAwareObjectMapper();
            objectMapper.registerModules(new Jdk8Module());
            objectMapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
            objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            converter.setObjectMapper(objectMapper);
            return converter;
        }
    }
}
