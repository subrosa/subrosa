package com.subrosagames.subrosa.api.web;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.subrosagames.subrosa.api.NotAuthenticatedException;
import com.subrosagames.subrosa.api.dto.AccountDescriptor;
import com.subrosagames.subrosa.domain.account.Accolade;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.account.AccountFactory;
import com.subrosagames.subrosa.domain.account.AccountNotFoundException;
import com.subrosagames.subrosa.domain.account.AccountValidationException;
import com.subrosagames.subrosa.security.SubrosaUser;
import com.subrosagames.subrosa.service.AccountService;
import com.subrosagames.subrosa.service.PaginatedList;
import com.subrosagames.subrosa.util.ObjectUtils;

/**
 * Controller responsible for account related CRUD actions.
 */
@Controller
public class ApiAccountController {

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
    public PaginatedList<Account> listAccounts(@AuthenticationPrincipal SubrosaUser user,
                                               @RequestParam(value = "limitParam", required = false) Integer limitParam,
                                               @RequestParam(value = "offsetParam", required = false) Integer offsetParam,
                                               @RequestParam(value = "expand", required = false) String expand)
            throws NotAuthenticatedException
    {
        LOG.debug("{}: Getting account list with limitParam {} and offsetParam {}.", user.getId(), limitParam, offsetParam);
        LOG.debug("Has roles: {}", String.join(",", user.getAuthorities().stream().map(GrantedAuthority::toString).collect(Collectors.toList())));
        int limit = ObjectUtils.defaultIfNull(limitParam, 10);
        int offset = ObjectUtils.defaultIfNull(offsetParam, 0);
        String[] expansions = StringUtils.isEmpty(expand) ? new String[0] : expand.split(",");
        Page<Account> page = accountService.listAccounts(limit, offset, expansions);
        return new PaginatedList<>(page.getContent(), page.getTotalElements(), page.getSize(), page.getNumber() * page.getSize());
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
    public Account getAccount(@AuthenticationPrincipal SubrosaUser user,
                              @PathVariable("accountId") Integer accountId,
                              @RequestParam(value = "expand", required = false) String expandParam)
            throws AccountNotFoundException, NotAuthenticatedException
    {
        if (user == null) {
            throw new NotAuthenticatedException("Unauthenticated attempt to get account.");
        }
        String expand = ObjectUtils.defaultIfNull(expandParam, "");
        LOG.debug("{}: Getting account {} info with expansions {}", user.getId(), accountId, expand);
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
    public Account getAccountForUser(@AuthenticationPrincipal SubrosaUser user,
                                     @RequestParam(value = "expand", required = false) String expand)
            throws AccountNotFoundException, NotAuthenticatedException
    {
        return getAccount(user, user.getId(), expand);
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
    public Account updateAccount(@AuthenticationPrincipal SubrosaUser user,
                                 @PathVariable("accountId") Integer accountId,
                                 @RequestBody AccountDescriptor accountDescriptor)
            throws AccountNotFoundException, AccountValidationException, NotAuthenticatedException
    {
        LOG.debug("{}: Saving account with ID {} as {}", user.getId(), accountId, accountDescriptor);
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
    public Account updateAccountForUser(@AuthenticationPrincipal SubrosaUser user,
                                        @RequestBody AccountDescriptor accountDescriptor)
            throws AccountNotFoundException, AccountValidationException, NotAuthenticatedException
    {
        return updateAccount(user, user.getId(), accountDescriptor);
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
