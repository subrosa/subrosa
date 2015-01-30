package com.subrosagames.subrosa.bootstrap;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Maps Google API configurations.
 */
@Component
@ConfigurationProperties("subrosa.google")
public class GoogleIntegration {

    private String geocodeEndpoint;
    private String geocodeApiKey;
    private String gcmApiKey;

    public String getGeocodeEndpoint() {
        return geocodeEndpoint;
    }

    public void setGeocodeEndpoint(String geocodeEndpoint) {
        this.geocodeEndpoint = geocodeEndpoint;
    }

    public String getGeocodeApiKey() {
        return geocodeApiKey;
    }

    public void setGeocodeApiKey(String geocodeApiKey) {
        this.geocodeApiKey = geocodeApiKey;
    }

    public String getGcmApiKey() {
        return gcmApiKey;
    }

    public void setGcmApiKey(String gcmApiKey) {
        this.gcmApiKey = gcmApiKey;
    }
}
