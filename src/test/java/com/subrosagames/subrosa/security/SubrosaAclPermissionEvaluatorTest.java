package com.subrosagames.subrosa.security;

import javax.annotation.Nullable;

import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.account.AccountRole;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import org.junit.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import static org.junit.Assert.assertTrue;

/**
 * Tests {@link SubrosaAclPermissionEvaluator}.
 */
public class SubrosaAclPermissionEvaluatorTest {

    // CHECKSTYLE-OFF: JavadocMethod

    private SubrosaAclPermissionEvaluator permissionEvaluator = new SubrosaAclPermissionEvaluator();

    @Test
    public void testAdminHasPermission() throws Exception {
        Authentication authentication = authenticationWithAuthorities("ADMIN");
        Object target = new Account();
        for (SubrosaAclPermissionEvaluator.SubrosaPermission permission : SubrosaAclPermissionEvaluator.SubrosaPermission.values()) {
            assertTrue(permissionEvaluator.hasPermission(authentication, target, permission));
        }
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

