package com.subrosagames.subrosa.api.dto.target;

import com.subrosagames.subrosa.domain.player.Target;
import com.subrosagames.subrosa.domain.player.TargetPlayer;
import com.subrosagames.subrosa.domain.player.TargetTeam;

/**
 * Factory for Target DTOs.
 */
public final class TargetDtoFactory {

    private TargetDtoFactory() { }

    /**
     * Get DTO for given target.
     * @param target target
     * @return target DTO
     */
    public static TargetDto getDtoForTarget(Target target) {
        if (target instanceof TargetPlayer) {
            return new TargetPlayerDto((TargetPlayer) target);
        } else if (target instanceof TargetTeam) {
            return new TargetTeamDto((TargetTeam) target);
        } else {
            return new TargetDto(target);
        }
    }
}
