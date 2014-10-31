package com.subrosagames.subrosa.security;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.account.AccountNotFoundException;
import com.subrosagames.subrosa.domain.account.AccountRepository;
import com.subrosagames.subrosa.domain.account.AccountValidationException;

/**
 * Listener that updates {@link Account#lastLoggedIn} on successful authentication.
 */
@Component
public class UpdateLastLoggedInListener implements ApplicationListener<AuthenticationSuccessEvent> {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        Account account = ((SubrosaUser) event.getAuthentication().getPrincipal()).getAccount();
        account.setLastLoggedIn(DateTime.now().toDate());
        try {
            accountRepository.update(account);
        } catch (AccountNotFoundException | AccountValidationException e) {
            throw new IllegalStateException(e);
        }
    }
}
