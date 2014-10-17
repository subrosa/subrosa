package com.subrosagames.subrosa.infrastructure.persistence.hibernate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.subrosagames.subrosa.domain.file.FileAsset;
import com.subrosagames.subrosa.domain.file.FileAssetRepository;

/**
 * Handles persistence for file assets.
 */
@Repository
public class JpaFileAssetRepository implements FileAssetRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public FileAsset create(FileAsset fileAsset) {
        entityManager.persist(fileAsset);
        return fileAsset;
    }

    @Override
    public FileAsset update(FileAsset fileAsset) {
        return entityManager.merge(fileAsset);
    }
}
