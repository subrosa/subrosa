package com.subrosagames.subrosa.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.account.AccountRepository;

/**
 * Implementation of {@code UserDetailsService} for retrieving subrosa users.
 */
@Service
public class SubrosaUserDetailsService implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        Account account = accountRepository.getAccountByEmail(email);
        return new SubrosaUser(account);
    }

}
