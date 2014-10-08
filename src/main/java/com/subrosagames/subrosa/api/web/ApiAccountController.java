package com.subrosagames.subrosa.api.web;

import java.util.List;

import javax.transaction.Transactional;

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
import com.subrosagames.subrosa.api.BadRequestException;
import com.subrosagames.subrosa.api.dto.AccountDescriptor;
import com.subrosagames.subrosa.api.dto.Registration;
import com.subrosagames.subrosa.domain.account.Accolade;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.account.AccountFactory;
import com.subrosagames.subrosa.domain.account.AccountNotFoundException;
import com.subrosagames.subrosa.domain.account.AccountRepository;
import com.subrosagames.subrosa.domain.account.AccountValidationException;
import com.subrosagames.subrosa.domain.account.Address;
import com.subrosagames.subrosa.domain.account.EmailConflictException;
import com.subrosagames.subrosa.service.PaginatedList;
import com.subrosagames.subrosa.util.ObjectUtils;

/**
 * Controller responsible for account related CRUD actions.
 */
@Controller
@RequestMapping("/account")
public class ApiAccountController {

    private static final Logger LOG = LoggerFactory.getLogger(ApiAccountController.class);

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountFactory accountFactory;


    /**
     * Get paginated list of accounts.
     *
     * @param limit  limit
     * @param offset offset
     * @param expand fields to expand
     * @return paginated list of accounts
     */
    @RequestMapping(value = { "", "/" }, method = RequestMethod.GET)
    @ResponseBody
    public PaginatedList<Account> listAccounts(@RequestParam(value = "limit", required = false) Integer limit,
                                               @RequestParam(value = "offset", required = false) Integer offset,
                                               @RequestParam(value = "expand", required = false) String expand)
    {
        LOG.debug("Getting game list with limit {} and offset {}.", limit, offset);
        limit = ObjectUtils.defaultIfNull(limit, 10);
        offset = ObjectUtils.defaultIfNull(offset, 0);
        if (StringUtils.isEmpty(expand)) {
            return accountFactory.getAccounts(limit, offset);
        } else {
            return accountFactory.getAccounts(limit, offset, expand.split(","));
        }
    }

    /**
     * Get an {@link Account} using the provided accountId.
     *
     * @param accountId the accountId from the path.
     * @param expand    fields to expand
     * @return {@link Account}
     * @throws AccountNotFoundException if account not found
     */
    @RequestMapping(value = { "/{accountId}", "/{accountId}/" }, method = RequestMethod.GET)
    @ResponseBody
    public Account getAccount(@PathVariable("accountId") Integer accountId,
                              @RequestParam(value = "expand", required = false) String expand)
            throws AccountNotFoundException
    {
        LOG.debug("Getting account {} info with expansions {}", accountId, expand);
        if (StringUtils.isEmpty(expand)) {
            return accountFactory.getAccount(accountId);
        } else {
            return accountFactory.getAccount(accountId, expand.split(","));
        }
    }

    /**
     * Create an {@link Account} from the provided parameters.
     *
     * @param registration the registration parameters.
     * @return {@link Account}
     * @throws BadRequestException        if no POST body is supplied
     * @throws AccountValidationException if account data is not valid
     * @throws EmailConflictException     if supplied email is already in use
     */
    @RequestMapping(value = { "", "/" }, method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public Account createAccount(@RequestBody(required = false) Registration registration)
            throws AccountValidationException, BadRequestException, EmailConflictException
    {
        LOG.debug("Creating new account");
        if (registration == null) {
            throw new BadRequestException("No POST body supplied");
        }
        if (registration.getAccount() == null) {
            throw new BadRequestException("Missing account information creating account");
        }
        // TODO put some constraints on password
        if (StringUtils.isEmpty(registration.getPassword())) {
            throw new BadRequestException("Missing password creating account");
        }
        AccountDescriptor accountDescriptor = registration.getAccount();
        accountDescriptor.setActivated(Optional.of(false));
        Account account = accountFactory.forDto(accountDescriptor);
        return account.create(registration.getPassword());
    }

    /**
     * Update an {@link Account} from the provided parameters.
     *
     * @param accountId         the accountId from the path.
     * @param accountDescriptor the {@link AccountDescriptor} data to update.
     * @return {@link Account}
     * @throws AccountNotFoundException   if account not found
     * @throws AccountValidationException if account data is not valid
     */
    @RequestMapping(value = { "/{accountId}", "/{accountId}/" }, method = RequestMethod.PUT)
    @ResponseBody
    @Transactional
    public Account updateAccount(@PathVariable("accountId") Integer accountId,
                                 @RequestBody AccountDescriptor accountDescriptor)
            throws AccountNotFoundException, AccountValidationException
    {
        LOG.debug("Saving account with ID {} as {}", accountId, accountDescriptor);
        Account account = accountFactory.getAccount(accountId);
        // read-only fields
        // TODO Account should handle this
        accountDescriptor.setId(accountId);
        accountDescriptor.setActivated(Optional.of(false));
        return account.update(accountDescriptor);
    }

    /**
     * Get a list of {@link Accolade}s for an {@link Account} using the provided accountId.
     *
     * @param accountId the accountId from the path.
     * @return a list of {@link Accolade}s.
     * @throws AccountNotFoundException if account not found
     */
    @RequestMapping(value = { "/{accountId}/accolade", "/{accountId}/accolade/" }, method = RequestMethod.GET)
    @ResponseBody
    public List<Accolade> getAccolades(@PathVariable("accountId") Integer accountId)
            throws AccountNotFoundException
    {
        LOG.debug("Getting accolades for account ID {}", accountId);
        Account account = accountRepository.get(accountId);
        return account.getAccolades();
    }

    /**
     * Update the {@link Address} for an {@link Account}.
     *
     * @param accountId the accountId from the path.
     * @param address   the {@link Address} parameters.
     * @return {@link Account}
     * @throws AccountNotFoundException if account not found
     */
    @RequestMapping(value = { "/{accountId}/address", "/{accountId}/address/" }, method = RequestMethod.PUT)
    @ResponseBody
    public Account updateAddress(@PathVariable("accountId") Integer accountId,
                                 @RequestBody Address address)
            throws AccountNotFoundException
    {
        LOG.debug("Saving address of type {} for account ID {}", address.getAddressType(), accountId);
        return accountRepository.get(accountId);
    }

}
