package com.subrosagames.gateway.auth;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.subrosagames.subrosa.security.SubrosaUser;

/**
 * Implementation of {@code UserDetailsService} for retrieving subrosa users.
 */
@Service
public class SubrosaUserDetailsService implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String emailOrPhone) {
        Optional<Account> byEmail = accountRepository.findOneByEmail(emailOrPhone);
        if (byEmail.isPresent()) {
            return new SubrosaUser(byEmail.get());
        }
        Optional<Account> byPhone = accountRepository.findOneByPhone(emailOrPhone);
        if (byPhone.isPresent()) {
            return new SubrosaUser(byPhone.get());
        }
        throw new UsernameNotFoundException("User with identifier " + emailOrPhone + " not found");
    }

}
