package com.subrosagames.subrosa.api.web;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.subrosagames.subrosa.api.dto.TeamDescriptor;
import com.subrosagames.subrosa.domain.game.GameNotFoundException;
import com.subrosagames.subrosa.domain.player.Team;
import com.subrosagames.subrosa.domain.player.TeamNotFoundException;
import com.subrosagames.subrosa.service.GameService;

/**
 * Controller for game team related CRUD operations.
 */
@RestController
@RequestMapping("/game/{parentId}/team")
public class ApiGameTeamController extends AbstractCrudController<Team, TeamDescriptor> {

    private static final Logger LOG = LoggerFactory.getLogger(ApiGameTeamController.class);

    @Autowired
    private GameService gameService;

    @Override
    protected List<Team> listObjects(String gameUrl) throws GameNotFoundException {
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

}

