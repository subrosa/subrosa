package com.subrosagames.subrosa.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            return new SubrosaUser(accountRepository.getAccountByEmail(email));
        } catch (com.subrosagames.subrosa.domain.account.AccountNotFoundException e) {
            throw new UsernameNotFoundException("User with email " + email + " not found", e);
        }
    }

}
