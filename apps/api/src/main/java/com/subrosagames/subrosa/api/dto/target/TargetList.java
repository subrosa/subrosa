package com.subrosagames.subrosa.api.dto.target;

import java.util.List;

/**
 * List of targets.
 */
public class TargetList {

    private List<? extends TargetDto> targets;

    /**
     * Construct with targets.
     *
     * @param targets targets
     */
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
