package com.subrosagames.subrosa.api.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.subrosagames.subrosa.api.NotAuthenticatedException;
import com.subrosagames.subrosa.api.dto.PlayerProfileDescriptor;
import com.subrosagames.subrosa.domain.account.AccountNotFoundException;
import com.subrosagames.subrosa.domain.account.PlayerProfile;
import com.subrosagames.subrosa.domain.account.PlayerProfileNotFoundException;
import com.subrosagames.subrosa.domain.account.PlayerProfileValidationException;
import com.subrosagames.subrosa.domain.image.ImageNotFoundException;
import com.subrosagames.subrosa.domain.player.Player;
import com.subrosagames.subrosa.service.AccountService;
import com.subrosagames.subrosa.service.PaginatedList;
import com.subrosagames.subrosa.util.ObjectUtils;

/**
 * Controller for {@link Player} related CRUD operations.
 */
@Controller
public class ApiAccountPlayerController extends BaseApiController {

    private static final Logger LOG = LoggerFactory.getLogger(ApiAccountPlayerController.class);

    @Autowired
    private AccountService accountService;

    /**
     * Get a paginated list of account players.
     *
     * @param accountId   account id
     * @param limitParam  limit
     * @param offsetParam offset
     * @return paginated list of players
     * @throws NotAuthenticatedException if request is unauthenticated
     * @throws AccountNotFoundException  if account is not found
     */
    @RequestMapping(value = { "/account/{accountId}/player", "/account/{accountId}/player/" }, method = RequestMethod.GET)
    @ResponseBody
    public PaginatedList<PlayerProfile> listPlayers(@PathVariable("accountId") Integer accountId,
                                                    @RequestParam(value = "limit", required = false) Integer limitParam,
                                                    @RequestParam(value = "offset", required = false) Integer offsetParam)
            throws NotAuthenticatedException, AccountNotFoundException
    {
        LOG.debug("{}: listing player profiles for {}", getAuthenticatedUser().getId(), accountId);
        int limit = ObjectUtils.defaultIfNull(limitParam, 10);
        int offset = ObjectUtils.defaultIfNull(offsetParam, 0);
        return accountService.listPlayerProfiles(accountId, limit, offset);
    }

    /**
     * Get a paginated list of account players.
     *
     * @param limit  limit
     * @param offset offset
     * @return paginated list of players
     * @throws NotAuthenticatedException if request is unauthenticated
     * @throws AccountNotFoundException  if account is not found
     */
    @RequestMapping(value = { "/user/player", "/user/player/" }, method = RequestMethod.GET)
    @ResponseBody
    public PaginatedList<PlayerProfile> listPlayersForAuthenticatedUser(@RequestParam(value = "limit", required = false) Integer limit,
                                                                        @RequestParam(value = "offset", required = false) Integer offset)
            throws NotAuthenticatedException, AccountNotFoundException
    {
        return listPlayers(getAuthenticatedUser().getId(), limit, offset);
    }

    /**
     * Get an account player profile.
     *
     * @param accountId account id
     * @param playerId  player id
     * @return account player
     * @throws NotAuthenticatedException      if request is not authenticated
     * @throws AccountNotFoundException       if account is not found
     * @throws PlayerProfileNotFoundException if player is not found
     */
    @RequestMapping(value = { "/account/{accountId}/player/{playerId}", "/account/{accountId}/player/{playerId}/" }, method = RequestMethod.GET)
    @ResponseBody
    public PlayerProfile getPlayer(@PathVariable("accountId") Integer accountId,
                                   @PathVariable("playerId") Integer playerId)
            throws NotAuthenticatedException, AccountNotFoundException, PlayerProfileNotFoundException
    {
        LOG.debug("{}: get player profile {} for {}", getAuthenticatedUser().getId(), playerId, accountId);
        return accountService.getPlayerProfile(accountId, playerId);
    }

    /**
     * Get an account player profile.
     *
     * @param playerId player id
     * @return account player
     * @throws NotAuthenticatedException      if request is not authenticated
     * @throws AccountNotFoundException       if account is not found
     * @throws PlayerProfileNotFoundException if player is not found
     */
    @RequestMapping(value = { "/user/player/{playerId}", "/user/player/{playerId}/" }, method = RequestMethod.GET)
    @ResponseBody
    public PlayerProfile getPlayerForAuthenticatedUser(@PathVariable("playerId") Integer playerId)
            throws NotAuthenticatedException, AccountNotFoundException, PlayerProfileNotFoundException
    {
        return getPlayer(getAuthenticatedUser().getId(), playerId);
    }

    /**
     * Create an account player profile.
     *
     * @param accountId               account id
     * @param playerProfileDescriptor player profile information
     * @return created player
     * @throws NotAuthenticatedException        if request is not authenticated
     * @throws AccountNotFoundException         if account is not found
     * @throws ImageNotFoundException           if image is not found
     * @throws PlayerProfileValidationException if player profile is not valid for saving
     */
    @RequestMapping(value = { "/account/{accountId}/player", "/account/{accountId}/player/" }, method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public PlayerProfile createPlayer(@PathVariable("accountId") Integer accountId,
                                      @RequestBody PlayerProfileDescriptor playerProfileDescriptor)
            throws NotAuthenticatedException, AccountNotFoundException, ImageNotFoundException, PlayerProfileValidationException
    {
        LOG.debug("{}: creating player profile for {}", getAuthenticatedUser().getId(), accountId);
        return accountService.createPlayerProfile(accountId, playerProfileDescriptor);
    }

    /**
     * Create an account player profile.
     *
     * @param playerProfileDescriptor player profile information
     * @return created player
     * @throws NotAuthenticatedException        if request is not authenticated
     * @throws AccountNotFoundException         if account is not found
     * @throws ImageNotFoundException           if image is not found
     * @throws PlayerProfileValidationException if player profile is not valid for saving
     */
    @RequestMapping(value = { "/user/player", "/user/player/" }, method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public PlayerProfile createPlayerForAuthenticatedUser(@RequestBody PlayerProfileDescriptor playerProfileDescriptor)
            throws NotAuthenticatedException, AccountNotFoundException, ImageNotFoundException, PlayerProfileValidationException
    {
        return createPlayer(getAuthenticatedUser().getId(), playerProfileDescriptor);
    }

    /**
     * Update an account player profile.
     *
     * @param accountId               account id
     * @param playerId                player id
     * @param playerProfileDescriptor player profile information
     * @return updated player profile
     * @throws NotAuthenticatedException        if request is unauthenticated
     * @throws AccountNotFoundException         if account is not found
     * @throws PlayerProfileNotFoundException   if player profile is not found
     * @throws ImageNotFoundException           if image is not found
     * @throws PlayerProfileValidationException if player profile is not valid for saving
     */
    @RequestMapping(value = { "/account/{accountId}/player/{playerId}", "/account/{accountId}/player/{playerId}/" }, method = RequestMethod.PUT)
    @ResponseBody
    public PlayerProfile updatePlayer(@PathVariable("accountId") Integer accountId,
                                      @PathVariable("playerId") Integer playerId,
                                      @RequestBody PlayerProfileDescriptor playerProfileDescriptor)
            throws NotAuthenticatedException, AccountNotFoundException, ImageNotFoundException, PlayerProfileNotFoundException, PlayerProfileValidationException
    {
        LOG.debug("{}: updating player profile {} for account {}", getAuthenticatedUser().getId(), playerId, accountId);
        return accountService.updatePlayerProfile(accountId, playerId, playerProfileDescriptor);
    }

    /**
     * Update an account player profile.
     *
     * @param playerId                player id
     * @param playerProfileDescriptor player profile information
     * @return updated player profile
     * @throws NotAuthenticatedException        if request is unauthenticated
     * @throws AccountNotFoundException         if account is not found
     * @throws PlayerProfileNotFoundException   if player profile is not found
     * @throws ImageNotFoundException           if image is not found
     * @throws PlayerProfileValidationException if player profile is not valid for saving
     */
    @RequestMapping(value = { "/user/player/{playerId}", "/user/player/{playerId}/" }, method = RequestMethod.PUT)
    @ResponseBody
    public PlayerProfile updatePlayerForAuthenticatedUser(@PathVariable("playerId") Integer playerId,
                                                          @RequestBody PlayerProfileDescriptor playerProfileDescriptor)
            throws NotAuthenticatedException, AccountNotFoundException, PlayerProfileNotFoundException, ImageNotFoundException, PlayerProfileValidationException
    {
        return updatePlayer(getAuthenticatedUser().getId(), playerId, playerProfileDescriptor);
    }

    /**
     * Delete an account player profile.
     *
     * @param accountId account id
     * @param playerId  player id
     * @return deleted player profile
     * @throws NotAuthenticatedException      if request is unauthenticated
     * @throws AccountNotFoundException       if account is not found
     * @throws PlayerProfileNotFoundException if player profile is not found
     */
    @RequestMapping(value = { "/account/{accountId}/player/{playerId}", "/account/{accountId}/player/{playerId}/" }, method = RequestMethod.DELETE)
    @ResponseBody
    public PlayerProfile deletePlayer(@PathVariable("accountId") Integer accountId,
                                      @PathVariable("playerId") Integer playerId)
            throws NotAuthenticatedException, PlayerProfileNotFoundException, AccountNotFoundException
    {
        LOG.debug("{}: deleting player profile {} for account {}", getAuthenticatedUser().getId(), playerId, accountId);
        return accountService.deletePlayerProfile(accountId, playerId);
    }

    /**
     * Delete an account player profile.
     *
     * @param playerId player id
     * @return deleted player profile
     * @throws NotAuthenticatedException      if request is unauthenticated
     * @throws AccountNotFoundException       if account is not found
     * @throws PlayerProfileNotFoundException if player profile is not found
     */
    @RequestMapping(value = { "/user/player/{playerId}", "/user/player/{playerId}/" }, method = RequestMethod.DELETE)
    @ResponseBody
    public PlayerProfile deletePlayerForAuthenticatedUser(@PathVariable("playerId") Integer playerId)
            throws NotAuthenticatedException, PlayerProfileNotFoundException, AccountNotFoundException
    {
        return deletePlayer(getAuthenticatedUser().getId(), playerId);
    }
}

