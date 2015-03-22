package com.subrosagames.subrosa.domain.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.io.ByteStreams;
import fm.last.moji.MojiFile;
import fm.last.moji.spring.SpringMojiBean;

/**
 */
@Component
public class MogileFileStorer implements FileStorer {

    @Autowired
    private SpringMojiBean moji;

    @Override
    public long store(InputStream inputStream, String identifier) throws IOException {
        MojiFile mojiFile = moji.getFile(identifier);
        try (OutputStream fileStream = mojiFile.getOutputStream()) {
            ByteStreams.copy(inputStream, fileStream);
            fileStream.flush();
        }
        return mojiFile.length();
    }
}
