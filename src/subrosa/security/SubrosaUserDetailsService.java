package com.subrosa.security;

import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.infrastructure.persistence.hibernate.JpaAccountRepository;
import com.subrosagames.subrosa.domain.account.AccountRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;
import java.util.HashSet;

public class SubrosaUserDetailsService implements UserDetailsService {

    @Autowired
    private JpaAccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = accountRepository.getAccountByEmail(email);
        return new SubrosaUserDetails(account);
    }

    private class SubrosaUserDetails implements UserDetails {

        private Account account;

        public SubrosaUserDetails(Account account) {
            this.account = account;
        }
        
        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            Collection<GrantedAuthority> authorities = new HashSet<GrantedAuthority>(2);
            for (final AccountRole role : account.getAccountRoles()) {
                authorities.add(new GrantedAuthority() {
                    @Override
                    public String getAuthority() {
                        return role.name();
                    }
                });
            }
            return authorities;
        }

        @Override
        public String getPassword() {
            return account.getPassword();
        }

        @Override
        public String getUsername() {
            return account.getUsername();
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }
    
}
