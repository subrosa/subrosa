package com.subrosagames.subrosa.api.web;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

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
import com.subrosagames.subrosa.security.SubrosaUser;
import com.subrosagames.subrosa.service.PaginatedList;
import com.subrosagames.subrosa.util.ObjectUtils;

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
     * @param gameUrl     the game
     * @param limitParam  maximum number of {@link GameEvent}s to return.
     * @param offsetParam offset into the list.
     * @return a {@link PaginatedList} of {@link GameEvent}s.
     * @throws GameNotFoundException     if game is not found
     * @throws NotAuthenticatedException if request is not authenticated
     * @throws NotAuthorizedException    if user does not have permissions list events
     */
    @RequestMapping(value = { "", "/" }, method = RequestMethod.GET)
    @ResponseBody
    public PaginatedList<GameEvent> listEvents(@AuthenticationPrincipal SubrosaUser user,
                                               @PathVariable("gameUrl") String gameUrl,
                                               @RequestParam(value = "limitParam", required = false) Integer limitParam,
                                               @RequestParam(value = "offsetParam", required = false) Integer offsetParam)
            throws GameNotFoundException, NotAuthenticatedException, NotAuthorizedException
    {
        if (user == null) {
            throw new NotAuthenticatedException("Unauthenticated attempt to list game events.");
        }
        Game game = gameFactory.getGame(gameUrl, "events");
        if (!game.getOwner().getId().equals(user.getId())) {
            throw new NotAuthorizedException("Incorrect permissions.");
        }

        int limit = ObjectUtils.defaultIfNull(limitParam, 0);
        int offset = ObjectUtils.defaultIfNull(offsetParam, 0);
        List<? extends GameEvent> events = game.getEvents();
        if (CollectionUtils.isEmpty(events)) {
            return new PaginatedList<>(Lists.<GameEvent>newArrayList(), 0, limit, offset);
        } else {
            return new PaginatedList<>(
                    events.subList(offset, Math.min(events.size() - 1, offset + limit)),
                    events.size(),
                    limit, offset);
        }
    }

    /**
     * Get a {@link GameEvent}.
     *
     * @param gameUrl the game gameUrl
     * @param eventId the game event id
     * @return game event
     * @throws GameNotFoundException      if game is not found
     * @throws GameEventNotFoundException if game event is not found
     * @throws NotAuthenticatedException  if request is not authenticated
     * @throws NotAuthorizedException     if user does not have permissions to update event
     */
    @RequestMapping(value = { "/{eventId}", "/{eventId}/" }, method = RequestMethod.GET)
    @ResponseBody
    public GameEvent getEvent(@AuthenticationPrincipal SubrosaUser user,
                              @PathVariable("gameUrl") String gameUrl,
                              @PathVariable("eventId") Integer eventId)
            throws GameNotFoundException, GameEventNotFoundException, NotAuthenticatedException, NotAuthorizedException
    {
        if (user == null) {
            throw new NotAuthenticatedException("Not authenticated.");
        }
        LOG.debug("Getting game event for game {} with eventId {}", gameUrl, eventId);
        Game game = gameFactory.getGame(gameUrl, "events");
        if (!game.getOwner().getId().equals(user.getId())) {
            throw new NotAuthorizedException("Incorrect permissions.");
        }
        return game.getEvent(eventId);
    }

    /**
     * Create a {@link GameEvent} for a game.
     *
     * @param gameUrl             game identifier
     * @param gameEventDescriptor description of game event
     * @param response            http servlet response
     * @return game event
     * @throws GameNotFoundException        if game is not found
     * @throws GameEventValidationException if game event is invalid
     * @throws NotAuthenticatedException    if request is not authenticated
     * @throws NotAuthorizedException       if user does not have permissions to update event
     * @throws BadRequestException          if event information is not supplied
     */
    @RequestMapping(value = { "", "/" }, method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    @Transactional
    public GameEvent createEvent(@AuthenticationPrincipal SubrosaUser user,
                                 @PathVariable("gameUrl") String gameUrl,
                                 @RequestBody(required = false) GameEventDescriptor gameEventDescriptor,
                                 HttpServletResponse response)
            throws GameNotFoundException, GameEventValidationException,
            NotAuthenticatedException, NotAuthorizedException, BadRequestException
    {
        if (gameEventDescriptor == null) {
            throw new BadRequestException("No POST body supplied");
        }
        if (user == null) {
            throw new NotAuthenticatedException("Unauthenticated attempt to create an event.");
        }
        Game game = gameFactory.getGame(gameUrl);
        if (!game.getOwner().getId().equals(user.getId())) {
            throw new NotAuthorizedException("Unauthenticated attempt to get game event.");
        }
        LOG.debug("Creating new event for game {}: {}", game.getUrl(), gameEventDescriptor);
        GameEvent gameEvent = game.addEvent(gameEventDescriptor);
        response.setHeader("Location", "/game/" + gameUrl + "/event/" + gameEvent.getId());
        return gameEvent;
    }

    /**
     * Update a {@link GameEvent} from the provided parameters.
     *
     * @param gameUrl             game id
     * @param eventId             event id
     * @param gameEventDescriptor game event descriptor
     * @return updated game event
     * @throws GameNotFoundException        if game is not found
     * @throws GameEventNotFoundException   if game event is not found
     * @throws GameEventValidationException if game event is invalid
     * @throws NotAuthenticatedException    if request is not authenticated
     * @throws NotAuthorizedException       if user does not have permissions to update event
     * @throws BadRequestException          if event information is not supplied
     */
    @RequestMapping(value = { "/{eventId}", "/{eventId}/" }, method = RequestMethod.PUT)
    @ResponseBody
    public GameEvent updateEvent(@AuthenticationPrincipal SubrosaUser user,
                                 @PathVariable("gameUrl") String gameUrl,
                                 @PathVariable("eventId") Integer eventId,
                                 @RequestBody(required = false) GameEventDescriptor gameEventDescriptor)
            throws GameNotFoundException, GameEventNotFoundException, GameEventValidationException,
            NotAuthenticatedException, NotAuthorizedException, BadRequestException
    {
        if (user == null) {
            throw new NotAuthenticatedException("Unauthenticated attempt to update a game.");
        }
        if (gameEventDescriptor == null) {
            throw new BadRequestException("No POST body supplied");
        }
        Game game = gameFactory.getGame(gameUrl);
        if (!game.getOwner().getId().equals(user.getId())) {
            throw new NotAuthorizedException("Unauthenticated attempt to get game event.");
        }
        return game.updateEvent(eventId, gameEventDescriptor);
    }

}
