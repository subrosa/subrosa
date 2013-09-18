package com.subrosagames.subrosa.api;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

import com.subrosagames.subrosa.api.dto.GameDescriptor;
import com.subrosagames.subrosa.api.dto.TargetAchievement;
import com.subrosagames.subrosa.api.dto.target.TargetDto;
import com.subrosagames.subrosa.api.dto.target.TargetDtoFactory;
import com.subrosagames.subrosa.api.dto.target.TargetList;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.game.Game;
import com.subrosagames.subrosa.domain.game.GameFactory;
import com.subrosagames.subrosa.domain.game.GameNotFoundException;
import com.subrosagames.subrosa.domain.game.GameValidationException;
import com.subrosagames.subrosa.domain.game.event.GameEvent;
import com.subrosagames.subrosa.domain.game.persistence.GameEntity;
import com.subrosagames.subrosa.domain.message.Post;
import com.subrosagames.subrosa.domain.player.Player;
import com.subrosagames.subrosa.domain.player.Target;
import com.subrosagames.subrosa.domain.player.TargetNotFoundException;
import com.subrosagames.subrosa.security.SecurityHelper;
import com.subrosagames.subrosa.security.SubrosaUser;
import com.subrosagames.subrosa.service.PaginatedList;
import com.subrosagames.subrosa.util.ObjectUtils;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
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

/**
 * Controller for {@link com.subrosagames.subrosa.domain.game.Game} related CRUD operations.
 */
@Controller
@RequestMapping("/game")
public class ApiGameController {

    private static final Logger LOG = LoggerFactory.getLogger(ApiGameController.class);

    @Autowired
    private GameFactory gameFactory;

    /**
     * Get a list of {@link com.subrosagames.subrosa.domain.game.GameHelper}s.
     * @param limit  maximum number of {@link com.subrosagames.subrosa.domain.game.GameHelper}s to return.
     * @param offset offset into the list.
     * @return a PaginatedList of {@link com.subrosagames.subrosa.domain.game.GameHelper}s.
     */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    @ResponseBody
    public PaginatedList<Game> listGames(@RequestParam(value = "limit", required = false) Integer limit,
                                         @RequestParam(value = "offset", required = false) Integer offset,
                                         @RequestParam(value = "expand", required = false) String expand)
    {
        LOG.debug("Getting game list with limit {} and offset {}.", limit, offset);
        limit = ObjectUtils.defaultIfNull(limit, 10);
        offset = ObjectUtils.defaultIfNull(offset, 0);
        if (StringUtils.isEmpty(expand)) {
            return gameFactory.getGames(limit, offset);
        } else {
            return gameFactory.getGames(limit, offset, expand.split(","));
        }
    }

    /**
     * Get a {@link com.subrosagames.subrosa.domain.game.GameHelper} representation.
     * @param url the game url
     * @return {@link com.subrosagames.subrosa.domain.game.GameHelper}
     */
    @RequestMapping(value = {"/{url}", "/{url}/"}, method = RequestMethod.GET)
    @ResponseBody
    public Game getGameByUrl(@PathVariable("url") String url,
                             @RequestParam(value = "expand", required = false) String expand)
            throws GameNotFoundException
    {
        LOG.debug("Getting game at {} with expansions {}", url, expand);

        if (StringUtils.isEmpty(expand)) {
            return gameFactory.getGame(url);
        } else {
            return gameFactory.getGame(url, expand.split(","));
        }
    }

    /**
     * Create a {@link com.subrosagames.subrosa.domain.game.GameHelper} from the provided parameters.
     * @param gameDescriptor description of game
     * @return {@link com.subrosagames.subrosa.domain.game.GameHelper}
     */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public Game createGame(@RequestBody GameDescriptor gameDescriptor) throws GameValidationException, NotAuthenticatedException {
        if (!SecurityHelper.isAuthenticated()) {
            throw new NotAuthenticatedException("Unauthenticated attempt to create a game.");
        }
        Account user = getAuthenticatedUser();
        LOG.debug("Creating new game for user {}: {}", user.getEmail(), gameDescriptor);
        GameEntity gameEntity = gameFactory.forDto(gameDescriptor);
        gameEntity.setOwner(user);
        return gameEntity.create();
    }

    /**
     * Update an {@link com.subrosagames.subrosa.domain.game.GameHelper} from the provided parameters.
     * @param gameUrl game id
     * @return {@link com.subrosagames.subrosa.domain.game.GameHelper}
     */
    @RequestMapping(value = "/{gameUrl}", method = RequestMethod.PUT)
    @ResponseBody
    public Game updateGame(@PathVariable("gameUrl") String gameUrl,
                           @RequestBody GameDescriptor gameDescriptor)
            throws GameValidationException, GameNotFoundException, NotAuthenticatedException
    {
        if (!SecurityHelper.isAuthenticated()) {
            throw new NotAuthenticatedException("Unauthenticated attempt to update a game.");
        }
        Account user = getAuthenticatedUser();
        Game game = gameFactory.getGame(gameUrl);
        if (!user.getId().equals(game.getOwner().getId())) {
            throw new GameNotFoundException("Could not find game to update at " + gameUrl);
        }
        // read-only fields
        gameDescriptor.setUrl(gameUrl);
        gameDescriptor.setGameType(game.getGameType());
        return game.update(gameFactory.forDto(gameDescriptor));
    }

    @RequestMapping(value = "/{gameUrl}/publish", method = RequestMethod.POST)
    @ResponseBody
    public Game publishGame(@PathVariable("gameUrl") String gameUrl)
            throws GameNotFoundException, GameValidationException, NotAuthenticatedException
    {
        if (!SecurityHelper.isAuthenticated()) {
            throw new NotAuthenticatedException("Unauthenticated attempt to publish a game.");
        }
        return gameFactory.getGame(gameUrl).publish();
    }

    /**
     * Get a paginated list of posts for the specified game.
     * @param gameUrl game id
     * @param limit   number of posts
     * @param offset  offset into posts
     * @return paginated list of posts
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
        List<Post> posts = gameFactory.getGame(gameUrl).getPosts();
        if (CollectionUtils.isEmpty(posts)) {
            return new PaginatedList<Post>(Lists.<Post>newArrayList(), 0, limit, offset);
        } else {
            return new PaginatedList<Post>(
                    posts.subList(offset, offset + limit),
                    posts.size(),
                    limit, offset);
        }
    }

    @RequestMapping(value = "/game/{gameUrl}/history", method = RequestMethod.GET)
    @ResponseBody
    public PaginatedList<GameEvent> getHistory(@PathVariable("gameUrl") String gameUrl,
                                               @RequestParam(value = "limit", required = false) Integer limit,
                                               @RequestParam(value = "offset", required = false) Integer offset)
            throws GameNotFoundException
    {
        limit = ObjectUtils.defaultIfNull(limit, 10);
        offset = ObjectUtils.defaultIfNull(offset, 0);
        LOG.debug("Retrieving history for game {}", gameUrl);
        List<GameEvent> events = gameFactory.getGame(gameUrl).getHistory();
        if (CollectionUtils.isEmpty(events)) {
            return new PaginatedList<GameEvent>(Lists.<GameEvent>newArrayList(), 0, limit, offset);
        } else {
            return new PaginatedList<GameEvent>(
                    events.subList(offset, offset + limit),
                    events.size(),
                    limit, offset);
        }
    }

    /**
     * Get the current targets for the logged in user for the specified game.
     * @param gameUrl game id
     * @return list of targets
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
        LOG.debug("Player {} in game {} has {} targets", new Object[]{player.getId(), gameUrl, targets.size()});
        return new TargetList(Lists.transform(targets, new Function<Target, TargetDto>() {
            @Override
            public TargetDto apply(Target input) {
                return TargetDtoFactory.getDtoForTarget(input);
            }
        }));
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
                              HttpServletResponse httpServletResponse) throws GameNotFoundException, TargetNotFoundException
    {
        int accountId = getAuthenticatedUser().getId();
        Game game = gameFactory.getGame(gameUrl);
        Player player = game.getPlayer(accountId);
        if (!game.achieveTarget(player, targetId, targetAchievement.getCode())) {
            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
    }

    @PreAuthorize("isAuthenticated()")
    public Player joinGame(String gameUrl) throws GameNotFoundException {
        Game game = gameFactory.getGame(gameUrl);
        return game.addUserAsPlayer(getAuthenticatedUser());
    }

    private Account getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ((SubrosaUser) authentication.getPrincipal()).getAccount();
    }

}
