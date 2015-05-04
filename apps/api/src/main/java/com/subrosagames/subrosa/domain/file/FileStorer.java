package com.subrosagames.subrosa.domain.file;

import java.io.IOException;
import java.io.InputStream;

/**
 * Defines interface for a file storage handler.
 */
public interface FileStorer {
    /**
     * Store the given input stream as a file with the given identifier.
     *
     * @param inputStream file data
     * @param identifier file identifier
     * @return size of file in bytes
     * @throws IOException if storage fails
     */
    long store(InputStream inputStream, String identifier) throws IOException;
}
