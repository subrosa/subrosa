package com.subrosa.account;

import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.infrastructure.persistence.hibernate.JpaAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    @Autowired
    private JpaAccountRepository accountRepository;

    public Account getAccount(int accountId) {
        return accountRepository.getAccount(accountId);
    }

    public Account update(Account account) {
        return accountRepository.update(account);
    }


    public Account create(Account account) {
        return accountRepository.create(account, null);
    }
}
