package com.subrosagames.subrosa.domain.player;

import java.util.List;

import org.hibernate.validator.constraints.NotBlank;

import com.subrosagames.subrosa.domain.image.Image;
import com.subrosagames.subrosa.domain.location.Location;

/**
 * Represents a participant in a game.
 */
public interface Participant {

    Integer getId();

    /**
     * Participant's name.
     *
     * @return name
     */
    @NotBlank
    String getName();

    /**
     * Get participant's targets.
     *
     * @return targets
     */
    List<? extends Target> getTargets();

    /**
     * Get specified target.
     *
     * @param targetId target id
     * @return target
     * @throws TargetNotFoundException if target does not exist
     */
    Target getTarget(int targetId) throws TargetNotFoundException;

    /**
     * Add a player target for the participant.
     *
     * @param target targeted player
     */
    void addTarget(Player target);

    /**
     * Add a team target for the participant.
     *
     * @param target targeted team
     */
    void addTarget(Team target);

    /**
     * Add a location target for the participant.
     *
     * @param target targeted location
     */
    void addTarget(Location target);

    /**
     * Get an image for the participant.
     *
     * @return partipiant image
     */
    Image getAvatar();
}
