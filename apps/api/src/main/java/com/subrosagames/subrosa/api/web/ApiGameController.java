package com.subrosagames.subrosa.api.web;

import java.math.BigDecimal;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.subrosagames.subrosa.api.NotAuthenticatedException;
import com.subrosagames.subrosa.api.dto.GameDescriptor;
import com.subrosagames.subrosa.api.dto.PostDescriptor;
import com.subrosagames.subrosa.api.dto.TargetAchievement;
import com.subrosagames.subrosa.api.dto.target.TargetDto;
import com.subrosagames.subrosa.api.dto.target.TargetDtoFactory;
import com.subrosagames.subrosa.api.dto.target.TargetList;
import com.subrosagames.subrosa.api.list.QueryCriteria;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.game.BaseGame;
import com.subrosagames.subrosa.domain.game.Game;
import com.subrosagames.subrosa.domain.game.GameFactory;
import com.subrosagames.subrosa.domain.game.GameNotFoundException;
import com.subrosagames.subrosa.domain.game.UrlConflictException;
import com.subrosagames.subrosa.domain.game.event.GameHistory;
import com.subrosagames.subrosa.domain.game.persistence.PostEntity;
import com.subrosagames.subrosa.domain.game.validation.GameValidationException;
import com.subrosagames.subrosa.domain.game.validation.PostValidationException;
import com.subrosagames.subrosa.domain.image.ImageNotFoundException;
import com.subrosagames.subrosa.domain.location.Coordinates;
import com.subrosagames.subrosa.domain.location.Zone;
import com.subrosagames.subrosa.domain.message.Post;
import com.subrosagames.subrosa.domain.player.Player;
import com.subrosagames.subrosa.domain.player.Target;
import com.subrosagames.subrosa.domain.player.TargetNotFoundException;
import com.subrosagames.subrosa.security.SecurityHelper;
import com.subrosagames.subrosa.service.GameService;
import com.subrosagames.subrosa.service.PaginatedList;
import com.subrosagames.subrosa.util.ObjectUtils;
import com.subrosagames.subrosa.util.RequestUtils;

/**
 * Controller for {@link com.subrosagames.subrosa.domain.game.Game} related CRUD operations.
 */
@RestController
@RequestMapping("/game")
public class ApiGameController extends BaseApiController {

    private static final Logger LOG = LoggerFactory.getLogger(ApiGameController.class);

    @Autowired
    private GameFactory gameFactory;

    @Autowired
    private GameService gameService;

    /**
     * Get a list of {@link Game}s.
     *
     * @param limitParam  maximum number of {@link Game}s to return.
     * @param offsetParam offsetParam into the list.
     * @param expand      fields to expand
     * @param latitude    game location latitude
     * @param longitude   game location longitude
     * @param request     HTTP servlet request
     * @return a PaginatedList of {@link Game}s.
     */
    @RequestMapping(value = { "", "/" }, method = RequestMethod.GET)
    @ResponseBody
    public PaginatedList<Game> listGames(@RequestParam(value = "limitParam", required = false) Integer limitParam,
                                         @RequestParam(value = "offsetParam", required = false) Integer offsetParam,
                                         @RequestParam(value = "expand", required = false) String expand,
                                         @RequestParam(value = "latitude", required = false) Double latitude,
                                         @RequestParam(value = "longitude", required = false) Double longitude,
                                         HttpServletRequest request)
    {
        LOG.debug("Getting game list with limitParam {}, offsetParam {}, expand {}, latitude {}, longitude {}.",
                limitParam, offsetParam, expand, latitude, longitude);
        String[] expansions = StringUtils.isEmpty(expand) ? new String[0] : expand.split(",");
        if (latitude != null && longitude != null) {
            Coordinates coordinates = new Coordinates(new BigDecimal(latitude), new BigDecimal(longitude));
            int limit = ObjectUtils.defaultIfNull(limitParam, 10);
            int offset = ObjectUtils.defaultIfNull(offsetParam, 0);
            return gameFactory.getGamesNear(coordinates, limit, offset, expansions);
        }
        QueryCriteria<BaseGame> queryCriteria = RequestUtils.createQueryCriteriaFromRequestParameters(request, BaseGame.class);
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
        String[] expansions = StringUtils.isEmpty(expand) ? new String[0] : expand.split(",");
        LOG.debug("Getting game at {} with expansions {}", gameUrl, expansions);
        return gameService.getGame(gameUrl, expansions);
    }

    /**
     * Create a {@link Game} from the provided parameters.
     *
     * @param gameDescriptorParam description of game
     * @return {@link Game}
     * @throws GameValidationException   if game is invalid for creation
     * @throws ImageNotFoundException    if image is not found
     * @throws NotAuthenticatedException if request is not authenticated
     */
    @RequestMapping(value = { "", "/" }, method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public Game createGame(@RequestBody(required = false) GameDescriptor gameDescriptorParam)
            throws GameValidationException, NotAuthenticatedException, ImageNotFoundException
    {
        if (!SecurityHelper.isAuthenticated()) {
            throw new NotAuthenticatedException("Unauthenticated attempt to create a game.");
        }
        GameDescriptor gameDescriptor = ObjectUtils.defaultIfNull(gameDescriptorParam, new GameDescriptor());
        Account user = getAuthenticatedUser();
        LOG.debug("Creating new game for user {}: {}", user.getEmail(), gameDescriptor);
        return gameService.createGame(gameDescriptor, user);
    }

    /**
     * Update an {@link Game} from the provided parameters.
     *
     * @param gameUrl        game id
     * @param gameDescriptor game descriptor
     * @return {@link Game}
     * @throws GameNotFoundException     if game is not found
     * @throws GameValidationException   if game is invalid for updating
     * @throws ImageNotFoundException    if image is not found
     * @throws NotAuthenticatedException if request is not authenticated
     */
    @RequestMapping(value = "/{gameUrl}", method = RequestMethod.PUT)
    @ResponseBody
    public Game updateGame(@PathVariable("gameUrl") String gameUrl,
                           @RequestBody GameDescriptor gameDescriptor)
            throws GameValidationException, GameNotFoundException, NotAuthenticatedException, ImageNotFoundException
    {
        if (!SecurityHelper.isAuthenticated()) {
            throw new NotAuthenticatedException("Unauthenticated attempt to update a game.");
        }
        try {
            return gameService.updateGame(gameUrl, gameDescriptor);
        } catch (JpaSystemException | DataIntegrityViolationException e) {
            if (isUniqueConstraintViolation(e)) {
                throw new UrlConflictException("URL already in use.", e);
            }
            throw e;
        }
    }

    /**
     * Publish game.
     *
     * @param gameUrl game url
     * @return updated game
     * @throws GameNotFoundException     if game is not found
     * @throws GameValidationException   if game is not valid for publishing
     * @throws NotAuthenticatedException if request is not authenticated
     */
    @RequestMapping(value = "/{gameUrl}/publish", method = RequestMethod.POST)
    @ResponseBody
    public Game publishGame(@PathVariable("gameUrl") String gameUrl)
            throws GameNotFoundException, GameValidationException, NotAuthenticatedException
    {
        if (!SecurityHelper.isAuthenticated()) {
            throw new NotAuthenticatedException("Unauthenticated attempt to publish a game.");
        }
        return gameService.publishGame(gameUrl);
    }

    /**
     * Get a paginated list of posts for the specified game.
     *
     * @param gameUrl     game id
     * @param limitParam  number of posts
     * @param offsetParam offsetParam into posts
     * @return paginated list of posts
     * @throws GameNotFoundException if game is not found
     */
    @RequestMapping(value = "/{gameUrl}/post", method = RequestMethod.GET)
    @ResponseBody
    public PaginatedList<Post> getPosts(@PathVariable("gameUrl") String gameUrl,
                                        @RequestParam(value = "limitParam", required = false) Integer limitParam,
                                        @RequestParam(value = "offsetParam", required = false) Integer offsetParam)
            throws GameNotFoundException
    {
        int limit = ObjectUtils.defaultIfNull(limitParam, 10);
        int offset = ObjectUtils.defaultIfNull(offsetParam, 0);
        List<Post> posts = gameFactory.getGame(gameUrl, "posts").getPosts();
        if (CollectionUtils.isEmpty(posts)) {
            return new PaginatedList<>(Lists.<Post>newArrayList(), 0, limit, offset);
        } else {
            return new PaginatedList<>(
                    posts.subList(offset, Math.min(posts.size(), offset + limit)),
                    posts.size(),
                    limit, offset);
        }
    }

    /**
     * Create new post for game.
     *
     * @param gameUrl             game url
     * @param postDescriptorParam post descriptor
     * @return created post
     * @throws GameNotFoundException     if game is not found
     * @throws NotAuthenticatedException if user is not authenticated
     * @throws PostValidationException   if post is invalid for creation
     */
    @RequestMapping(value = { "/{gameUrl}/post", "/{gameUrl}/post/" }, method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public Post createPost(@PathVariable("gameUrl") String gameUrl,
                           @RequestBody(required = false) PostDescriptor postDescriptorParam)
            throws GameNotFoundException, NotAuthenticatedException, PostValidationException
    {
        if (!SecurityHelper.isAuthenticated()) {
            throw new NotAuthenticatedException("Unauthenticated attempt to create a post.");
        }
        PostDescriptor postDescriptor = ObjectUtils.defaultIfNull(postDescriptorParam, new PostDescriptor());
        Game game = gameFactory.getGame(gameUrl);
        PostEntity postEntity = gameFactory.forDto(postDescriptor);
        postEntity.setAccount(SecurityHelper.getAuthenticatedUser());
        return game.addPost(postEntity);
    }

    /**
     * Get game history.
     *
     * @param gameUrl     game url
     * @param limitParam  limit
     * @param offsetParam offset
     * @return paginated list of history
     * @throws GameNotFoundException if game is not found
     */
    @RequestMapping(value = "/game/{gameUrl}/history", method = RequestMethod.GET)
    @ResponseBody
    public PaginatedList<GameHistory> getHistory(@PathVariable("gameUrl") String gameUrl,
                                                 @RequestParam(value = "limitParam", required = false) Integer limitParam,
                                                 @RequestParam(value = "offsetParam", required = false) Integer offsetParam)
            throws GameNotFoundException
    {
        int limit = ObjectUtils.defaultIfNull(limitParam, 10);
        int offset = ObjectUtils.defaultIfNull(offsetParam, 0);
        LOG.debug("Retrieving history for game {}", gameUrl);
        List<GameHistory> events = gameFactory.getGame(gameUrl).getHistory();
        if (CollectionUtils.isEmpty(events)) {
            return new PaginatedList<>(Lists.<GameHistory>newArrayList(), 0, limit, offset);
        } else {
            return new PaginatedList<>(
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
     * @throws GameNotFoundException     if game is not found
     * @throws NotAuthenticatedException if request is unauthenticated
     */
    @RequestMapping(value = "/{gameUrl}/target", method = RequestMethod.GET)
    @ResponseBody
    public TargetList getTargets(@PathVariable("gameUrl") String gameUrl) throws GameNotFoundException, NotAuthenticatedException {
        int accountId = getAuthenticatedUser().getId();
        LOG.debug("Retrieving targets for game {} and account {}", gameUrl, accountId);
        Game game = gameFactory.getGame(gameUrl);
        Player player = game.getPlayerForUser(accountId);
        LOG.debug("Found player {} in game. Getting targets.", player.getId());
        List<? extends Target> targets = player.getTargets();
        LOG.debug("Player {} in game {} has {} targets", player.getId(), gameUrl, targets.size());
        return new TargetList(Lists.transform(targets, new Function<Target, TargetDto>() {
            @Override
            public TargetDto apply(Target input) {
                return TargetDtoFactory.getDtoForTarget(input);
            }
        }));
    }

    /**
     * Get list of the game zones.
     *
     * @param gameUrl game url
     * @return game zones
     * @throws GameNotFoundException if game is not found
     */
    @RequestMapping(value = "/{gameUrl}/zone", method = RequestMethod.GET)
    @ResponseBody
    public List<Zone> getZones(@PathVariable("gameUrl") String gameUrl) throws GameNotFoundException {
        LOG.debug("Retrieving zones for game {}", gameUrl);
        return gameFactory.getGameZones(gameUrl);
    }

    /**
     * Get specified target.
     *
     * @param gameUrl  game url
     * @param targetId target id
     * @return target
     * @throws GameNotFoundException     if game is not found
     * @throws TargetNotFoundException   if target is not found
     * @throws NotAuthenticatedException if request is unauthenticated
     */
    @RequestMapping(value = "/{gameUrl}/target/{targetId}", method = RequestMethod.GET)
    @ResponseBody
    public TargetDto getTarget(@PathVariable("gameUrl") String gameUrl,
                               @PathVariable("targetId") Integer targetId) throws GameNotFoundException, TargetNotFoundException, NotAuthenticatedException
    {
        int accountId = getAuthenticatedUser().getId();
        Game game = gameFactory.getGame(gameUrl);
        Player player = game.getPlayerForUser(accountId);
        Target target = player.getTarget(targetId);
        return TargetDtoFactory.getDtoForTarget(target);
    }

    /**
     * Achieve the specified target.
     *
     * @param gameUrl           game url
     * @param targetId          target id
     * @param targetAchievement information about the target achieval
     * @throws GameNotFoundException     if game is not found
     * @throws TargetNotFoundException   if target is not found
     * @throws NotAuthenticatedException if request is unauthenticated
     */
    @RequestMapping(value = "/{gameUrl}/target/{targetId}", method = RequestMethod.POST)
    @ResponseBody
    public void achieveTarget(@PathVariable("gameUrl") String gameUrl,
                              @PathVariable("targetId") Integer targetId,
                              @RequestBody TargetAchievement targetAchievement)
            throws GameNotFoundException, TargetNotFoundException, NotAuthenticatedException
    {
        int accountId = getAuthenticatedUser().getId();
        Game game = gameFactory.getGame(gameUrl);
        Player player = game.getPlayerForUser(accountId);
        game.achieveTarget(player, targetId, targetAchievement.getCode());
    }

    private boolean isUniqueConstraintViolation(NonTransientDataAccessException e) {
        String message = e.getMostSpecificCause().getMessage();
        return message.contains("unique constraint");
    }
}