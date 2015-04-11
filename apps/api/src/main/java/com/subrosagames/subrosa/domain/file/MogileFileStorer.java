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
public class MogileFileStorer implements FileStorer {

    private SpringMojiBean springMojiBean;

    public MogileFileStorer(SpringMojiBean springMojiBean) {
        this.springMojiBean = springMojiBean;
    }

    @Override
    public long store(InputStream inputStream, String identifier) throws IOException {
        MojiFile mojiFile = springMojiBean.getFile(identifier);
        try (OutputStream fileStream = mojiFile.getOutputStream()) {
            ByteStreams.copy(inputStream, fileStream);
            fileStream.flush();
        }
        return mojiFile.length();
    }
}
