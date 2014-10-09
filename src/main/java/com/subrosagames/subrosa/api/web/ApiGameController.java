package com.subrosagames.subrosa.api.web;

import java.math.BigDecimal;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.subrosa.api.actions.list.QueryCriteria;
import com.subrosagames.subrosa.api.BadRequestException;
import com.subrosagames.subrosa.api.NotAuthenticatedException;
import com.subrosagames.subrosa.api.dto.GameDescriptor;
import com.subrosagames.subrosa.api.dto.PlayerDescriptor;
import com.subrosagames.subrosa.api.dto.PostDescriptor;
import com.subrosagames.subrosa.api.dto.TargetAchievement;
import com.subrosagames.subrosa.api.dto.target.TargetDto;
import com.subrosagames.subrosa.api.dto.target.TargetDtoFactory;
import com.subrosagames.subrosa.api.dto.target.TargetList;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.game.BaseGame;
import com.subrosagames.subrosa.domain.game.Game;
import com.subrosagames.subrosa.domain.game.GameFactory;
import com.subrosagames.subrosa.domain.game.GameNotFoundException;
import com.subrosagames.subrosa.domain.game.event.GameHistory;
import com.subrosagames.subrosa.domain.game.persistence.PostEntity;
import com.subrosagames.subrosa.domain.game.validation.GameValidationException;
import com.subrosagames.subrosa.domain.game.validation.PostValidationException;
import com.subrosagames.subrosa.domain.location.Coordinates;
import com.subrosagames.subrosa.domain.location.Zone;
import com.subrosagames.subrosa.domain.message.Post;
import com.subrosagames.subrosa.domain.player.Player;
import com.subrosagames.subrosa.domain.player.PlayerValidationException;
import com.subrosagames.subrosa.domain.player.Target;
import com.subrosagames.subrosa.domain.player.TargetNotFoundException;
import com.subrosagames.subrosa.security.SecurityHelper;
import com.subrosagames.subrosa.security.SubrosaUser;
import com.subrosagames.subrosa.security.annotation.IsAuthenticated;
import com.subrosagames.subrosa.service.GameService;
import com.subrosagames.subrosa.service.PaginatedList;
import com.subrosagames.subrosa.util.ObjectUtils;
import com.subrosagames.subrosa.util.RequestUtils;

/**
 * Controller for {@link com.subrosagames.subrosa.domain.game.Game} related CRUD operations.
 */
@Controller
@RequestMapping("/game")
public class ApiGameController {

    private static final Logger LOG = LoggerFactory.getLogger(ApiGameController.class);

    @Autowired
    private GameFactory gameFactory;

    @Autowired
    private GameService gameService;

    /**
     * Get a list of {@link Game}s.
     *
     * @param limit     maximum number of {@link Game}s to return.
     * @param offset    offset into the list.
     * @param expand    fields to expand
     * @param latitude  game location latitude
     * @param longitude game location longitude
     * @param request   HTTP servlet request
     * @return a PaginatedList of {@link Game}s.
     */
    @RequestMapping(value = { "", "/" }, method = RequestMethod.GET)
    @ResponseBody
    public PaginatedList<Game> listGames(@RequestParam(value = "limit", required = false) Integer limit,
                                         @RequestParam(value = "offset", required = false) Integer offset,
                                         @RequestParam(value = "expand", required = false) String expand,
                                         @RequestParam(value = "latitude", required = false) Double latitude,
                                         @RequestParam(value = "longitude", required = false) Double longitude,
                                         HttpServletRequest request)
    {
        LOG.debug("Getting game list with limit {}, offset {}, expand {}, latitude {}, longitude {}.", limit, offset, expand, latitude, longitude);
        if (latitude != null && longitude != null) {
            Coordinates coordinates = new Coordinates(new BigDecimal(latitude), new BigDecimal(longitude));
            limit = ObjectUtils.defaultIfNull(limit, 10);
            offset = ObjectUtils.defaultIfNull(offset, 0);
            return StringUtils.isEmpty(expand)
                    ? gameFactory.getGamesNear(coordinates, limit, offset)
                    : gameFactory.getGamesNear(coordinates, limit, offset, expand.split(","));
        }
        QueryCriteria<BaseGame> queryCriteria = RequestUtils.createQueryCriteriaFromRequestParameters(request, BaseGame.class);
        String[] expansions = StringUtils.isEmpty(expand) ? new String[0] : expand.split(",");
        return gameFactory.fromCriteria(queryCriteria, expansions);
    }

    /**
     * Get a {@link Game} representation.
     *
     * @param gameUrl the game gameUrl
     * @param expand  fields to expand
     * @return {@link Game}
     * @throws GameNotFoundException if game is not found
     */
    @RequestMapping(value = { "/{gameUrl}", "/{gameUrl}/" }, method = RequestMethod.GET)
    @ResponseBody
    public Game getGameByUrl(@PathVariable("gameUrl") String gameUrl,
                             @RequestParam(value = "expand", required = false) String expand)
            throws GameNotFoundException
    {
        LOG.debug("Getting game at {} with expansions {}", gameUrl, expand);

        if (StringUtils.isEmpty(expand)) {
            return gameFactory.getGame(gameUrl);
        } else {
            return gameFactory.getGame(gameUrl, expand.split(","));
        }
    }

    /**
     * Create a {@link Game} from the provided parameters.
     *
     * @param gameDescriptor description of game
     * @return {@link Game}
     */
    @RequestMapping(value = { "", "/" }, method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    @Transactional
    public Game createGame(@RequestBody(required = false) GameDescriptor gameDescriptor)
            throws GameValidationException, NotAuthenticatedException, BadRequestException
    {
        if (!SecurityHelper.isAuthenticated()) {
            throw new NotAuthenticatedException("Unauthenticated attempt to create a game.");
        }
        if (gameDescriptor == null) {
            throw new BadRequestException("No POST body supplied");
        }
        Account user = getAuthenticatedUser();
        LOG.debug("Creating new game for user {}: {}", user.getEmail(), gameDescriptor);
        Game game = gameFactory.forDto(gameDescriptor);
        game.setOwner(user);
        return game.create();
    }

    /**
     * Update an {@link Game} from the provided parameters.
     *
     * @param gameUrl        game id
     * @param gameDescriptor game descriptor
     * @return {@link Game}
     * @throws GameNotFoundException if game is not found
     */
    @RequestMapping(value = "/{gameUrl}", method = RequestMethod.PUT)
    @ResponseBody
    @Transactional
    public Game updateGame(@PathVariable("gameUrl") String gameUrl,
                           @RequestBody GameDescriptor gameDescriptor)
            throws GameValidationException, GameNotFoundException, NotAuthenticatedException
    {
        if (!SecurityHelper.isAuthenticated()) {
            throw new NotAuthenticatedException("Unauthenticated attempt to update a game.");
        }
        return gameService.updateGame(gameUrl, gameDescriptor);
    }

    /**
     * Publish game.
     *
     * @param gameUrl game url
     * @return updated game
     * @throws GameNotFoundException     if game is not found
     * @throws GameValidationException   if game is not valid for publishing
     * @throws NotAuthenticatedException if user not authenticated
     */
    @RequestMapping(value = "/{gameUrl}/publish", method = RequestMethod.POST)
    @ResponseBody
    public Game publishGame(@PathVariable("gameUrl") String gameUrl)
            throws GameNotFoundException, GameValidationException, NotAuthenticatedException
    {
        if (!SecurityHelper.isAuthenticated()) {
            throw new NotAuthenticatedException("Unauthenticated attempt to publish a game.");
        }
//        Game game = gameFactory.getGame(gameUrl, "events");
        // check for ownership
//        return game.publish();
        return gameService.publishGame(gameUrl);
    }

    /**
     * Get a paginated list of posts for the specified game.
     *
     * @param gameUrl game id
     * @param limit   number of posts
     * @param offset  offset into posts
     * @return paginated list of posts
     * @throws GameNotFoundException if game is not found
     */
    @RequestMapping(value = "/{gameUrl}/post", method = RequestMethod.GET)
    @ResponseBody
    public PaginatedList<Post> getPosts(@PathVariable("gameUrl") String gameUrl,
                                        @RequestParam(value = "limit", required = false) Integer limit,
                                        @RequestParam(value = "offset", required = false) Integer offset)
            throws GameNotFoundException
    {
        limit = ObjectUtils.defaultIfNull(limit, 10);
        offset = ObjectUtils.defaultIfNull(offset, 0);
        List<Post> posts = gameFactory.getGame(gameUrl, "posts").getPosts();
        if (CollectionUtils.isEmpty(posts)) {
            return new PaginatedList<Post>(Lists.<Post>newArrayList(), 0, limit, offset);
        } else {
            return new PaginatedList<Post>(
                    posts.subList(offset, Math.min(posts.size() - 1, offset + limit)),
                    posts.size(),
                    limit, offset);
        }
    }

    /**
     * Create new post for game.
     *
     * @param gameUrl        game url
     * @param postDescriptor post descriptor
     * @return created post
     * @throws GameNotFoundException     if game is not found
     * @throws NotAuthenticatedException if user is not authenticated
     */
    @RequestMapping(value = { "/{gameUrl}/post", "/{gameUrl}/post/" }, method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public Post createPost(@PathVariable("gameUrl") String gameUrl,
                           @RequestBody(required = false) PostDescriptor postDescriptor)
            throws GameNotFoundException, NotAuthenticatedException, BadRequestException, PostValidationException
    {
        if (!SecurityHelper.isAuthenticated()) {
            throw new NotAuthenticatedException("Unauthenticated attempt to create a post.");
        }
        if (postDescriptor == null) {
            throw new BadRequestException("No POST body supplied");
        }
        Game game = gameFactory.getGame(gameUrl);
        PostEntity postEntity = gameFactory.forDto(postDescriptor);
        postEntity.setAccount(SecurityHelper.getAuthenticatedUser());
        return game.addPost(postEntity);
    }

    /**
     * Get game history.
     *
     * @param gameUrl game url
     * @param limit   limit
     * @param offset  offset
     * @return paginated list of history
     * @throws GameNotFoundException if game is not found
     */
    @RequestMapping(value = "/game/{gameUrl}/history", method = RequestMethod.GET)
    @ResponseBody
    public PaginatedList<GameHistory> getHistory(@PathVariable("gameUrl") String gameUrl,
                                                 @RequestParam(value = "limit", required = false) Integer limit,
                                                 @RequestParam(value = "offset", required = false) Integer offset)
            throws GameNotFoundException
    {
        limit = ObjectUtils.defaultIfNull(limit, 10);
        offset = ObjectUtils.defaultIfNull(offset, 0);
        LOG.debug("Retrieving history for game {}", gameUrl);
        List<GameHistory> events = gameFactory.getGame(gameUrl).getHistory();
        if (CollectionUtils.isEmpty(events)) {
            return new PaginatedList<GameHistory>(Lists.<GameHistory>newArrayList(), 0, limit, offset);
        } else {
            return new PaginatedList<GameHistory>(
                    events.subList(offset, offset + limit),
                    events.size(),
                    limit, offset);
        }
    }

    /**
     * Get the current targets for the logged in user for the specified game.
     *
     * @param gameUrl game id
     * @return list of targets
     * @throws GameNotFoundException if game is not found
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/{gameUrl}/target", method = RequestMethod.GET)
    @ResponseBody
    public TargetList getTargets(@PathVariable("gameUrl") String gameUrl) throws GameNotFoundException {
        int accountId = getAuthenticatedUser().getId();
        LOG.debug("Retrieving targets for game {} and account {}", gameUrl, accountId);
        Game game = gameFactory.getGame(gameUrl);
        Player player = game.getPlayer(accountId);
        LOG.debug("Found player {} in game. Getting targets.", player.getId());
        List<? extends Target> targets = player.getTargets();
        LOG.debug("Player {} in game {} has {} targets", new Object[]{ player.getId(), gameUrl, targets.size() });
        return new TargetList(Lists.transform(targets, new Function<Target, TargetDto>() {
            @Override
            public TargetDto apply(Target input) {
                return TargetDtoFactory.getDtoForTarget(input);
            }
        }));
    }

    @RequestMapping(value = "/{gameUrl}/zone", method = RequestMethod.GET)
    @ResponseBody
    public List<Zone> getZones(@PathVariable("gameUrl") String gameUrl) throws GameNotFoundException {
        LOG.debug("Retrieving zones for game {}", gameUrl);
        return gameFactory.getGameZones(gameUrl);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/{gameUrl}/target/{targetId}", method = RequestMethod.GET)
    @ResponseBody
    public TargetDto getTarget(@PathVariable("gameUrl") String gameUrl,
                               @PathVariable("targetId") Integer targetId) throws GameNotFoundException, TargetNotFoundException
    {
        int accountId = getAuthenticatedUser().getId();
        Game game = gameFactory.getGame(gameUrl);
        Player player = game.getPlayer(accountId);
        Target target = player.getTarget(targetId);
        return TargetDtoFactory.getDtoForTarget(target);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/{gameUrl}/target/{targetId}", method = RequestMethod.POST)
    @ResponseBody
    public void achieveTarget(@PathVariable("gameUrl") String gameUrl,
                              @PathVariable("targetId") Integer targetId,
                              @RequestBody TargetAchievement targetAchievement,
                              HttpServletResponse httpServletResponse)
            throws GameNotFoundException, TargetNotFoundException
    {
        int accountId = getAuthenticatedUser().getId();
        Game game = gameFactory.getGame(gameUrl);
        Player player = game.getPlayer(accountId);
        game.achieveTarget(player, targetId, targetAchievement.getCode());
    }

    @IsAuthenticated
    @RequestMapping(value = { "/{gameUrl}/join", "{gameUrl}/join/" }, method = RequestMethod.POST)
    @ResponseBody
    public Player joinGame(@PathVariable("gameUrl") String gameUrl,
                           @RequestBody(required = false) PlayerDescriptor playerDescriptor)
            throws GameNotFoundException, NotAuthenticatedException, PlayerValidationException
    {
        if (!SecurityHelper.isAuthenticated()) {
            throw new NotAuthenticatedException("Unauthenticated attempt to join game.");
        }
        playerDescriptor = ObjectUtils.defaultIfNull(playerDescriptor, new PlayerDescriptor());
        Game game = gameFactory.getGame(gameUrl);
        return game.addUserAsPlayer(getAuthenticatedUser(), playerDescriptor);
    }

    private Account getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ((SubrosaUser) authentication.getPrincipal()).getAccount();
    }

}
