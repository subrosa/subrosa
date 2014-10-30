package com.subrosagames.subrosa.domain.player;

import com.subrosagames.subrosa.domain.account.Address;
import com.subrosagames.subrosa.domain.image.Image;

/**
 * A target that is a player.
 */
public interface TargetPlayer extends Target {

    /**
     * Get target ID photo.
     *
     * @return ID photo
     */
    Image getPhotoId();

    /**
     * Get target action photo.
     *
     * @return action photo
     */
    Image getActionPhoto();

    /**
     * Get target avatar.
     *
     * @return target avatar
     */
    Image getAvatar();

    /**
     * Get target home address.
     *
     * @return home address
     */
    Address getHomeAddress();

    /**
     * Get target work address.
     *
     * @return work address
     */
    Address getWorkAddress();
}
