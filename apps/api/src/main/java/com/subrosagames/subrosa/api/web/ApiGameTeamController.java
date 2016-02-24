package com.subrosagames.subrosa.api.web;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.subrosagames.subrosa.api.NotAuthenticatedException;
import com.subrosagames.subrosa.api.dto.JoinTeamRequest;
import com.subrosagames.subrosa.api.dto.TeamDescriptor;
import com.subrosagames.subrosa.domain.game.GameNotFoundException;
import com.subrosagames.subrosa.domain.player.Player;
import com.subrosagames.subrosa.domain.player.PlayerNotFoundException;
import com.subrosagames.subrosa.domain.player.Team;
import com.subrosagames.subrosa.domain.player.TeamNotFoundException;
import com.subrosagames.subrosa.security.SubrosaUser;
import com.subrosagames.subrosa.service.GameService;

/**
 * Controller for game team related CRUD operations.
 */
@RestController
@RequestMapping("/game/{" + AbstractCrudController.PARENT_ID + "}/team")
public class ApiGameTeamController extends AbstractCrudController<Team, TeamDescriptor> {

    private static final Logger LOG = LoggerFactory.getLogger(ApiGameTeamController.class);

    @Autowired
    private GameService gameService;

    @Override
    protected List<? extends Team> listObjects(String gameUrl) throws GameNotFoundException {
        LOG.debug("Listing teams for game {}", gameUrl);
        return gameService.listTeams(gameUrl);
    }

    @Override
    protected Team getObject(String gameUrl, String teamId) throws GameNotFoundException, TeamNotFoundException {
        LOG.debug("Getting team {} for game {}", teamId, gameUrl);
        return gameService.getTeam(gameUrl, Integer.valueOf(teamId));
    }

    @Override
    protected Team createObject(String gameUrl, TeamDescriptor teamDescriptor) throws GameNotFoundException {
        LOG.debug("Creating team for game {} as {}", gameUrl, teamDescriptor);
        return gameService.createTeam(gameUrl, teamDescriptor);
    }

    @Override
    protected String createdObjectLocation(String gameUrl, String teamId) {
        String location = "/game/" + gameUrl + "/team/" + teamId;
        LOG.debug("Newly created team {} for game {} is at location {}", teamId, gameUrl, location);
        return location;
    }

    @Override
    protected Team updateObject(String gameUrl, String teamId, TeamDescriptor teamDescriptor) throws TeamNotFoundException, GameNotFoundException {
        LOG.debug("Updating team {} for game {} as {}", teamId, gameUrl, teamDescriptor);
        return gameService.updateTeam(gameUrl, Integer.valueOf(teamId), teamDescriptor);
    }

    @RequestMapping(value = { "/{" + CHILD_ID + "}/join", "/{" + CHILD_ID + "}/join/" }, method = RequestMethod.POST)
    public Team joinTeam(@AuthenticationPrincipal SubrosaUser user,
                         @PathVariable(PARENT_ID) String gameUrl,
                         @PathVariable(CHILD_ID) String teamId,
                         @RequestBody JoinTeamRequest joinTeamRequest)
            throws TeamNotFoundException, GameNotFoundException, NotAuthenticatedException, PlayerNotFoundException
    {
        LOG.debug("Joining team {} for game {} as {}", teamId, gameUrl, joinTeamRequest);
        Player player = gameService.getGame(gameUrl).getPlayerForUser(user.getId());
        Team team = gameService.getTeam(gameUrl, Integer.valueOf(teamId));
        return gameService.joinTeam(player, team, joinTeamRequest);
    }
}

