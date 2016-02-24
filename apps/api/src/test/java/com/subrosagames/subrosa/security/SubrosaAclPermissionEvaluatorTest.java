package com.subrosagames.subrosa.security;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.google.common.collect.Sets;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.account.AccountRole;
import com.subrosagames.subrosa.security.permission.Permission;
import com.subrosagames.subrosa.security.permission.ReadAccountPermission;
import com.subrosagames.subrosa.security.permission.ReadGamePermission;
import com.subrosagames.subrosa.security.permission.WriteAccountPermission;
import com.subrosagames.subrosa.security.permission.WriteGamePermission;
import com.subrosagames.subrosa.test.AbstractContextTest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link SubrosaAclPermissionEvaluator}.
 */
public class SubrosaAclPermissionEvaluatorTest extends AbstractContextTest {

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
        Authentication authentication = authenticationWithAuthorities("ROLE_ADMIN");
        Map<String, Permission> permissionMap = permissionEvaluator.getPermissionMap();
        for (Permission permission : permissionMap.values()) {
            assertTrue(permission.isAllowed(authentication, target));
            assertTrue(permission.isAllowed(authentication, 3, "Thing"));
        }
    }

    @Test
    public void testHasUnknownPermissionFails() throws Exception {
        Authentication authentication = authenticationWithAuthorities("ROLE_ADMIN");
        assertFalse(permissionEvaluator.hasPermission(authentication, new Object(), "DOES_NOT_EXIST"));
    }

    private Authentication authenticationWithAuthorities(String... authorities) {
        Account account = new Account();
        account.setEmail("email");
        account.setPassword("password");
        account.setRoles(Sets.<AccountRole>newHashSet());
        return new TestingAuthenticationToken(
                account,
                "credentials",
                Arrays.stream(authorities).map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
    }

    // CHECKSTYLE-ON: JavadocMethod
}

