package com.subrosagames.subrosa.domain.player;

import com.subrosagames.subrosa.domain.image.Image;

/**
 * A target that is a team.
 */
public interface TargetTeam extends Target {

    /**
     * Get team image.
     *
     * @return team image
     */
    Image getImage();
}
