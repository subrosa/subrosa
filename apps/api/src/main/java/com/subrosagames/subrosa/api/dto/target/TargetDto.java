package com.subrosagames.subrosa.api.dto.target;

import com.subrosagames.subrosa.domain.player.Target;
import com.subrosagames.subrosa.domain.player.TargetType;

/**
 * Data transfer object for targets.
 */
public class TargetDto {

    private int targetId;
    private TargetType targetType;

    /**
     * Construct for target.
     *
     * @param target target
     */
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
