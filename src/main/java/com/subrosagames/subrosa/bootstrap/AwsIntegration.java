package com.subrosagames.subrosa.bootstrap;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Maps AWS configurations.
 */
@Component
@ConfigurationProperties("subrosa.aws")
public class AwsIntegration {

    private String accessKey;
    private String secretKey;
    private String imageBucket;

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getImageBucket() {
        return imageBucket;
    }

    public void setImageBucket(String imageBucket) {
        this.imageBucket = imageBucket;
    }
}
