package com.subrosagames.subrosa.domain.file;

import java.io.IOException;
import java.io.InputStream;

/**
 */
public interface FileStorer {
    long store(InputStream inputStream, String identifier) throws IOException;
}
