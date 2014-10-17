package com.subrosagames.subrosa.domain.file;

/**
 * CRUD operations for file assets.
 */
public interface FileAssetRepository {

    /**
     * Create a file asset.
     *
     * @param fileAsset file asset
     * @return created file asset
     */
    FileAsset create(FileAsset fileAsset);

    /**
     * Update a file asset.
     *
     * @param fileAsset file asset
     * @return updated file asset
     */
    FileAsset update(FileAsset fileAsset);
}
