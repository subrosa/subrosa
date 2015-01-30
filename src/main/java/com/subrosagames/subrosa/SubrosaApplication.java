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
@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan
public class SubrosaApplication {

    private static final Logger LOG = LoggerFactory.getLogger(SubrosaApplication.class);

    /**
     * This is not a utility class!
     *
     * Spring boot requires that the default constructor exist, and this is my dumb way of telling
     * checkstyle that this class should be instantiable.
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
