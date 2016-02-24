package com.subrosagames.subrosa.domain.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.io.ByteStreams;
import com.subrosagames.subrosa.bootstrap.SubrosaFiles;

/**
 * Local filesystem implementation of file storage.
 */
public class FilesystemFileStorer implements FileStorer {

    private static final Logger LOG = LoggerFactory.getLogger(FilesystemFileStorer.class);

    private SubrosaFiles subrosaFiles;

    /**
     * Construct with provided file config.
     *
     * @param subrosaFiles file config
     */
    public FilesystemFileStorer(SubrosaFiles subrosaFiles) {
        this.subrosaFiles = subrosaFiles;
    }

    @Override
    public long store(InputStream inputStream, String identifier) throws IOException {
        File physicalFile = getPhysicalFile(identifier);
        if (LOG.isInfoEnabled()) {
            LOG.info("Storing file with uuid {} at location {}.", identifier, physicalFile.getCanonicalPath());
        }
        try (OutputStream fileOutputStream = FileUtils.openOutputStream(physicalFile)) {
            ByteStreams.copy(inputStream, fileOutputStream);
        }
        return physicalFile.length();
    }

    private File getPhysicalFile(String identifier) {
        String relativePath = StringUtils.join(
                Iterables.toArray(Splitter.fixedLength(2).split(identifier), String.class),
                File.separator);
        return new File(subrosaFiles.getAssetDirectory() + File.separator + relativePath, identifier);
    }

}
