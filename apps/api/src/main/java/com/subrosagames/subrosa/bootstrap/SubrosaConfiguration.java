package com.subrosagames.subrosa.bootstrap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.subrosagames.subrosa.domain.DomainObjectRepositoryFactoryBean;

/**
 * Base configuration class for application.
 */
@Configuration
@EnableJpaRepositories(
        basePackageClasses = com.subrosagames.subrosa.domain.Marker.class,
        repositoryFactoryBeanClass = DomainObjectRepositoryFactoryBean.class
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

}
