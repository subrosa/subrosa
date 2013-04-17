package com.subrosagames.subrosa.api.dto.target;

import com.subrosagames.subrosa.domain.player.TargetTeam;
import com.subrosagames.subrosa.domain.image.Image;

/**
 *
 */
public class TargetTeamDto extends TargetDto {

    private Image image;

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
