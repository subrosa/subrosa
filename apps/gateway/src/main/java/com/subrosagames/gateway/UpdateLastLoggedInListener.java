package com.subrosagames.gateway;

import java.time.Clock;
import java.time.Instant;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import com.subrosagames.gateway.auth.Account;
import com.subrosagames.gateway.auth.AccountRepository;
import com.subrosagames.subrosa.security.SubrosaUser;
import lombok.Setter;

/**
 * Listener that updates {@link Account#lastLoggedIn} on successful authentication.
 */
@Component
public class UpdateLastLoggedInListener implements ApplicationListener<AuthenticationSuccessEvent> {

    @Autowired
    private AccountRepository accountRepository;
    @Setter
    private Clock clock = Clock.systemUTC();

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        Object principal = event.getAuthentication().getPrincipal();
        if (principal instanceof SubrosaUser) {
            Account account = accountRepository.findOne(((SubrosaUser) principal).getId());
            account.setLastLoggedIn(Date.from(Instant.now(clock)));
            accountRepository.save(account);
        }
    }
}
