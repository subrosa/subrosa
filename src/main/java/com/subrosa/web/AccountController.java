package com.subrosa.web;

import com.subrosa.account.Account;
import com.subrosa.account.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AccountController {

    private static final Logger LOG = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    private AccountService accountService;

    @RequestMapping(value = "/account/{accountId}", method = RequestMethod.GET)
    @ResponseBody
    public Account getAccount(@PathVariable("accountId") String accountId) {
        LOG.debug("Getting account {} info", accountId);
        return accountService.getAccount(Integer.valueOf(accountId));
    }

    @RequestMapping(value = "/account/{accountId}", method = RequestMethod.POST)
    @ResponseBody
    public Account saveAccount(@PathVariable("accountId") String accountId) {
        return accountService.getAccount(Integer.valueOf(accountId));
    }

}
