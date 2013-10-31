package com.subrosagames.subrosa.domain.player;

import java.util.List;

import org.hibernate.validator.constraints.NotBlank;
import com.subrosagames.subrosa.domain.location.Location;

/**
 * Represents a participant in a game.
 */
public interface Participant {

    /**
     * Participant's name.
     * @return name
     */
    @NotBlank
    String getName();

    List<? extends Target> getTargets();

    Target getTarget(int targetId) throws TargetNotFoundException;

    void addTarget(Player target);

    void addTarget(Team target);

    void addTarget(Location target);
}
