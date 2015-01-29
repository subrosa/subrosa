package com.subrosagames.subrosa.security;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import com.subrosagames.subrosa.SubrosaApplication;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.account.AccountRole;
import com.subrosagames.subrosa.security.permission.Permission;
import com.subrosagames.subrosa.security.permission.ReadAccountPermission;
import com.subrosagames.subrosa.security.permission.ReadGamePermission;
import com.subrosagames.subrosa.security.permission.WriteAccountPermission;
import com.subrosagames.subrosa.security.permission.WriteGamePermission;
import com.subrosagames.subrosa.test.TestConfiguration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link SubrosaAclPermissionEvaluator}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SubrosaApplication.class)
@ContextConfiguration(classes = TestConfiguration.class)
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class
})
public class SubrosaAclPermissionEvaluatorTest {

    // CHECKSTYLE-OFF: JavadocMethod

    private SubrosaAclPermissionEvaluator permissionEvaluator;

    @Before
    public void setUp() throws Exception {
        permissionEvaluator = new SubrosaAclPermissionEvaluator();
        permissionEvaluator.setPermissionMap(new HashMap<String, Permission>() {
            {
                put("READ_ACCOUNT", new ReadAccountPermission());
                put("WRITE_ACCOUNT", new WriteAccountPermission());
                put("READ_GAME", new ReadGamePermission());
                put("WRITE_GAME", new WriteGamePermission());
            }
        });
    }

    @Test
    public void testAdminHasPermission() throws Exception {
        Object target = new Account();
        Authentication authentication = authenticationWithAuthorities("ADMIN");
        Map<String, Permission> permissionMap = permissionEvaluator.getPermissionMap();
        for (Permission permission : permissionMap.values()) {
            assertTrue(permission.isAllowed(authentication, target));
            assertTrue(permission.isAllowed(authentication, 3, "Thing"));
        }
    }

    @Test
    public void testHasUnknownPermissionFails() throws Exception {
        Authentication authentication = authenticationWithAuthorities("ADMIN");
        assertFalse(permissionEvaluator.hasPermission(authentication, new Object(), "DOES_NOT_EXIST"));
    }

    private PreAuthenticatedAuthenticationToken authenticationWithAuthorities(String... authorities) {
        Account account = new Account();
        account.setEmail("email");
        account.setPassword("password");
        account.setRoles(Sets.<AccountRole>newHashSet());
        return new PreAuthenticatedAuthenticationToken(
                new SubrosaUser(account),
                "credentials",
                Collections2.transform(Sets.newHashSet(authorities), new Function<String, GrantedAuthority>() {
                    @Override
                    public GrantedAuthority apply(@Nullable String s) {
                        return new SimpleGrantedAuthority(s);
                    }
                }));
    }

    // CHECKSTYLE-ON: JavadocMethod
}

