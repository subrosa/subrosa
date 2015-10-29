package com.subrosagames.subrosa.domain.account.repository;

import java.util.Optional;

import com.subrosagames.subrosa.domain.DomainObjectRepository;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.account.Address;

/**
 * TODO is this repo actually necessary? can we just use AccountRepository?
 */
public interface AddressRepository extends DomainObjectRepository<Address, Integer> {

    Optional<Address> findOneByAccountAndId(Account account, Integer id);
}
