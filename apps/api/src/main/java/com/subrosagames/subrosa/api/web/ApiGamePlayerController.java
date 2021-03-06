package com.subrosagames.subrosa.api.web;

import java.util.List;

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
import com.subrosagames.subrosa.api.NotAuthenticatedException;
import com.subrosagames.subrosa.api.NotAuthorizedException;
import com.subrosagames.subrosa.api.dto.JoinGameRequest;
import com.subrosagames.subrosa.domain.account.AccountNotFoundException;
import com.subrosagames.subrosa.domain.account.AddressNotFoundException;
import com.subrosagames.subrosa.domain.account.PlayerProfileNotFoundException;
import com.subrosagames.subrosa.domain.game.Game;
import com.subrosagames.subrosa.domain.game.GameFactory;
import com.subrosagames.subrosa.domain.game.GameNotFoundException;
import com.subrosagames.subrosa.domain.image.ImageNotFoundException;
import com.subrosagames.subrosa.domain.player.Player;
import com.subrosagames.subrosa.domain.player.PlayerNotFoundException;
import com.subrosagames.subrosa.domain.player.PlayerValidationException;
import com.subrosagames.subrosa.security.SubrosaUser;
import com.subrosagames.subrosa.service.GameService;
import com.subrosagames.subrosa.service.PaginatedList;
import com.subrosagames.subrosa.util.ObjectUtils;

/**
 * Controller for {@link Player} related CRUD operations.
 */
@Controller
@RequestMapping("/game/{gameUrl}/player")
public class ApiGamePlayerController {

    private static final Logger LOG = LoggerFactory.getLogger(ApiGamePlayerController.class);

    @Autowired
    private GameFactory gameFactory;

    @Autowired
    private GameService gameService;

    /**
     * Get a list of the {@link Player}s in the specified game.
     *
     * @param gameUrl     the game
     * @param limitParam  maximum number of {@link Player}s to return.
     * @param offsetParam offset into the list.
     * @return a PaginatedList of {@link Player}s.
     * @throws GameNotFoundException     if game is not found
     * @throws NotAuthenticatedException if request is not authenticated
     * @throws NotAuthorizedException    if user does not have correct permissions
     */
    @RequestMapping(value = { "", "/" }, method = RequestMethod.GET)
    @ResponseBody
    public PaginatedList<Player> listPlayers(@AuthenticationPrincipal SubrosaUser user,
                                             @PathVariable("gameUrl") final String gameUrl,
                                             @RequestParam(value = "limitParam", required = false) Integer limitParam,
                                             @RequestParam(value = "offset", required = false) Integer offsetParam)
            throws GameNotFoundException, NotAuthenticatedException, NotAuthorizedException
    {
        if (user == null) {
            throw new NotAuthenticatedException("Unauthenticated attempt to list game players.");
        }
        final Game game = gameFactory.getGame(gameUrl);
        if (!game.getOwner().getId().equals(user.getId())) {
            throw new NotAuthorizedException("Incorrect permissions.");
        }

        int limit = ObjectUtils.defaultIfNull(limitParam, 0);
        int offset = ObjectUtils.defaultIfNull(offsetParam, 0);
        List<? extends Player> players = game.getPlayers(limit, offset);
        if (CollectionUtils.isEmpty(players)) {
            return new PaginatedList<>(Lists.<Player>newArrayList(), 0, limit, offset);
        } else {
            return new PaginatedList<>(
                    players.subList(offset, Math.min(players.size() - 1, offset + limit)),
                    players.size(),
                    limit, offset);
        }
    }

    /**
     * Get a game player.
     *
     * @param gameUrl  game url
     * @param playerId player id
     * @return game player
     * @throws GameNotFoundException     if game is not found
     * @throws PlayerNotFoundException   if player is not found
     * @throws NotAuthenticatedException if request is not authenticated
     * @throws NotAuthorizedException    if user does not have correct permissions
     */
    @RequestMapping(value = { "/{playerId}", "/{playerId}/" }, method = RequestMethod.GET)
    @ResponseBody
    public Player getPlayer(@AuthenticationPrincipal SubrosaUser user,
                            @PathVariable("gameUrl") String gameUrl,
                            @PathVariable("playerId") Integer playerId)
            throws GameNotFoundException, PlayerNotFoundException, NotAuthenticatedException, NotAuthorizedException
    {
        if (user == null) {
            throw new NotAuthenticatedException("Unauthenticated attempt to get game player.");
        }
        return gameService.getGamePlayer(gameUrl, playerId);
    }

    /**
     * Creates a game player for the user.
     *
     * @param gameUrl              game url
     * @param joinGameRequestParam join game information
     * @return created player
     * @throws GameNotFoundException                                                   if the game is not found
     * @throws PlayerValidationException                                               if the player information is invalid
     * @throws com.subrosagames.subrosa.domain.player.InsufficientInformationException if required player information is missing
     * @throws com.subrosagames.subrosa.domain.player.PlayRestrictedException          if account does not satisfy game requirements
     * @throws AccountNotFoundException                                                if account is not found
     * @throws AddressNotFoundException                                                if address is not found
     * @throws ImageNotFoundException                                                  if image is not found
     * @throws NotAuthenticatedException                                               if the request is not authenticated
     * @throws NotAuthorizedException                                                  if the user does not have correct permissions
     * @throws PlayerProfileNotFoundException                                          if the specified player profile is not found
     */
    @RequestMapping(value = { "", "/" }, method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public Player joinGame(@AuthenticationPrincipal SubrosaUser user,
                           @PathVariable("gameUrl") String gameUrl,
                           @RequestBody(required = false) JoinGameRequest joinGameRequestParam)
            throws NotAuthenticatedException, NotAuthorizedException, GameNotFoundException, PlayerValidationException, AccountNotFoundException,
            AddressNotFoundException, ImageNotFoundException, PlayerProfileNotFoundException
    {
        if (user == null) {
            throw new NotAuthenticatedException("Unauthenticated attempt to join game.");
        }
        JoinGameRequest joinGameRequest = ObjectUtils.defaultIfNull(joinGameRequestParam, new JoinGameRequest());
        return gameService.joinGame(gameUrl, user.getId(), joinGameRequest);
    }

    /**
     * Update an game player.
     *
     * @param gameUrl         game url
     * @param playerId        player id
     * @param joinGameRequest player information
     * @return updated player profile
     * @throws NotAuthenticatedException      if request is unauthenticated
     * @throws AccountNotFoundException       if account is not found
     * @throws GameNotFoundException          if game is not found
     * @throws PlayerNotFoundException        if player is not found
     * @throws ImageNotFoundException         if image is not found
     * @throws AddressNotFoundException       if address is not found
     * @throws PlayerValidationException      if player is not valid for saving
     * @throws PlayerProfileNotFoundException if the specified player profile is not found
     */
    @RequestMapping(value = { "{playerId}", "{playerId}/" }, method = RequestMethod.PUT)
    @ResponseBody
    public Player updatePlayer(@AuthenticationPrincipal SubrosaUser user,
                               @PathVariable("gameUrl") String gameUrl,
                               @PathVariable("playerId") Integer playerId,
                               @RequestBody JoinGameRequest joinGameRequest)
            throws NotAuthenticatedException, AccountNotFoundException, ImageNotFoundException, PlayerNotFoundException, PlayerValidationException,
            AddressNotFoundException, GameNotFoundException, PlayerProfileNotFoundException
    {
        LOG.debug("{}: updating game player {} for game {}", user.getId(), playerId, gameUrl);
        return gameService.updateGamePlayer(gameUrl, playerId, joinGameRequest);
    }

}
