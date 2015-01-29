package com.subrosagames.subrosa.bootstrap;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * Base configuration class for application.
 */
@Configuration
@ImportResource("classpath:com/subrosagames/subrosa/subrosa-application-context.xml")
public class SubrosaConfiguration {
}
