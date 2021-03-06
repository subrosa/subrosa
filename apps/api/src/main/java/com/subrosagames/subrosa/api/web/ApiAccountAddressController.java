package com.subrosagames.subrosa.api.web;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.subrosagames.subrosa.api.NotAuthenticatedException;
import com.subrosagames.subrosa.api.dto.AddressDescriptor;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.account.AccountNotFoundException;
import com.subrosagames.subrosa.domain.account.Address;
import com.subrosagames.subrosa.domain.account.AddressNotFoundException;
import com.subrosagames.subrosa.domain.account.AddressValidationException;
import com.subrosagames.subrosa.domain.image.ImageNotFoundException;
import com.subrosagames.subrosa.security.SubrosaUser;
import com.subrosagames.subrosa.service.AccountService;
import com.subrosagames.subrosa.service.PaginatedList;
import com.subrosagames.subrosa.util.ObjectUtils;

/**
 * Controller for {@link Address} related CRUD operations.
 */
@RestController
public class ApiAccountAddressController {

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
    public PaginatedList<Address> listAddresses(@AuthenticationPrincipal SubrosaUser user,
                                                @PathVariable("accountId") Integer accountId,
                                                @RequestParam(value = "limit", required = false) Integer limitParam,
                                                @RequestParam(value = "offset", required = false) Integer offsetParam)
            throws NotAuthenticatedException, AccountNotFoundException
    {
        LOG.debug("{}: listing addresses for {}", user.getId(), accountId);
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
    public PaginatedList<Address> listAddressesForAuthenticatedUser(@AuthenticationPrincipal SubrosaUser user,
                                                                    @RequestParam(value = "limit", required = false) Integer limit,
                                                                    @RequestParam(value = "offset", required = false) Integer offset)
            throws NotAuthenticatedException, AccountNotFoundException
    {
        return listAddresses(user, user.getId(), limit, offset);
    }

    /**
     * Get an account address.
     *
     * @param accountId account id
     * @param addressId address id
     * @return account address
     * @throws NotAuthenticatedException if request is not authenticated
     * @throws AccountNotFoundException  if account is not found
     * @throws AddressNotFoundException  if address is not found
     */
    @RequestMapping(value = { "/account/{accountId}/address/{addressId}", "/account/{accountId}/address/{addressId}/" }, method = RequestMethod.GET)
    public Address getAddress(@AuthenticationPrincipal SubrosaUser user,
                              @PathVariable("accountId") Integer accountId,
                              @PathVariable("addressId") Integer addressId)
            throws NotAuthenticatedException, AccountNotFoundException, AddressNotFoundException
    {
        LOG.debug("{}: get address {} for {}", user.getId(), addressId, accountId);
        return accountService.getAddress(accountId, addressId);
    }

    /**
     * Get an account address.
     *
     * @param addressId address id
     * @return account address
     * @throws NotAuthenticatedException if request is not authenticated
     * @throws AccountNotFoundException  if account is not found
     * @throws AddressNotFoundException  if address is not found
     */
    @RequestMapping(value = { "/user/address/{addressId}", "/user/address/{addressId}/" }, method = RequestMethod.GET)
    public Address getAddressForAuthenticatedUser(@AuthenticationPrincipal SubrosaUser user,
                                                  @PathVariable("addressId") Integer addressId)
            throws NotAuthenticatedException, AccountNotFoundException, AddressNotFoundException
    {
        return getAddress(user, user.getId(), addressId);
    }

    /**
     * Create an account address.
     *
     * @param accountId         account id
     * @param addressDescriptor address information
     * @return created address
     * @throws NotAuthenticatedException  if request is not authenticated
     * @throws AccountNotFoundException   if account is not found
     * @throws ImageNotFoundException     if image is not found
     * @throws AddressValidationException if address is not valid for saving
     */
    @RequestMapping(value = { "/account/{accountId}/address", "/account/{accountId}/address/" }, method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN') || hasPermission(#accountId, 'Account', 'WRITE_ACCOUNT')")
    public Address createAddress(@AuthenticationPrincipal SubrosaUser user,
                                 @PathVariable("accountId") Integer accountId,
                                 @RequestBody AddressDescriptor addressDescriptor)
            throws NotAuthenticatedException, AccountNotFoundException, ImageNotFoundException, AddressValidationException
    {
        LOG.debug("{}: creating address for {}", user.getId(), accountId);
        Address address = accountService.createAddress(accountId, addressDescriptor);
        accountService.notifyOfNewUserAddress(address);
        return address;
    }

    /**
     * Create an account address.
     *
     * @param addressDescriptor address information
     * @return created address
     * @throws NotAuthenticatedException  if request is not authenticated
     * @throws AccountNotFoundException   if account is not found
     * @throws ImageNotFoundException     if image is not found
     * @throws AddressValidationException if address is not valid for saving
     */
    @RequestMapping(value = { "/user/address", "/user/address/" }, method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
//    @PreAuthorize("authenticated")
    public Address createAddressForAuthenticatedUser(@AuthenticationPrincipal SubrosaUser user,
                                                     Principal principal,
                                                     @RequestBody AddressDescriptor addressDescriptor)
            throws NotAuthenticatedException, AccountNotFoundException, ImageNotFoundException, AddressValidationException
    {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        LOG.debug("authentication is {}", securityContext.getAuthentication());
        LOG.debug("auth principal is {}", securityContext.getAuthentication().getPrincipal());
        LOG.debug("principal is {}", principal);
        LOG.debug("user is {}", user);
        return createAddress(user, user.getId(), addressDescriptor);
    }

    /**
     * Update an account address.
     *
     * @param accountId         account id
     * @param addressId         address id
     * @param addressDescriptor address information
     * @return updated address
     * @throws NotAuthenticatedException  if request is unauthenticated
     * @throws AccountNotFoundException   if account is not found
     * @throws AddressNotFoundException   if address is not found
     * @throws ImageNotFoundException     if image is not found
     * @throws AddressValidationException if address is not valid for saving
     */
    @RequestMapping(value = { "/account/{accountId}/address/{addressId}", "/account/{accountId}/address/{addressId}/" }, method = RequestMethod.PUT)
    public Address updateAddress(@AuthenticationPrincipal SubrosaUser user,
                                 @PathVariable("accountId") Integer accountId,
                                 @PathVariable("addressId") Integer addressId,
                                 @RequestBody AddressDescriptor addressDescriptor)
            throws NotAuthenticatedException, AccountNotFoundException, ImageNotFoundException, AddressNotFoundException, AddressValidationException
    {
        LOG.debug("{}: updating address {} for account {}", user.getId(), addressId, accountId);
        Address address = accountService.updateAddress(accountId, addressId, addressDescriptor);
        accountService.notifyOfNewUserAddress(address);
        return address;
    }

    /**
     * Update an account address.
     *
     * @param addressId         address id
     * @param addressDescriptor address information
     * @return updated address
     * @throws NotAuthenticatedException  if request is unauthenticated
     * @throws AccountNotFoundException   if account is not found
     * @throws AddressNotFoundException   if address is not found
     * @throws ImageNotFoundException     if image is not found
     * @throws AddressValidationException if address is not valid for saving
     */
    @RequestMapping(value = { "/user/address/{addressId}", "/user/address/{addressId}/" }, method = RequestMethod.PUT)
    public Address updateAddressForAuthenticatedUser(@AuthenticationPrincipal SubrosaUser user,
                                                     @PathVariable("addressId") Integer addressId,
                                                     @RequestBody AddressDescriptor addressDescriptor)
            throws NotAuthenticatedException, AccountNotFoundException, AddressNotFoundException, ImageNotFoundException, AddressValidationException
    {
        return updateAddress(user, user.getId(), addressId, addressDescriptor);
    }

    /**
     * Delete an account address.
     *
     * @param accountId account id
     * @param addressId address id
     * @return deleted address
     * @throws NotAuthenticatedException if request is unauthenticated
     * @throws AccountNotFoundException  if account is not found
     * @throws AddressNotFoundException  if address is not found
     */
    @RequestMapping(value = { "/account/{accountId}/address/{addressId}", "/account/{accountId}/address/{addressId}/" }, method = RequestMethod.DELETE)
    public Address deleteAddress(@AuthenticationPrincipal SubrosaUser user,
                                 @PathVariable("accountId") Integer accountId,
                                 @PathVariable("addressId") Integer addressId)
            throws NotAuthenticatedException, AddressNotFoundException, AccountNotFoundException
    {
        LOG.debug("{}: deleting address {} for account {}", user.getId(), addressId, accountId);
        return accountService.deleteAddress(accountId, addressId);
    }

    /**
     * Delete an account address.
     *
     * @param addressId address id
     * @return deleted address
     * @throws NotAuthenticatedException if request is unauthenticated
     * @throws AccountNotFoundException  if account is not found
     * @throws AddressNotFoundException  if address is not found
     */
    @RequestMapping(value = { "/user/address/{addressId}", "/user/address/{addressId}/" }, method = RequestMethod.DELETE)
    public Address deleteAddressForAuthenticatedUser(@AuthenticationPrincipal SubrosaUser user,
                                                     @PathVariable("addressId") Integer addressId)
            throws NotAuthenticatedException, AddressNotFoundException, AccountNotFoundException
    {
        return deleteAddress(user, user.getId(), addressId);
    }

    /**
     * Forces a re-processing of an account address.
     *
     * @param accountId account id
     * @param addressId address id
     * @return processed address
     * @throws AddressNotFoundException if address is not found
     * @throws AccountNotFoundException if account is not found
     */
    @RequestMapping(value = "/admin/account/{accountId}/address/{addressId}/reprocess", method = RequestMethod.POST)
    public Address reprocessAddress(@PathVariable("accountId") Integer accountId,
                                    @PathVariable("addressId") Integer addressId)
            throws AddressNotFoundException, AccountNotFoundException
    {

        return accountService.reprocessAddress(accountId, addressId);
    }

}

