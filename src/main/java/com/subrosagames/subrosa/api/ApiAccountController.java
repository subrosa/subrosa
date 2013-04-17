package com.subrosagames.subrosa.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.subrosagames.subrosa.api.dto.Registration;
import com.subrosagames.subrosa.domain.account.Accolade;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.account.AccountRepository;
import com.subrosagames.subrosa.domain.account.Address;
import com.subrosagames.subrosa.security.SubrosaUser;

/**
 * Controller responsible for account related CRUD actions.
 */
@Controller
public class ApiAccountController {

    private static final Logger LOG = LoggerFactory.getLogger(ApiAccountController.class);

    @Autowired
    private AccountRepository accountRepository;

    /**
     * Get the currently logged in user's account info.
     *
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    @ResponseBody
    public Account getLoggedInUser() {
        LOG.debug("Getting account info for the currently logged in user");
        return getAuthenticatedUser();
    }

    private Account getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ((SubrosaUser) authentication.getPrincipal()).getAccount();
    }

    /**
     * Get an {@link Account} using the provided accountId.
     * @param accountId the accountId from the path.
     * @return {@link Account}
     */
    @RequestMapping(value = "/account/{accountId}", method = RequestMethod.GET)
    @ResponseBody
    public Account getAccount(@PathVariable("accountId") Integer accountId) {
        LOG.debug("Getting account {} info", accountId);
        return accountRepository.getAccount(accountId);
    }

    /**
     * Create an {@link Account} from the provided parameters.
     * @param registration the registration parameters.
     * @return {@link Account}
     */
    @RequestMapping(value = "/account/", method = RequestMethod.POST)
    @ResponseBody
    public Account createAccount(@RequestBody Registration registration) {
        LOG.debug("Creating new account");
        return accountRepository.create(registration.getAccount(), registration.getPassword());
    }

    /**
     * Update an {@link Account} from the provided parameters.
     * @param accountId the accountId from the path.
     * @param account the {@link Account} data to update.
     * @return {@link Account}
     */
    @RequestMapping(value = "/account/{accountId}", method = RequestMethod.PUT)
    @ResponseBody
    public Account updateAccount(@PathVariable("accountId") Integer accountId,
                                 @RequestBody Account account)
    {
        LOG.debug("Saving account with ID {} as {}", accountId, account);
        account.setId(accountId);
        return accountRepository.update(account);
    }

    /**
     * Get a list of {@link Accolade}s for an {@link Account} using the provided accountId.
     * @param accountId the accountId from the path.
     * @return a list of {@link Accolade}s.
     */
    @RequestMapping(value = "/account/{accountId}/accolade", method = RequestMethod.GET)
    @ResponseBody
    public List<Accolade> getAccolades(@PathVariable("accountId") Integer accountId) {
        LOG.debug("Getting accolades for account ID {}", accountId);
        Account account = accountRepository.getAccount(accountId);
        return account.getAccolades();
    }

    /**
     * Update the {@link Address} for an {@link Account}.
     * @param accountId the accountId from the path.
     * @param address the {@link Address} parameters.
     * @return {@link Account}
     */
    @RequestMapping(value = "/account/{accountId}/address", method = RequestMethod.PUT)
    @ResponseBody
    public Account updateAddress(@PathVariable("accountId") Integer accountId,
                                 @RequestBody Address address)
    {
        LOG.debug("Saving address of type {} for account ID {}", address.getAddressType(), accountId);
        return accountRepository.getAccount(accountId);
    }
}
