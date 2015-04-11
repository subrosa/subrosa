package com.subrosagames.subrosa.bootstrap;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.subrosagames.subrosa.test.AbstractContextTest;

import static org.junit.Assert.assertNotNull;

/**
 * Tests the various configuration/bootstrapping classes.
 */
public class ConfigurationTest extends AbstractContextTest {

    // CHECKSTYLE-OFF: JavadocMethod

    @Autowired
    private SubrosaFiles subrosaFiles;

    @Autowired
    private AwsIntegration awsIntegration;

    @Autowired
    private GoogleIntegration googleIntegration;

    @Test
    public void testFilesConfigurations() throws Exception {
        assertNotNull(subrosaFiles);
        assertNotNull(subrosaFiles.getMaxUploadSize());
    }

    @Test
    public void testAwsIntegration() throws Exception {
        assertNotNull(awsIntegration);
    }

    @Test
    public void testGoogleIntegration() throws Exception {
        assertNotNull(googleIntegration);
    }

    // CHECKSTYLE-ON: JavadocMethod
}
