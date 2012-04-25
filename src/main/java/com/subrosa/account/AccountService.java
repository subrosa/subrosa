package com.subrosa.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    @Autowired
    private AccountDao accountDao;

    public Account getAccount(int accountId) {
        return accountDao.getAccount(accountId);
    }
}
