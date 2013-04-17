package com.subrosagames.subrosa.api.dto.target;

import com.subrosagames.subrosa.domain.player.Target;
import com.subrosagames.subrosa.domain.player.TargetType;

/**
 *
 */
public class TargetDto {

    private int targetId;
    private TargetType targetType;

    public TargetDto(Target target) {
        this.targetId = target.getId();
        this.targetType = target.getTargetType();
    }

    public int getTargetId() {
        return targetId;
    }

    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }

    public TargetType getTargetType() {
        return targetType;
    }

    public void setTargetType(TargetType targetType) {
        this.targetType = targetType;
    }

}
