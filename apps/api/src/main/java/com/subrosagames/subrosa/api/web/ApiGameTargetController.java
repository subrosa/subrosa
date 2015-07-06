package com.subrosagames.subrosa.api.web;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import com.subrosagames.subrosa.api.dto.TargetDescriptor;
import com.subrosagames.subrosa.domain.player.Target;
import com.subrosagames.subrosa.service.GameService;

/**
 * Controller for game target related CRUD operations.
 */
@RequestMapping("/game/{parentId}/target")
public class ApiGameTargetController extends AbstractCrudController<Target, TargetDescriptor> {

    private static final Logger LOG = LoggerFactory.getLogger(ApiGameTargetController.class);

    @Autowired
    private GameService gameService;

    @Override
    protected List<Target> listObjects(String gameUrl) {
        LOG.debug("Listing targets for game {}", gameUrl);
        return gameService.listTargets(gameUrl);
    }

    @Override
    protected Target getObject(String gameUrl, String targetId) {
        LOG.debug("Getting target {} for game {}", targetId, gameUrl);
        return gameService.getTarget(gameUrl, Integer.valueOf(targetId));
    }

    @Override
    protected Target createObject(String gameUrl, TargetDescriptor targetDescriptor) {
        LOG.debug("Creating target for game {} as {}", gameUrl, targetDescriptor);
        return gameService.createTarget(gameUrl, targetDescriptor);
    }

    @Override
    protected String createdObjectLocation(String gameUrl, String targetId) {
        String location = "/game/" + gameUrl + "/target/" + targetId;
        LOG.debug("Newly created target {} for game {} is at location {}", targetId, gameUrl, location);
        return location;
    }

    @Override
    protected Target updateObject(String gameUrl, String targetId, TargetDescriptor targetDescriptor) {
        LOG.debug("Updating target {} for game {} as {}", targetId, gameUrl, targetDescriptor);
        return gameService.updateTarget(gameUrl, Integer.valueOf(targetId), targetDescriptor);
    }

}

