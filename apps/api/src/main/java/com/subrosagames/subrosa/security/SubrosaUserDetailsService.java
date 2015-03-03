package com.subrosagames.subrosa.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.subrosagames.subrosa.domain.account.AccountNotFoundException;
import com.subrosagames.subrosa.domain.account.AccountRepository;

/**
 * Implementation of {@code UserDetailsService} for retrieving subrosa users.
 */
@Service("userDetailsService")
public class SubrosaUserDetailsService implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        try {
            return new SubrosaUser(accountRepository.getAccountByEmail(email));
        } catch (AccountNotFoundException e) {
            throw new UsernameNotFoundException("User with email " + email + " not found", e);
        }
    }

}
