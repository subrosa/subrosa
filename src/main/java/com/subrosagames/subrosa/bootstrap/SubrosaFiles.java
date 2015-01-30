package com.subrosagames.subrosa.bootstrap;

import java.io.File;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * File management configurations.
 */
@Component
@ConfigurationProperties("subrosa.files")
public class SubrosaFiles {

    private Long maxUploadSize;
    private File assetDirectory;

    public Long getMaxUploadSize() {
        return maxUploadSize;
    }

    public void setMaxUploadSize(Long maxUploadSize) {
        this.maxUploadSize = maxUploadSize;
    }

    public File getAssetDirectory() {
        return assetDirectory;
    }

    public void setAssetDirectory(File assetDirectory) {
        this.assetDirectory = assetDirectory;
    }
}
