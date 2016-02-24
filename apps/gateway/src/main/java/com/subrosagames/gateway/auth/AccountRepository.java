package com.subrosagames.gateway.auth;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

/**
 * TODO
 */
public interface AccountRepository extends CrudRepository<Account, Integer> {
    Optional<Account> findOneByEmail(String email);
    Optional<Account> findOneByPhone(String phone);
}
