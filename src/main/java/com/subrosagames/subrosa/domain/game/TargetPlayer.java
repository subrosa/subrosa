package com.subrosagames.subrosa.domain.game;

import com.subrosagames.subrosa.domain.account.Address;
import com.subrosagames.subrosa.domain.image.Image;

/**
 *
 */
public interface TargetPlayer extends Target {

    Image getPhotoId();
    Image getActionPhoto();
    Image getAvatar();

    Address getHomeAddress();
    Address getWorkAddress();
}
