package com.subrosagames.subrosa.api.web;

import com.subrosagames.subrosa.api.AccountController;
import com.subrosagames.subrosa.api.Registration;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.security.SubrosaSignInAuthorizingRealm;
import junit.framework.Assert;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test {@link com.subrosagames.subrosa.api.AccountController}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/test-context.xml")
public class AccountControllerTest implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Autowired
    private AccountController accountController;

    @Test
    public void testRegistrationAndAuthentication() throws Exception {

        // create a new account
        Registration registration = new Registration();
        Account account = new Account();
        account.setEmail("jimmy@icanhazemail.com");
        registration.setAccount(account);
        registration.setPassword("password");
        final Account result = accountController.createAccount(registration);
        Assert.assertEquals("jimmy@icanhazemail.com", result.getEmail());

        // authenticate that account in the shiro realm
        SubrosaSignInAuthorizingRealm realm = applicationContext.getBean("subrosaRealm", SubrosaSignInAuthorizingRealm.class);
        AuthenticationToken token = new AuthenticationToken() {
            @Override public Object getPrincipal() { return result.getEmail(); }
            @Override public Object getCredentials() { return "password".toCharArray(); }
        };
        AuthenticationInfo authenticationInfo = realm.getAuthenticationInfo(token);
        Account principal = (Account) authenticationInfo.getPrincipals().getPrimaryPrincipal();
        Assert.assertEquals("jimmy@icanhazemail.com", principal.getEmail());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
