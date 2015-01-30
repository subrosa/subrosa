package com.subrosagames.subrosa.bootstrap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

/**
 * Base configuration class for application.
 */
@ImportResource("classpath:com/subrosagames/subrosa/subrosa-application-context.xml")
@Configuration
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
