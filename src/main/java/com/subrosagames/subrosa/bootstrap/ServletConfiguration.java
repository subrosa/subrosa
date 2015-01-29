package com.subrosagames.subrosa.bootstrap;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * Base configuration class for application.
 */
@Configuration
@ImportResource("classpath:com/subrosagames/subrosa/api-v1-servlet.xml")
public class ServletConfiguration {
}
