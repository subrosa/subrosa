package com.subrosagames.subrosa.api.web;

import java.util.List;

import org.apache.commons.lang.StringUtils;
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

import com.google.common.base.Optional;
import com.subrosagames.subrosa.api.AccountActivation;
import com.subrosagames.subrosa.api.BadRequestException;
import com.subrosagames.subrosa.api.NotAuthenticatedException;
import com.subrosagames.subrosa.api.dto.AccountDescriptor;
import com.subrosagames.subrosa.api.dto.RegistrationRequest;
import com.subrosagames.subrosa.domain.account.Accolade;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.account.AccountFactory;
import com.subrosagames.subrosa.domain.account.AccountNotFoundException;
import com.subrosagames.subrosa.domain.account.AccountValidationException;
import com.subrosagames.subrosa.domain.account.Address;
import com.subrosagames.subrosa.domain.account.EmailConflictException;
import com.subrosagames.subrosa.domain.token.TokenInvalidException;
import com.subrosagames.subrosa.service.AccountService;
import com.subrosagames.subrosa.service.PaginatedList;
import com.subrosagames.subrosa.util.ObjectUtils;

/**
 * Controller responsible for account related CRUD actions.
 */
@Controller
public class ApiAccountController extends BaseApiController {

    private static final Logger LOG = LoggerFactory.getLogger(ApiAccountController.class);

    @Autowired
    private AccountFactory accountFactory;

    @Autowired
    private AccountService accountService;

    /**
     * Get paginated list of accounts.
     *
     * @param limitParam  limitParam
     * @param offsetParam offsetParam
     * @param expand      fields to expand
     * @return paginated list of accounts
     * @throws NotAuthenticatedException if request is unauthenticated
     */
    @RequestMapping(value = { "/account", "/account/" }, method = RequestMethod.GET)
    @ResponseBody
    public PaginatedList<Account> listAccounts(@RequestParam(value = "limitParam", required = false) Integer limitParam,
                                               @RequestParam(value = "offsetParam", required = false) Integer offsetParam,
                                               @RequestParam(value = "expand", required = false) String expand)
            throws NotAuthenticatedException
    {
        LOG.debug("{}: Getting account list with limitParam {} and offsetParam {}.", getAuthenticatedUser().getId(), limitParam, offsetParam);
        int limit = ObjectUtils.defaultIfNull(limitParam, 10);
        int offset = ObjectUtils.defaultIfNull(offsetParam, 0);
        String[] expansions = StringUtils.isEmpty(expand) ? new String[0] : expand.split(",");
        return accountFactory.getAccounts(limit, offset, expansions);
    }

    /**
     * Get an {@link Account} using the provided accountId.
     *
     * @param accountId   the accountId from the path.
     * @param expandParam fields to expand
     * @return {@link Account}
     * @throws AccountNotFoundException  if account not found
     * @throws NotAuthenticatedException if request is unauthenticated
     */
    @RequestMapping(value = { "/account/{accountId}", "/account/{accountId}/" }, method = RequestMethod.GET)
    @ResponseBody
    public Account getAccount(@PathVariable("accountId") Integer accountId,
                              @RequestParam(value = "expand", required = false) String expandParam)
            throws AccountNotFoundException, NotAuthenticatedException
    {
        String expand = ObjectUtils.defaultIfNull(expandParam, "");
        LOG.debug("{}: Getting account {} info with expansions {}", getAuthenticatedUser().getId(), accountId, expand);
        String[] expansions = StringUtils.isEmpty(expand) ? new String[0] : expand.split(",");
        return accountService.getAccount(accountId, expansions);
    }

    /**
     * Get an {@link Account} using the provided accountId.
     *
     * @param expand fields to expand
     * @return {@link Account}
     * @throws AccountNotFoundException  if account not found
     * @throws NotAuthenticatedException if request is unauthenticated
     */
    @RequestMapping(value = { "/user", "/user/" }, method = RequestMethod.GET)
    @ResponseBody
    public Account getAccountForUser(@RequestParam(value = "expand", required = false) String expand)
            throws AccountNotFoundException, NotAuthenticatedException
    {
        return getAccount(getAuthenticatedUser().getId(), expand);
    }

    /**
     * Create an {@link Account} from the provided parameters.
     *
     * @param registrationRequest the registration parameters.
     * @return {@link Account}
     * @throws BadRequestException        if no POST body is supplied
     * @throws AccountValidationException if account data is not valid
     * @throws EmailConflictException     if supplied email is already in use
     * @throws NotAuthenticatedException  if request is unauthenticated
     */
    @RequestMapping(value = { "/account", "/account/" }, method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public Account createAccount(@RequestBody(required = false) RegistrationRequest registrationRequest)
            throws AccountValidationException, BadRequestException, EmailConflictException, NotAuthenticatedException
    {
        LOG.debug("Creating new account");
        if (registrationRequest == null) {
            throw new BadRequestException("No POST body supplied");
        }
        if (registrationRequest.getAccount() == null) {
            throw new BadRequestException("Missing account information creating account");
        }
        // TODO put some constraints on password
        if (StringUtils.isEmpty(registrationRequest.getPassword())) {
            throw new BadRequestException("Missing password creating account");
        }
        AccountDescriptor accountDescriptor = registrationRequest.getAccount();
        accountDescriptor.setActivated(Optional.of(false));
        Account account = accountFactory.forDto(accountDescriptor);
        return account.create(registrationRequest.getPassword());
    }

    /**
     * Update an {@link Account} from the provided parameters.
     *
     * @param accountId         the accountId from the path.
     * @param accountDescriptor the {@link AccountDescriptor} data to update.
     * @return {@link Account}
     * @throws AccountNotFoundException   if account not found
     * @throws AccountValidationException if account data is not valid
     * @throws NotAuthenticatedException  if request is unauthenticated
     */
    @RequestMapping(value = { "/account/{accountId}", "/account/{accountId}/" }, method = RequestMethod.PUT)
    @ResponseBody
    public Account updateAccount(@PathVariable("accountId") Integer accountId,
                                 @RequestBody AccountDescriptor accountDescriptor)
            throws AccountNotFoundException, AccountValidationException, NotAuthenticatedException
    {
        LOG.debug("{}: Saving account with ID {} as {}", getAuthenticatedUser().getId(), accountId, accountDescriptor);
        Account account = accountFactory.getAccount(accountId);
        return account.update(accountDescriptor);
    }

    /**
     * Update an {@link Account} from the provided parameters.
     *
     * @param accountDescriptor the {@link AccountDescriptor} data to update.
     * @return {@link Account}
     * @throws AccountNotFoundException   if account not found
     * @throws AccountValidationException if account data is not valid
     * @throws NotAuthenticatedException  if request is unauthenticated
     */
    @RequestMapping(value = { "/user", "/user/" }, method = RequestMethod.PUT)
    @ResponseBody
    public Account updateAccountForUser(@RequestBody AccountDescriptor accountDescriptor)
            throws AccountNotFoundException, AccountValidationException, NotAuthenticatedException
    {
        return updateAccount(getAuthenticatedUser().getId(), accountDescriptor);
    }

    /**
     * Activate the specified account with the given activation details.
     *
     * @param accountId         account id
     * @param accountActivation activation details
     * @return activated account
     * @throws AccountNotFoundException   if account does not exist
     * @throws BadRequestException        if bad activation details are supplied
     * @throws TokenInvalidException      if activation token is not valid
     * @throws AccountValidationException if account is invalid for activation
     */
    @RequestMapping(value = { "/account/{accountId}/activate", "/account/{accountId}/activate/" }, method = RequestMethod.POST)
    @ResponseBody
    public Account activateAccount(@PathVariable("accountId") Integer accountId,
                                   @RequestBody(required = false) AccountActivation accountActivation)
            throws AccountNotFoundException, BadRequestException, TokenInvalidException, AccountValidationException
    {
        LOG.debug("Activating account with ID {} using token {}", accountId, accountActivation);
        if (accountActivation == null) {
            throw new BadRequestException("No POST body supplied");
        }
        if (accountActivation.getToken() == null) {
            throw new BadRequestException("No activation token supplied");
        }
        Account account = accountFactory.getAccount(accountId);
        account.activate(accountActivation.getToken());
        return account;
    }

    /**
     * Get a list of {@link Accolade}s for an {@link Account} using the provided accountId.
     *
     * @param accountId the accountId from the path.
     * @return a list of {@link Accolade}s.
     * @throws AccountNotFoundException if account not found
     */
    @RequestMapping(value = { "/account/{accountId}/accolade", "/account/{accountId}/accolade/" }, method = RequestMethod.GET)
    @ResponseBody
    public List<Accolade> getAccolades(@PathVariable("accountId") Integer accountId)
            throws AccountNotFoundException
    {
        LOG.debug("Getting accolades for account ID {}", accountId);
        Account account = accountFactory.getAccount(accountId);
        return account.getAccolades();
    }

}
