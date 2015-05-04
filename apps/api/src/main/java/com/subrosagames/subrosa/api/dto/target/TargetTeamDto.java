package com.subrosagames.subrosa.api.dto.target;

import com.subrosagames.subrosa.domain.image.Image;
import com.subrosagames.subrosa.domain.player.TargetTeam;

/**
 * Target team DTO.
 */
public class TargetTeamDto extends TargetDto {

    private Image image;

    /**
     * Construct with given target team.
     *
     * @param target target team
     */
    public TargetTeamDto(TargetTeam target) {
        super(target);
        image = target.getImage();
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }
}
