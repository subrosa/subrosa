package com.subrosagames.subrosa.test;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * Configuration overrides for unit tests.
 */
@Configuration
@ComponentScan(basePackages = "com.subrosagames.subrosa")
@ImportResource("classpath:test-context.xml")
public class TestConfiguration {
}
