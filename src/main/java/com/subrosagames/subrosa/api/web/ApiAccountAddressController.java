package com.subrosagames.subrosa.api.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.subrosagames.subrosa.api.NotAuthenticatedException;
import com.subrosagames.subrosa.api.dto.AddressDescriptor;
import com.subrosagames.subrosa.domain.account.AccountNotFoundException;
import com.subrosagames.subrosa.domain.account.Address;
import com.subrosagames.subrosa.domain.account.AddressNotFoundException;
import com.subrosagames.subrosa.domain.account.AddressValidationException;
import com.subrosagames.subrosa.domain.image.ImageNotFoundException;
import com.subrosagames.subrosa.service.AccountService;
import com.subrosagames.subrosa.service.PaginatedList;
import com.subrosagames.subrosa.util.ObjectUtils;

/**
 * Controller for {@link Address} related CRUD operations.
 */
@Controller
public class ApiAccountAddressController extends BaseApiController {

    private static final Logger LOG = LoggerFactory.getLogger(ApiAccountAddressController.class);

    @Autowired
    private AccountService accountService;

    /**
     * Get a paginated list of account addresses.
     *
     * @param accountId   account id
     * @param limitParam  limit
     * @param offsetParam offset
     * @return paginated list of addresses
     * @throws NotAuthenticatedException if request is unauthenticated
     * @throws AccountNotFoundException  if account is not found
     */
    @RequestMapping(value = { "/account/{accountId}/address", "/account/{accountId}/address/" }, method = RequestMethod.GET)
    @ResponseBody
    public PaginatedList<Address> listAddresses(@PathVariable("accountId") Integer accountId,
                                                @RequestParam(value = "limit", required = false) Integer limitParam,
                                                @RequestParam(value = "offset", required = false) Integer offsetParam)
            throws NotAuthenticatedException, AccountNotFoundException
    {
        LOG.debug("{}: listing addresses for {}", getAuthenticatedUser().getId(), accountId);
        int limit = ObjectUtils.defaultIfNull(limitParam, 10);
        int offset = ObjectUtils.defaultIfNull(offsetParam, 0);
        return accountService.listAddresses(accountId, limit, offset);
    }

    /**
     * Get a paginated list of account addresses.
     *
     * @param limit  limit
     * @param offset offset
     * @return paginated list of addresses
     * @throws NotAuthenticatedException if request is unauthenticated
     * @throws AccountNotFoundException  if account is not found
     */
    @RequestMapping(value = { "/user/address", "/user/address/" }, method = RequestMethod.GET)
    @ResponseBody
    public PaginatedList<Address> listAddressesForAuthenticatedUser(@RequestParam(value = "limit", required = false) Integer limit,
                                                                    @RequestParam(value = "offset", required = false) Integer offset)
            throws NotAuthenticatedException, AccountNotFoundException
    {
        return listAddresses(getAuthenticatedUser().getId(), limit, offset);
    }

    /**
     * Get an account address profile.
     *
     * @param accountId account id
     * @param addressId address id
     * @return account address
     * @throws NotAuthenticatedException if request is not authenticated
     * @throws AccountNotFoundException  if account is not found
     * @throws AddressNotFoundException  if address is not found
     */
    @RequestMapping(value = { "/account/{accountId}/address/{addressId}", "/account/{accountId}/address/{addressId}/" }, method = RequestMethod.GET)
    @ResponseBody
    public Address getAddress(@PathVariable("accountId") Integer accountId,
                              @PathVariable("addressId") Integer addressId)
            throws NotAuthenticatedException, AccountNotFoundException, AddressNotFoundException
    {
        LOG.debug("{}: get address profile {} for {}", getAuthenticatedUser().getId(), addressId, accountId);
        return accountService.getAddress(accountId, addressId);
    }

    /**
     * Get an account address profile.
     *
     * @param addressId address id
     * @return account address
     * @throws NotAuthenticatedException if request is not authenticated
     * @throws AccountNotFoundException  if account is not found
     * @throws AddressNotFoundException  if address is not found
     */
    @RequestMapping(value = { "/user/address/{addressId}", "/user/address/{addressId}/" }, method = RequestMethod.GET)
    @ResponseBody
    public Address getAddressForAuthenticatedUser(@PathVariable("addressId") Integer addressId)
            throws NotAuthenticatedException, AccountNotFoundException, AddressNotFoundException
    {
        return getAddress(getAuthenticatedUser().getId(), addressId);
    }

    /**
     * Create an account address profile.
     *
     * @param accountId                account id
     * @param addressDescriptor address profile information
     * @return created address
     * @throws NotAuthenticatedException  if request is not authenticated
     * @throws AccountNotFoundException   if account is not found
     * @throws ImageNotFoundException     if image is not found
     * @throws AddressValidationException if address profile is not valid for saving
     */
    @RequestMapping(value = { "/account/{accountId}/address", "/account/{accountId}/address/" }, method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public Address createAddress(@PathVariable("accountId") Integer accountId,
                                 @RequestBody AddressDescriptor addressDescriptor)
            throws NotAuthenticatedException, AccountNotFoundException, ImageNotFoundException, AddressValidationException
    {
        LOG.debug("{}: creating address profile for {}", getAuthenticatedUser().getId(), accountId);
        return accountService.createAddress(accountId, addressDescriptor);
    }

    /**
     * Create an account address profile.
     *
     * @param addressDescriptor address profile information
     * @return created address
     * @throws NotAuthenticatedException  if request is not authenticated
     * @throws AccountNotFoundException   if account is not found
     * @throws ImageNotFoundException     if image is not found
     * @throws AddressValidationException if address profile is not valid for saving
     */
    @RequestMapping(value = { "/user/address", "/user/address/" }, method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public Address createAddressForAuthenticatedUser(@RequestBody AddressDescriptor addressDescriptor)
            throws NotAuthenticatedException, AccountNotFoundException, ImageNotFoundException, AddressValidationException
    {
        return createAddress(getAuthenticatedUser().getId(), addressDescriptor);
    }

    /**
     * Update an account address profile.
     *
     * @param accountId                account id
     * @param addressId                address id
     * @param addressDescriptor address profile information
     * @return updated address profile
     * @throws NotAuthenticatedException  if request is unauthenticated
     * @throws AccountNotFoundException   if account is not found
     * @throws AddressNotFoundException   if address profile is not found
     * @throws ImageNotFoundException     if image is not found
     * @throws AddressValidationException if address profile is not valid for saving
     */
    @RequestMapping(value = { "/account/{accountId}/address/{addressId}", "/account/{accountId}/address/{addressId}/" }, method = RequestMethod.PUT)
    @ResponseBody
    public Address updateAddress(@PathVariable("accountId") Integer accountId,
                                 @PathVariable("addressId") Integer addressId,
                                 @RequestBody AddressDescriptor addressDescriptor)
            throws NotAuthenticatedException, AccountNotFoundException, ImageNotFoundException, AddressNotFoundException, AddressValidationException
    {
        LOG.debug("{}: updating address profile {} for account {}", getAuthenticatedUser().getId(), addressId, accountId);
        return accountService.updateAddress(accountId, addressId, addressDescriptor);
    }

    /**
     * Update an account address profile.
     *
     * @param addressId                address id
     * @param addressDescriptor address profile information
     * @return updated address profile
     * @throws NotAuthenticatedException  if request is unauthenticated
     * @throws AccountNotFoundException   if account is not found
     * @throws AddressNotFoundException   if address profile is not found
     * @throws ImageNotFoundException     if image is not found
     * @throws AddressValidationException if address profile is not valid for saving
     */
    @RequestMapping(value = { "/user/address/{addressId}", "/user/address/{addressId}/" }, method = RequestMethod.PUT)
    @ResponseBody
    public Address updateAddressForAuthenticatedUser(@PathVariable("addressId") Integer addressId,
                                                     @RequestBody AddressDescriptor addressDescriptor)
            throws NotAuthenticatedException, AccountNotFoundException, AddressNotFoundException, ImageNotFoundException, AddressValidationException
    {
        return updateAddress(getAuthenticatedUser().getId(), addressId, addressDescriptor);
    }

    /**
     * Delete an account address profile.
     *
     * @param accountId account id
     * @param addressId address id
     * @return deleted address profile
     * @throws NotAuthenticatedException if request is unauthenticated
     * @throws AccountNotFoundException  if account is not found
     * @throws AddressNotFoundException  if address profile is not found
     */
    @RequestMapping(value = { "/account/{accountId}/address/{addressId}", "/account/{accountId}/address/{addressId}/" }, method = RequestMethod.DELETE)
    @ResponseBody
    public Address deleteAddress(@PathVariable("accountId") Integer accountId,
                                 @PathVariable("addressId") Integer addressId)
            throws NotAuthenticatedException, AddressNotFoundException, AccountNotFoundException
    {
        LOG.debug("{}: deleting address profile {} for account {}", getAuthenticatedUser().getId(), addressId, accountId);
        return accountService.deleteAddress(accountId, addressId);
    }

    /**
     * Delete an account address profile.
     *
     * @param addressId address id
     * @return deleted address profile
     * @throws NotAuthenticatedException if request is unauthenticated
     * @throws AccountNotFoundException  if account is not found
     * @throws AddressNotFoundException  if address profile is not found
     */
    @RequestMapping(value = { "/user/address/{addressId}", "/user/address/{addressId}/" }, method = RequestMethod.DELETE)
    @ResponseBody
    public Address deleteAddressForAuthenticatedUser(@PathVariable("addressId") Integer addressId)
            throws NotAuthenticatedException, AddressNotFoundException, AccountNotFoundException
    {
        return deleteAddress(getAuthenticatedUser().getId(), addressId);
    }
}

