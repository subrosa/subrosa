package com.subrosagames.subrosa.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.subrosagames.subrosa.bootstrap.SubrosaFiles;
import com.subrosagames.subrosa.domain.file.FileStorer;
import com.subrosagames.subrosa.domain.file.FilesystemFileStorer;

/**
 * Mocks and bean overrides for unit tests.
 */
@Configuration
@Profile("unit-test")
public class UnitTestConfiguration {

    @Autowired
    private SubrosaFiles subrosaFiles;

    /**
     * Filesystem-based file storage.
     *
     * @return filesystem file storer
     */
    @Bean
    public FileStorer filesystemFileStorer() {
        return new FilesystemFileStorer(subrosaFiles);
    }

}
