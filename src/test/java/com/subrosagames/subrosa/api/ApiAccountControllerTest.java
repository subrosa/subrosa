package com.subrosagames.subrosa.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.subrosagames.subrosa.api.dto.Registration;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.security.SubrosaUser;
import junit.framework.Assert;

/**
 * Test {@link ApiAccountController}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/test-context.xml")
public class ApiAccountControllerTest implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Autowired
    private ApiAccountController apiAccountController;

    /**
     * Test registration and authentication of an account.
     * @throws Exception everything.
     */
    @Test
    public void testRegistrationAndAuthentication() throws Exception {

        // create a new account
        Registration registration = new Registration();
        Account account = new Account();
        account.setEmail("jimmy@icanhazemail.com");
        registration.setAccount(account);
        registration.setPassword("password");
        final Account result = apiAccountController.createAccount(registration);
        Assert.assertEquals("jimmy@icanhazemail.com", result.getEmail());

        AuthenticationManager authenticationManager = applicationContext.getBean("org.springframework.security.authenticationManager",
                AuthenticationManager.class);
        Authentication credentials = new UsernamePasswordAuthenticationToken("jimmy@icanhazemail.com", "password");
        Authentication authentication = authenticationManager.authenticate(credentials);
        SubrosaUser principal = (SubrosaUser) authentication.getPrincipal();
        Assert.assertEquals("jimmy@icanhazemail.com", principal.getAccount().getEmail());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
