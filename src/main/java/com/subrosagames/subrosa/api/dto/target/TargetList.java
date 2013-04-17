package com.subrosagames.subrosa.api.dto.target;

import java.util.List;

/**
 *
 */
public class TargetList {

    private List<? extends TargetDto> targets;

    public TargetList(List<TargetDto> targets) {
        this.targets = targets;
    }

    public List<? extends TargetDto> getTargets() {
        return targets;
    }

    public void setTargets(List<? extends TargetDto> targets) {
        this.targets = targets;
    }
}
