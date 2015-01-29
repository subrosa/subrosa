package com.subrosagames.subrosa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Entry point to subrosa application.
 */
// SUPPRESS CHECKSTYLE HideUtilityClassConstructor
@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan
// SUPPRESS CHECKSTYLE HideUtilityClassConstructor
public class SubrosaApplication { // SUPPRESS CHECKSTYLE HideUtilityClassConstructor

    private static final Logger LOG = LoggerFactory.getLogger(SubrosaApplication.class);

    /**
     * This is not a utility class!
     *
     * Spring boot requires that the default constructor exist.
     */
    public void noOp() {
    }

    /**
     * Main method. Starts application.
     *
     * @param args command line arguments
     * @throws Exception if anything goes wrong initializing the app
     */
    public static void main(String[] args) throws Exception {
        LOG.debug("Starting Subrosa application...");
        SpringApplication.run(SubrosaApplication.class, args);
        LOG.debug("Subrosa application running...");
    }

}
