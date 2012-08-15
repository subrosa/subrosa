package com.subrosagames.subrosa.api;

import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.account.AccountFactory;
import com.subrosagames.subrosa.domain.account.AccountRepository;
import com.subrosagames.subrosa.domain.account.Address;
import com.subrosagames.subrosa.domain.account.Accolade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class AccountController {

    private static final Logger LOG = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    private AccountFactory accountFactory;

    @Autowired
    private AccountRepository accountRepository;

    @RequestMapping(value = "/account/{accountId}", method = RequestMethod.GET)
    @ResponseBody
    public Account getAccount(@PathVariable("accountId") Integer accountId) {
        LOG.debug("Getting account {} info", accountId);
        return accountRepository.getAccount(accountId);
    }

    @RequestMapping(value = "/account", method = RequestMethod.POST)
    @ResponseBody
    public Account createAccount(@RequestBody Registration registration) {
        LOG.debug("Creating new account");
        return accountRepository.create(registration.getAccount(), registration.getPassword());
    }

    @RequestMapping(value = "/account/{accountId}", method = RequestMethod.PUT)
    @ResponseBody
    public Account updateAccount(@PathVariable("accountId") Integer accountId,
                                 @RequestBody Account account)
    {
        LOG.debug("Saving account with ID {} as {}", accountId, account);
        account.setId(accountId);
        return accountRepository.update(account);
    }

    public List<Accolade> getAccolades(@PathVariable("accountId") Integer accountId) {
        LOG.debug("Getting accolades for account ID {}", accountId);
        Account account = accountRepository.getAccount(accountId);
        return account.getAccolades();
    }

    @RequestMapping(value = "/account/{accountId}/address", method = RequestMethod.PUT)
    @ResponseBody
    public Account updateAddress(@PathVariable("accountId") Integer accountId,
                                 @RequestBody Address address)
    {
        LOG.debug("Saving address of type {} for account ID {}", address.getAddressType(), accountId);
        return accountRepository.getAccount(accountId);
    }
}
