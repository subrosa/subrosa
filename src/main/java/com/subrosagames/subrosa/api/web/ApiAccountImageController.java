package com.subrosagames.subrosa.api.web;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileUploadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import com.subrosagames.subrosa.api.NotAuthenticatedException;
import com.subrosagames.subrosa.domain.account.AccountNotFoundException;
import com.subrosagames.subrosa.domain.account.AccountValidationException;
import com.subrosagames.subrosa.domain.image.Image;
import com.subrosagames.subrosa.domain.image.ImageNotFoundException;
import com.subrosagames.subrosa.security.SecurityHelper;
import com.subrosagames.subrosa.service.ImageService;
import com.subrosagames.subrosa.service.PaginatedList;
import com.subrosagames.subrosa.util.ObjectUtils;

/**
 * Controller for {@link Image} related CRUD operations.
 */
@Controller
@RequestMapping("/account/{accountId}/image")
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
    @RequestMapping(value = { "", "/" }, method = RequestMethod.GET)
    @ResponseBody
    public PaginatedList<Image> listImages(@PathVariable("accountId") final Integer accountId,
                                           @RequestParam(value = "limit", required = false) Integer limitParam,
                                           @RequestParam(value = "offset", required = false) Integer offsetParam)
            throws NotAuthenticatedException, AccountNotFoundException
    {
        if (!SecurityHelper.isAuthenticated()) {
            throw new NotAuthenticatedException("Unauthenticated attempt to list account images.");
        }
        if (accountId == null) {
            throw new AccountNotFoundException("Missing account id");
        }
        int limit = ObjectUtils.defaultIfNull(limitParam, 10);
        int offset = ObjectUtils.defaultIfNull(offsetParam, 0);
        return imageService.listImages(accountId, limit, offset);
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
    @RequestMapping(value = { "/{imageId}", "/{imageId}/" }, method = RequestMethod.GET)
    @ResponseBody
    public Image getImage(@PathVariable("accountId") Integer accountId,
                          @PathVariable("imageId") Integer imageId)
            throws NotAuthenticatedException, AccountNotFoundException, ImageNotFoundException
    {
        if (!SecurityHelper.isAuthenticated()) {
            throw new NotAuthenticatedException("Unauthenticated attempt to get an account image.");
        }
        if (accountId == null) {
            throw new AccountNotFoundException("Missing account id");
        }
        return imageService.getImage(accountId, imageId);
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
    @RequestMapping(value = { "", "/" }, method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public Image uploadImage(@RequestPart MultipartFile file,
                             @PathVariable("accountId") Integer accountId,
                             HttpServletResponse response)
            throws NotAuthenticatedException, AccountNotFoundException,
            IOException, FileUploadException, AccountValidationException // SUPPRESS CHECKSTYLE RedundantThrowsCheck
    {
        if (!SecurityHelper.isAuthenticated()) {
            throw new NotAuthenticatedException("Unauthenticated attempt to upload an account image.");
        }
        Image image = imageService.uploadImageForAccount(accountId, file);
        response.setHeader("Location", "/account/" + accountId + "/image/" + image.getId());
        return image;
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
    @RequestMapping(value = { "/{imageId}/{size}.{format}" }, method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void downloadImage(@PathVariable("accountId") Integer accountId,
                              @PathVariable("imageId") Integer imageId,
                              @PathVariable("size") String size,
                              @PathVariable("format") String format,
                              HttpServletResponse response)
            throws NotAuthenticatedException, AccountNotFoundException, ImageNotFoundException, IOException
    {
        if (!SecurityHelper.isAuthenticated()) {
            throw new NotAuthenticatedException("Unauthenticated attempt to download an account image.");
        }
        imageService.streamImageData(imageId, response);
    }

}
