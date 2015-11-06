package com.subrosagames.subrosa.domain.player;

import com.subrosagames.subrosa.api.dto.JoinTeamRequest;
import com.subrosagames.subrosa.api.dto.TeamDescriptor;
import com.subrosagames.subrosa.domain.DomainObject;

/**
 * Model for Teams.
 */
public interface Team extends Participant, DomainObject {

    Team update(TeamDescriptor teamDescriptor);

    Team join(Player player, JoinTeamRequest joinTeamRequest);
}
