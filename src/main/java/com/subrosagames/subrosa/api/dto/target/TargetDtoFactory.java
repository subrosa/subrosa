package com.subrosagames.subrosa.api.dto.target;

import com.subrosagames.subrosa.domain.player.Target;
import com.subrosagames.subrosa.domain.player.TargetPlayer;
import com.subrosagames.subrosa.domain.player.TargetTeam;

/**
 *
 */
public final class TargetDtoFactory {

    private TargetDtoFactory() { }

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
