package com.subrosagames.subrosa.api.web;

import com.google.common.collect.Lists;
import com.subrosagames.subrosa.api.BadRequestException;
import com.subrosagames.subrosa.api.NotAuthenticatedException;
import com.subrosagames.subrosa.api.NotAuthorizedException;
import com.subrosagames.subrosa.api.dto.GameEventDescriptor;
import com.subrosagames.subrosa.domain.game.Game;
import com.subrosagames.subrosa.domain.game.GameFactory;
import com.subrosagames.subrosa.domain.game.GameNotFoundException;
import com.subrosagames.subrosa.domain.game.event.GameEvent;
import com.subrosagames.subrosa.domain.game.event.GameEventNotFoundException;
import com.subrosagames.subrosa.domain.game.validation.GameEventValidationException;
import com.subrosagames.subrosa.security.SecurityHelper;
import com.subrosagames.subrosa.service.PaginatedList;
import com.subrosagames.subrosa.util.ObjectUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Controller for {@link com.subrosagames.subrosa.domain.game.Game} related CRUD operations.
 */
@Controller
@RequestMapping("/game/{gameUrl}/event")
public class ApiGameEventController {

    private static final Logger LOG = LoggerFactory.getLogger(ApiGameEventController.class);

    @Autowired
    private GameFactory gameFactory;

    /**
     * Get a list of {@link com.subrosagames.subrosa.domain.game.event.GameHistory}s for the specified game.
     *
     * @param gameUrl the game
     * @param limit   maximum number of {@link com.subrosagames.subrosa.domain.game.Game}s to return.
     * @param offset  offset into the list.
     * @return a PaginatedList of {@link com.subrosagames.subrosa.domain.game.event.GameHistory}s.
     */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    @ResponseBody
    public PaginatedList<GameEvent> listEvents(@PathVariable("gameUrl") String gameUrl,
                                               @RequestParam(value = "limit", required = false) Integer limit,
                                               @RequestParam(value = "offset", required = false) Integer offset)
            throws GameNotFoundException, NotAuthenticatedException
    {
        if (!SecurityHelper.isAuthenticated()) {
            throw new NotAuthenticatedException("Unauthenticated attempt to list game events.");
        }
        limit = ObjectUtils.defaultIfNull(limit, 0);
        offset = ObjectUtils.defaultIfNull(offset, 0);
        Game game = gameFactory.getGame(gameUrl, "events");
        List<GameEvent> events = game.getEvents();
        if (CollectionUtils.isEmpty(events)) {
            return new PaginatedList<GameEvent>(Lists.<GameEvent>newArrayList(), 0, limit, offset);
        } else {
            return new PaginatedList<GameEvent>(
                    events.subList(offset, Math.min(events.size() - 1, offset + limit)),
                    events.size(),
                    limit, offset);
        }
    }

    /**
     * Get a {@link com.subrosagames.subrosa.domain.game.event.GameEvent}.
     *
     * @param gameUrl the game gameUrl
     * @param eventId the game event id
     * @throws com.subrosagames.subrosa.domain.game.GameNotFoundException            if game is not found
     * @throws com.subrosagames.subrosa.domain.game.event.GameEventNotFoundException if game event is not found
     */
    @RequestMapping(value = {"/{eventId}", "/{eventId}/"}, method = RequestMethod.GET)
    @ResponseBody
    public GameEvent getEvent(@PathVariable("gameUrl") String gameUrl,
                              @PathVariable("eventId") Integer eventId)
            throws GameNotFoundException, GameEventNotFoundException, NotAuthenticatedException, NotAuthorizedException
    {
        if (!SecurityHelper.isAuthenticated()) {
            throw new NotAuthenticatedException("Not authenticated.");
        }
        LOG.debug("Getting game event for game {} with eventId {}", gameUrl, eventId);
        Game game = gameFactory.getGame(gameUrl, "events");
        if (!SecurityHelper.getAuthenticatedUser().getId().equals(game.getOwner().getId())) {
            throw new NotAuthorizedException("Incorrect permissions.");
        }
        return game.getEvent(eventId);
    }

    /**
     * Create a {@link com.subrosagames.subrosa.domain.game.event.GameEvent} for a game.
     *
     * @param gameUrl             game identifier
     * @param gameEventDescriptor description of game event
     * @return {@link com.subrosagames.subrosa.domain.game.event.GameEvent}
     */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    @Transactional
    public GameEvent createEvent(@PathVariable("gameUrl") String gameUrl,
                                 @RequestBody(required = false) GameEventDescriptor gameEventDescriptor)
            throws GameNotFoundException, NotAuthenticatedException, BadRequestException, GameEventValidationException, NotAuthorizedException
    {
        if (gameEventDescriptor == null) {
            throw new BadRequestException("No POST body supplied");
        }
        if (!SecurityHelper.isAuthenticated()) {
            throw new NotAuthenticatedException("Unauthenticated attempt to create an event.");
        }
        Game game = gameFactory.getGame(gameUrl);
        if (!SecurityHelper.getAuthenticatedUser().getId().equals(game.getOwner().getId())) {
            throw new NotAuthorizedException("Unauthenticated attempt to get game event.");
        }
        LOG.debug("Creating new event for game {}: {}", game.getUrl(), gameEventDescriptor);
        return game.addEvent(gameFactory.forDto(gameEventDescriptor));
    }

    /**
     * Update a {@link com.subrosagames.subrosa.domain.game.event.GameEvent} from the provided parameters.
     *
     * @param gameUrl             game id
     * @param gameEventDescriptor game event descriptor
     * @return {@link com.subrosagames.subrosa.domain.game.event.GameEvent}
     * @throws com.subrosagames.subrosa.domain.game.GameNotFoundException            if game is not found
     * @throws com.subrosagames.subrosa.domain.game.event.GameEventNotFoundException if game event is not found
     */
    @RequestMapping(value = {"/{eventId}", "/{eventId}/"}, method = RequestMethod.PUT)
    @ResponseBody
    public Game updateGame(@PathVariable("gameUrl") String gameUrl,
                           @PathVariable("eventId") Integer eventId,
                           @RequestBody GameEventDescriptor gameEventDescriptor)
            throws GameNotFoundException, GameEventNotFoundException, NotAuthenticatedException, NotAuthorizedException
    {
        if (!SecurityHelper.isAuthenticated()) {
            throw new NotAuthenticatedException("Unauthenticated attempt to update a game.");
        }
        Game game = gameFactory.getGame(gameUrl);
        if (!SecurityHelper.getAuthenticatedUser().getId().equals(game.getOwner().getId())) {
            throw new NotAuthorizedException("Unauthenticated attempt to get game event.");
        }
//        return game.updateEvent(gameEventDescriptor);
        return null;
    }

}
