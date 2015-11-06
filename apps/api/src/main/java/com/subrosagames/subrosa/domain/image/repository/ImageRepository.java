package com.subrosagames.subrosa.domain.image.repository;

import java.util.Optional;

import com.subrosagames.subrosa.domain.DomainObjectRepository;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.image.Image;

/**
 * TODO
 */
public interface ImageRepository extends DomainObjectRepository<Image, Integer> {

    Optional<Image> findOneByAccountAndId(Account account, int imageId);
}
