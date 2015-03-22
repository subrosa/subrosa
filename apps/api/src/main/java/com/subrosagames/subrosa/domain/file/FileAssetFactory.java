package com.subrosagames.subrosa.domain.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.io.ByteStreams;
import com.subrosagames.subrosa.bootstrap.SubrosaFiles;
import fm.last.moji.MojiFile;
import fm.last.moji.spring.SpringMojiBean;
import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicException;
import net.sf.jmimemagic.MagicMatch;
import net.sf.jmimemagic.MagicMatchNotFoundException;
import net.sf.jmimemagic.MagicParseException;

import static org.apache.commons.fileupload.FileUploadBase.FileSizeLimitExceededException;

/**
 * Responsible for the creation of {@link FileAsset} objects.
 */
@Component
public class FileAssetFactory {

    private static final Logger LOG = LoggerFactory.getLogger(FileAssetFactory.class);

    @Autowired
    private FileAssetRepository fileAssetRepository;

    @Autowired
    private SubrosaFiles subrosaFiles;

    @Autowired
    private FileStorer fileStorer;

    /**
     * Create a file asset for the given multipart file upload.
     *
     * @param multipartFile multipart file
     * @return file asset
     * @throws IOException         if file transfer fails
     * @throws FileUploadException if file upload fails to meet restrictions
     */
    public FileAsset fileAssetForMultipartFile(MultipartFile multipartFile) throws IOException, FileUploadException { // SUPPRESS CHECKSTYLE RedundantThrowsCheck

        long maxUploadSize = subrosaFiles.getMaxUploadSize();
        if (maxUploadSize >= 0 && multipartFile.getSize() > maxUploadSize) {
            throw new FileSizeLimitExceededException("Upload exceeds maximum file size", multipartFile.getSize(), maxUploadSize);
        }

        FileAsset fileAsset = new FileAsset();
        fileAsset.setName(FilenameUtils.getName(multipartFile.getOriginalFilename()));
        fileAsset.setUuid(UUID.randomUUID().toString().replace("-", ""));
        fileAsset = fileAssetRepository.create(fileAsset);

        // store physical file
        long length = storeFileStream(multipartFile, fileAsset);

        // determine mime type
        MagicMatch match;
        try {
            // TODO I'm worried that this will force loading the entire file into memory - we should find another way if possible
            match = Magic.getMagicMatch(multipartFile.getBytes(), true);
            fileAsset.setMimeType(match.getMimeType());
        } catch (MagicParseException | MagicMatchNotFoundException | MagicException e) {
            LOG.error("Could not determine mime type.", e);
        }
        fileAsset.setSize(length);
        fileAssetRepository.update(fileAsset);

        if (LOG.isInfoEnabled()) {
            LOG.info("Successfully created new file: {} => {} ({})", fileAsset.getId(), fileAsset.getName(), fileAsset.getMimeType());
        }
        return fileAsset;
    }

    private long storeFileStream(MultipartFile multipartFile, FileAsset fileAsset) throws IOException {
        return fileStorer.store(multipartFile.getInputStream(), fileAsset.getUuid());
    }

    public void setFileStorer(FileStorer fileStorer) {
        this.fileStorer = fileStorer;
    }

}
