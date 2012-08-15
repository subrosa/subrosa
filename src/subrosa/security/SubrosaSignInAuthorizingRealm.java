package com.subrosa.security;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.account.AccountRepository;
import com.subrosagames.subrosa.domain.account.AccountRole;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: jgore
 * Date: 4/21/12
 * Time: 12:58 午後
 * To change this template use File | Settings | File Templates.
 */
public class SubrosaSignInAuthorizingRealm extends AuthorizingRealm {

    private static final Logger LOG = LoggerFactory.getLogger(SubrosaSignInAuthorizingRealm.class);

    @Autowired
    private AccountRepository accountRepository;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        Account account = (Account) principals.getPrimaryPrincipal();
        LOG.debug("Getting authorization info for {}", account.getEmail());
        Collection<String> roles = Collections2.transform(account.getAccountRoles(), new Function<AccountRole, String>() {
            @Override
            public String apply(AccountRole accountRole) {
                return accountRole.name();
            }
        });
        return new SimpleAuthorizationInfo(Sets.newHashSet(roles));
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String email = String.valueOf(token.getPrincipal());
        Account account = accountRepository.getAccountByEmail(email);
        LOG.debug("Authenticating account (id {}) with email {}", account.getId(), account.getEmail());
        return new SimpleAuthenticationInfo(new SimplePrincipalCollection(account, "subrosa"), account.getPassword());
    }
}
