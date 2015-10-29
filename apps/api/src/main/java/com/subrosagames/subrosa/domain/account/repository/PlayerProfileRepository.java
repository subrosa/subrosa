package com.subrosagames.subrosa.domain.account.repository;

import java.util.Optional;

import com.subrosagames.subrosa.domain.DomainObjectRepository;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.account.PlayerProfile;

/**
 * TODO is this repo actually necessary? can we just use AccountRepository?
 */
public interface PlayerProfileRepository extends DomainObjectRepository<PlayerProfile, Integer> {

    Optional<PlayerProfile> findOneByAccountAndId(Account account, int playerId);
}
