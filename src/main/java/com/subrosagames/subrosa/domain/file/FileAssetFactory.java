package com.subrosagames.subrosa.domain.file;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.subrosagames.subrosa.bootstrap.SubrosaFiles;
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

    /**
     * Create a file asset for the given multipart file upload.
     *
     * @param multipartFile multipart file
     * @return file asset
     * @throws IOException         if file transfer fails
     * @throws FileUploadException if file upload fails to meet restrictions
     */
    public FileAsset fileAssetForMultipartFile(MultipartFile multipartFile) throws IOException, FileUploadException { // SUPPRESS CHECKSTYLE RedundantThrowsCheck
        File physicalFile;
        FileAsset fileAsset = new FileAsset();

        fileAsset.setName(FilenameUtils.getName(multipartFile.getOriginalFilename()));
        fileAsset = fileAssetRepository.create(fileAsset);

        // store physical file
        physicalFile = storeFileItemStreamToDisk(multipartFile, fileAsset);

        long maxUploadSize = subrosaFiles.getMaxUploadSize();
        if (maxUploadSize >= 0 && physicalFile.length() > maxUploadSize) {
            if (!physicalFile.delete()) {
                LOG.warn("Failed to delete file {} that exceeded upload size limit", physicalFile.getCanonicalPath());
            }
            throw new FileSizeLimitExceededException("Upload exceeds maximum file size", physicalFile.length(), maxUploadSize);
        }

        MagicMatch match;
        try {
            match = Magic.getMagicMatch(physicalFile, true);
            fileAsset.setMimeType(match.getMimeType());
        } catch (MagicParseException | MagicMatchNotFoundException | MagicException e) {
            LOG.error("Could not determine mime type.", e);
        }
        fileAsset.setSize(physicalFile.length());
        fileAssetRepository.update(fileAsset);

        if (LOG.isInfoEnabled()) {
            LOG.info("Successfully created new file: {} => {} ({})", fileAsset.getId(), fileAsset.getName(), fileAsset.getMimeType());
        }
        return fileAsset;
    }

    private File storeFileItemStreamToDisk(MultipartFile fileItemStream, FileAsset fileAsset) throws IOException {
        File physicalFile = getPhysicalFile(fileAsset);
        if (LOG.isInfoEnabled()) {
            LOG.info("Storing file with id {} at location {}.", fileAsset.getId(), physicalFile.getCanonicalPath());
        }
        OutputStream fileOutputStream = null;
        try {
            fileOutputStream = FileUtils.openOutputStream(physicalFile);
            IOUtils.copy(fileItemStream.getInputStream(), fileOutputStream);
        } finally {
            IOUtils.closeQuietly(fileOutputStream);
        }
        return physicalFile;
    }

    private File getPhysicalFile(FileAsset fileAsset) {
        return new File(subrosaFiles.getAssetDirectory(), fileAsset.getName());
    }
}
