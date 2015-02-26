package com.subrosagames.subrosa.api.web;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileUploadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.subrosagames.subrosa.api.NotAuthenticatedException;
import com.subrosagames.subrosa.domain.account.AccountNotFoundException;
import com.subrosagames.subrosa.domain.account.AccountValidationException;
import com.subrosagames.subrosa.domain.account.ImageInUseException;
import com.subrosagames.subrosa.domain.image.Image;
import com.subrosagames.subrosa.domain.image.ImageNotFoundException;
import com.subrosagames.subrosa.service.ImageService;
import com.subrosagames.subrosa.service.PaginatedList;
import com.subrosagames.subrosa.util.ObjectUtils;

/**
 * Controller for {@link Image} related CRUD operations.
 */
@RestController
public class ApiAccountImageController extends BaseApiController {

    private static final Logger LOG = LoggerFactory.getLogger(ApiAccountImageController.class);

    @Autowired
    private ImageService imageService;

    /**
     * Get a paginated list of account images.
     *
     * @param accountId   account id
     * @param limitParam  limit
     * @param offsetParam offset
     * @return paginated list of images
     * @throws NotAuthenticatedException if request is unauthenticated
     * @throws AccountNotFoundException  if account is not found
     */
    @RequestMapping(value = { "/account/{accountId}/image", "/account/{accountId}/image/" }, method = RequestMethod.GET)
    public PaginatedList<Image> listImages(@PathVariable("accountId") Integer accountId,
                                           @RequestParam(value = "limit", required = false) Integer limitParam,
                                           @RequestParam(value = "offset", required = false) Integer offsetParam)
            throws NotAuthenticatedException, AccountNotFoundException
    {
        LOG.debug("{}: listing images for {}", getAuthenticatedUser().getId(), accountId);
        int limit = ObjectUtils.defaultIfNull(limitParam, 10);
        int offset = ObjectUtils.defaultIfNull(offsetParam, 0);
        return imageService.listImages(accountId, limit, offset);
    }

    /**
     * Get a paginated list of account images.
     *
     * @param limit  limit
     * @param offset offset
     * @return paginated list of images
     * @throws NotAuthenticatedException if request is unauthenticated
     * @throws AccountNotFoundException  if account is not found
     */
    @RequestMapping(value = { "/user/image", "/user/image/" }, method = RequestMethod.GET)
    public PaginatedList<Image> listImagesForAuthenticatedUser(@RequestParam(value = "limit", required = false) Integer limit,
                                                               @RequestParam(value = "offset", required = false) Integer offset)
            throws NotAuthenticatedException, AccountNotFoundException
    {
        return listImages(getAuthenticatedUser().getId(), limit, offset);
    }

    /**
     * Get an account image.
     *
     * @param accountId account id
     * @param imageId   image id
     * @return account image
     * @throws NotAuthenticatedException if request is not authenticated
     * @throws AccountNotFoundException  if account is not found
     * @throws ImageNotFoundException    if image is not found
     */
    @RequestMapping(value = { "/account/{accountId}/image/{imageId}", "/account/{accountId}/image/{imageId}/" }, method = RequestMethod.GET)
    public Image getImage(@PathVariable("accountId") Integer accountId,
                          @PathVariable("imageId") Integer imageId)
            throws NotAuthenticatedException, AccountNotFoundException, ImageNotFoundException
    {
        LOG.debug("{}: get image {} for {}", getAuthenticatedUser().getId(), imageId, accountId);
        return imageService.getImage(accountId, imageId);
    }

    /**
     * Get an account image.
     *
     * @param imageId image id
     * @return account image
     * @throws NotAuthenticatedException if request is not authenticated
     * @throws AccountNotFoundException  if account is not found
     * @throws ImageNotFoundException    if image is not found
     */
    @RequestMapping(value = { "/user/image/{imageId}", "/user/image/{imageId}/" }, method = RequestMethod.GET)
    public Image getImageForAuthenticatedUser(@PathVariable("imageId") Integer imageId)
            throws NotAuthenticatedException, AccountNotFoundException, ImageNotFoundException
    {
        return getImage(getAuthenticatedUser().getId(), imageId);
    }

    /**
     * Upload an account image.
     *
     * @param file      multipart file
     * @param accountId account id
     * @param response  http servlet response
     * @return created image
     * @throws NotAuthenticatedException  if request is not authenticated
     * @throws AccountNotFoundException   if account is not found
     * @throws IOException                if transfer fails
     * @throws FileUploadException        if file could not be accepted
     * @throws AccountValidationException if account is invalid for image storage
     */
    @RequestMapping(value = { "/account/{accountId}/image", "/account/{accountId}/image/" }, method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public Image uploadImage(@PathVariable("accountId") Integer accountId,
                             @RequestPart MultipartFile file,
                             HttpServletResponse response)
            throws NotAuthenticatedException, AccountNotFoundException,
            IOException, FileUploadException, AccountValidationException // SUPPRESS CHECKSTYLE RedundantThrowsCheck
    {
        LOG.debug("{}: uploading image for {}", getAuthenticatedUser().getId(), accountId);
        Image image = imageService.uploadImageForAccount(accountId, file);
        response.setHeader("Location", "/account/" + accountId + "/image/" + image.getId());
        return image;
    }

    /**
     * Upload an account image.
     *
     * @param file     multipart file
     * @param response http servlet response
     * @return created image
     * @throws NotAuthenticatedException  if request is not authenticated
     * @throws AccountNotFoundException   if account is not found
     * @throws IOException                if transfer fails
     * @throws FileUploadException        if file could not be accepted
     * @throws AccountValidationException if account is invalid for image storage
     */
    @RequestMapping(value = { "/user/image", "/user/image/" }, method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public Image uploadImageForAuthenticatedUser(@RequestPart MultipartFile file,
                                                 HttpServletResponse response)
            throws NotAuthenticatedException, AccountNotFoundException,
            IOException, FileUploadException, AccountValidationException // SUPPRESS CHECKSTYLE RedundantThrowsCheck
    {
        return uploadImage(getAuthenticatedUser().getId(), file, response);
    }

    /**
     * Download file data for account image.
     *
     * @param accountId account id
     * @param imageId   image id
     * @param size      image dimensions
     * @param format    image format
     * @param response  http servlet response
     * @throws NotAuthenticatedException if request is not authenticated
     * @throws AccountNotFoundException  if account is not found
     * @throws ImageNotFoundException    if image is not found
     * @throws IOException               if transfer fails
     */
    @RequestMapping(value = { "/account/{accountId}/image/{imageId}/{size}.{format}" }, method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public void downloadImage(@PathVariable("accountId") Integer accountId,
                              @PathVariable("imageId") Integer imageId,
                              @PathVariable("size") String size,
                              @PathVariable("format") String format,
                              HttpServletResponse response)
            throws NotAuthenticatedException, AccountNotFoundException, ImageNotFoundException, IOException
    {
        LOG.debug("{}: downloading image {} for {}", getAuthenticatedUser().getId(), imageId, accountId);
        imageService.streamImageData(accountId, imageId, response);
    }

    /**
     * Download file data for account image.
     *
     * @param imageId  image id
     * @param size     image dimensions
     * @param format   image format
     * @param response http servlet response
     * @throws NotAuthenticatedException if request is not authenticated
     * @throws AccountNotFoundException  if account is not found
     * @throws ImageNotFoundException    if image is not found
     * @throws IOException               if transfer fails
     */
    @RequestMapping(value = { "/user/image/{imageId}/{size}.{format}" }, method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public void downloadImageForAuthenticatedUser(@PathVariable("imageId") Integer imageId,
                                                  @PathVariable("size") String size,
                                                  @PathVariable("format") String format,
                                                  HttpServletResponse response)
            throws NotAuthenticatedException, AccountNotFoundException, ImageNotFoundException, IOException
    {
        downloadImage(getAuthenticatedUser().getId(), imageId, size, format, response);
    }

    /**
     * Delete an account image.
     *
     * @param accountId account id
     * @param imageId   image id
     * @return deleted image
     * @throws NotAuthenticatedException if request is not authenticated
     * @throws AccountNotFoundException  if account is not found
     * @throws ImageNotFoundException    if image is not found
     * @throws ImageInUseException       if image is in use
     */
    @RequestMapping(value = { "/account/{accountId}/image/{imageId}", "/account/{accountId}/image/{imageId}" }, method = RequestMethod.DELETE)
    public Image deleteImage(@PathVariable("accountId") Integer accountId,
                             @PathVariable("imageId") Integer imageId)
            throws NotAuthenticatedException, ImageNotFoundException, AccountNotFoundException, ImageInUseException
    {
        LOG.debug("{}: deleting image {} for {}", getAuthenticatedUser().getId(), imageId, accountId);
        return imageService.deleteImage(accountId, imageId);
    }

    /**
     * Delete an image for the authenticated user.
     *
     * @param imageId image id
     * @return deleted image
     * @throws NotAuthenticatedException if request is not authenticated
     * @throws AccountNotFoundException  if account is not found
     * @throws ImageNotFoundException    if image is not found
     * @throws ImageInUseException       if image is in use
     */
    @RequestMapping(value = { "/user/image/{imageId}", "/user/image/{imageId}" }, method = RequestMethod.DELETE)
    public Image deleteImageForAuthenticatedUser(@PathVariable("imageId") Integer imageId)
            throws NotAuthenticatedException, ImageNotFoundException, AccountNotFoundException, ImageInUseException

    {
        return deleteImage(getAuthenticatedUser().getId(), imageId);
    }

}
