package com.subrosagames.subrosa.service;

import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.fileupload.FileUploadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Lists;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.account.AccountFactory;
import com.subrosagames.subrosa.domain.account.AccountNotFoundException;
import com.subrosagames.subrosa.domain.account.AccountValidationException;
import com.subrosagames.subrosa.domain.file.FileAsset;
import com.subrosagames.subrosa.domain.file.FileAssetFactory;
import com.subrosagames.subrosa.domain.image.Image;
import com.subrosagames.subrosa.domain.image.ImageNotFoundException;

/**
 * Service layer for managing images.
 */
@Service
@Transactional
public class ImageService {

    private static final Logger LOG = LoggerFactory.getLogger(ImageService.class);

    @Autowired
    private AccountFactory accountFactory;

    @Autowired
    private FileAssetFactory fileAssetFactory;

    /**
     * Stream image data to the given HTTP servlet response.
     *
     * @param accountId account id
     * @param imageId   image id
     * @param response  http servlet response
     */
    public void streamImageData(int accountId, int imageId, HttpServletResponse response) {
        LOG.debug("Handling download for image {}", imageId);
    }

    /**
     * Handle image upload and store on the specified account.
     *
     * @param accountId     account id
     * @param multipartFile multipart file
     * @return account image
     * @throws AccountNotFoundException   if account does not exist
     * @throws AccountValidationException if account validation fails
     * @throws IOException                if file transfer fails
     * @throws FileUploadException        if file upload fails to meet restrictions
     */
    @PostAuthorize("hasPermission(#accountId, 'Account', 'WRITE_ACCOUNT')")
    public Image uploadImageForAccount(int accountId, MultipartFile multipartFile)
            throws AccountNotFoundException, AccountValidationException, IOException, FileUploadException // SUPPRESS CHECKSTYLE RedundantThrowsCheck
    {
        LOG.debug("Handling image upload for accountId {}", accountId);
        Account account = accountFactory.getAccount(accountId);

        FileAsset fileAsset = fileAssetFactory.fileAssetForMultipartFile(multipartFile);
        if (fileAsset == null) {
            throw new FileUploadException("File upload failed - no file was found to store");
        }

        Image image = accountFactory.imageForFileAsset(account, fileAsset);
        account.addImage(image);
        return image;
    }

    /**
     * Get a paginated list of the specified account's images.
     *
     * @param accountId account id
     * @param limit     number of images to return
     * @param offset    offset into images
     * @return paginated list of images
     * @throws AccountNotFoundException if account does not exist
     */
    @PostAuthorize("hasPermission(#accountId, 'Account', 'READ_ACCOUNT')")
    public PaginatedList<Image> listImages(int accountId, int limit, int offset) throws AccountNotFoundException {
        LOG.debug("Retrieving list of images for accountId {}", accountId);
        Account account = accountFactory.getAccount(accountId);
        List<Image> images = account.getImages();
        if (CollectionUtils.isEmpty(images)) {
            return new PaginatedList<>(Lists.<Image>newArrayList(), 0, limit, offset);
        } else {
            return new PaginatedList<>(
                    images.subList(offset, Math.min(images.size(), offset + limit)),
                    images.size(), limit, offset);
        }
    }

    /**
     * Retrieve an account image.
     *
     * @param accountId account id
     * @param imageId   image id
     * @return account image
     * @throws AccountNotFoundException if account does not exist
     * @throws ImageNotFoundException   if image does not exist
     */
    @PostAuthorize("hasPermission(#accountId, 'Account', 'READ_ACCOUNT')")
    public Image getImage(int accountId, int imageId) throws AccountNotFoundException, ImageNotFoundException {
        LOG.debug("Retrieving image {} for accountId {}", imageId, accountId);
        Account account = accountFactory.getAccount(accountId);
        return account.getImage(imageId);
    }
}
