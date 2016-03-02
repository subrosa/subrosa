package com.subrosagames.subrosa.test;

import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;

import com.google.common.collect.Sets;
import com.subrosagames.subrosa.SubrosaApplication;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.account.AccountRole;
import com.subrosagames.subrosa.security.SubrosaUser;
import com.subrosagames.subrosa.test.util.ForeignKeyDisablingTestListener;

/**
 * Parent class for tests involving the loading of the application context.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringApplicationConfiguration(classes = SubrosaApplication.class)
@ComponentScan(basePackages = "com.subrosagames.subrosa.test")
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        ForeignKeyDisablingTestListener.class,
})
@ActiveProfiles("unit-test")
public abstract class AbstractContextTest {

    protected void login(int id, AccountRole... roles) {
        TestingAuthenticationToken authentication = new TestingAuthenticationToken(
                new SubrosaUser(Account.builder().id(id).email("email").password("password").roles(Sets.newHashSet(roles)).build()), "");
        authentication.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    protected void logout() {
        SecurityContextHolder.clearContext();
    }
}
