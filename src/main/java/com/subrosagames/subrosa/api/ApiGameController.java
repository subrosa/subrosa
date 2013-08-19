package com.subrosagames.subrosa.api;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;
import javax.servlet.http.HttpServletResponse;

import com.subrosagames.subrosa.domain.game.*;
import com.subrosagames.subrosa.domain.game.event.GameEvent;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.subrosa.api.notification.GeneralCode;
import com.subrosa.api.notification.Notification;
import com.subrosa.api.notification.Severity;
import com.subrosa.api.response.NotificationList;
import com.subrosagames.subrosa.api.dto.GameDescriptor;
import com.subrosagames.subrosa.api.dto.TargetAchievement;
import com.subrosagames.subrosa.api.dto.target.TargetDto;
import com.subrosagames.subrosa.api.dto.target.TargetDtoFactory;
import com.subrosagames.subrosa.api.dto.target.TargetList;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.game.assassins.AssassinGameAttributeType;
import com.subrosagames.subrosa.domain.game.assassins.OrdnanceType;
import com.subrosagames.subrosa.domain.game.persistence.GameEntity;
import com.subrosagames.subrosa.domain.game.persistence.LifecycleEntity;
import com.subrosagames.subrosa.domain.message.Post;
import com.subrosagames.subrosa.domain.player.Player;
import com.subrosagames.subrosa.domain.player.Target;
import com.subrosagames.subrosa.domain.player.TargetNotFoundException;
import com.subrosagames.subrosa.event.message.EventMessage;
import com.subrosagames.subrosa.security.SubrosaUser;
import com.subrosagames.subrosa.service.PaginatedList;

/**
 * Controller for {@link com.subrosagames.subrosa.domain.game.Game} related CRUD operations.
 */
@Controller
public class ApiGameController {

    private static final Logger LOG = LoggerFactory.getLogger(ApiGameController.class);

    @Autowired
    private GameFactory gameFactory;

    private Random random = new Random();

    public String test() {
        return null;
    }

    /**
     * Test endpoint, creates a random game.
     * @return created game
     * @throws GameValidationException if something goes wrong
     */
    @RequestMapping(value = "/android")
    @ResponseBody
    public Game doIt() throws GameValidationException, GameNotFoundException {
          //@RequestBody GameDescriptor gameDescriptor) {
//        GameEntity entity = gameDescriptor.getInfo();
//        List<GameEvent> events = gameDescriptor.getEvents();

        LifecycleEntity lifecycleEntity = new LifecycleEntity();
        lifecycleEntity.setRegistrationStart(new Timestamp(new Date().getTime() + 2500)); // SUPPRESS CHECKSTYLE MagicNumber
        lifecycleEntity.setRegistrationEnd(new Timestamp(new Date().getTime() + 5000)); // SUPPRESS CHECKSTYLE MagicNumber
        lifecycleEntity.setGameStart(new Timestamp(new Date().getTime() + 10000)); // SUPPRESS CHECKSTYLE MagicNumber
        lifecycleEntity.setGameEnd(new Timestamp(new Date().getTime() + 40000)); // SUPPRESS CHECKSTYLE MagicNumber
        lifecycleEntity.addScheduledEvent(EventMessage.MUTUAL_INTEREST_ASSIGNMENT, lifecycleEntity.getGameStart());

        GameEntity gameEntity = new GameEntity();
        gameEntity.setName("game name" + random.nextLong());
        gameEntity.setUrl("game-url-" + random.nextLong());
        gameEntity.setDescription("This is a test.");
        gameEntity.setPrice(new BigDecimal("10.00"));
        gameEntity.setMaximumTeamSize(5);
        gameEntity.setGameType(GameType.ASSASSIN);
        gameEntity.setTimezone(TimeZone.getDefault().getDisplayName());

        Game game = gameFactory.createGame(gameEntity, lifecycleEntity);

        game.setAttribute(AssassinGameAttributeType.ORDNANCE_TYPE, OrdnanceType.WATER_WEAPONS);

        Account account1 = new Account();
        account1.setId(3);
        Account account2 = new Account();
        account2.setId(4);
        Account account3 = new Account();
        account3.setId(5);
        Account account4 = new Account();
        account4.setId(6);

        game.addUserAsPlayer(account1);
        game.addUserAsPlayer(account2);
        game.addUserAsPlayer(account3);
        game.addUserAsPlayer(account4);

        return gameFactory.getGameForId(game.getId());
    }

    /**
     * Get a list of {@link com.subrosagames.subrosa.domain.game.AbstractGame}s.
     * @param limit maximum number of {@link com.subrosagames.subrosa.domain.game.AbstractGame}s to return.
     * @param offset offset into the list.
     * @return a PaginatedList of {@link com.subrosagames.subrosa.domain.game.AbstractGame}s.
     */
    @RequestMapping(value = {"/game", "/game/"}, method = RequestMethod.GET)
    @ResponseBody
    public PaginatedList<Game> listGames(@RequestParam(value = "limit", required = false) Integer limit,
                                         @RequestParam(value = "offset", required = false) Integer offset)
    {
        LOG.debug("Getting game list with limit {} and offset {}.", limit, offset);
        limit = ObjectUtils.defaultIfNull(limit, 10);
        offset = ObjectUtils.defaultIfNull(offset, 0);
        return gameFactory.getGames(limit, offset);
    }

    /**
     * Get a {@link com.subrosagames.subrosa.domain.game.AbstractGame} representation.
     * @param url the game url
     * @return {@link com.subrosagames.subrosa.domain.game.AbstractGame}
     */
    @RequestMapping(value = "/game/{url}", method = RequestMethod.GET)
    @ResponseBody
    public Game getGameByUrl(@PathVariable("url") String url) throws GameNotFoundException {
        LOG.debug("Getting game info for url {}", url);
        return gameFactory.getGameForUrl(url);
    }

    /**
     * Create a {@link com.subrosagames.subrosa.domain.game.AbstractGame} from the provided parameters.
     * @param gameDescriptor description of game
     * @return {@link com.subrosagames.subrosa.domain.game.AbstractGame}
     */
    @RequestMapping(value = "/game/", method = RequestMethod.POST)
    @ResponseBody
    public Game createGame(@RequestBody GameDescriptor gameDescriptor) {
        return null;
    }

    /**
     * Update an {@link com.subrosagames.subrosa.domain.game.AbstractGame} from the provided parameters.
     * @param gameId the gameId from the path.
     * @return {@link com.subrosagames.subrosa.domain.game.AbstractGame}
     */
    @RequestMapping(value = "/game/{gameId}", method = RequestMethod.PUT)
    @ResponseBody
    public Game updateGame(@PathVariable("gameId") Integer gameId) {
        return null;
    }

    /**
     * Get a paginated list of posts for the specified game.
     * @param gameUrl game id
     * @param limit number of posts
     * @param offset offset into posts
     * @return paginated list of posts
     */
    @RequestMapping(value = "/game/{gameUrl}/post", method = RequestMethod.GET)
    @ResponseBody
    public PaginatedList<Post> getPosts(@PathVariable("gameUrl") String gameUrl,
                               @RequestParam(value = "limit", required = false) Integer limit,
                               @RequestParam(value = "offset", required = false) Integer offset)
            throws GameNotFoundException
    {
        limit = ObjectUtils.defaultIfNull(limit, 10);
        offset = ObjectUtils.defaultIfNull(offset, 0);
        List<Post> posts = gameFactory.getGameForUrl(gameUrl).getPosts();
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
            throws GameNotFoundException {
        limit = ObjectUtils.defaultIfNull(limit, 10);
        offset = ObjectUtils.defaultIfNull(offset, 0);
        LOG.debug("Retrieving history for game {}", gameUrl);
        List<GameEvent> events = gameFactory.getGameForUrl(gameUrl).getHistory();
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
     *
     * @param gameUrl game id
     * @return list of targets
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/game/{gameUrl}/target", method = RequestMethod.GET)
    @ResponseBody
    public TargetList getTargets(@PathVariable("gameUrl") String gameUrl) throws GameNotFoundException {
        int accountId = getAuthenticatedUser().getId();
        LOG.debug("Retrieving targets for game {} and account {}", gameUrl, accountId);
        Game game = gameFactory.getGameForUrl(gameUrl);
        Player player = game.getPlayer(accountId);
        LOG.debug("Found player {} in game. Getting targets.", player.getId());
        List<? extends Target> targets = player.getTargets();
        LOG.debug("Player {} in game {} has {} targets", new Object[] { player.getId(), gameUrl, targets.size()});
        return new TargetList(Lists.transform(targets, new Function<Target, TargetDto>() {
            @Override
            public TargetDto apply(Target input) {
                return TargetDtoFactory.getDtoForTarget(input);
            }
        }));
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/game/{gameUrl}/target/{targetId}", method = RequestMethod.GET)
    @ResponseBody
    public TargetDto getTarget(@PathVariable("gameUrl") String gameUrl,
                               @PathVariable("targetId") Integer targetId) throws GameNotFoundException, TargetNotFoundException
    {
        int accountId = getAuthenticatedUser().getId();
        Game game = gameFactory.getGameForUrl(gameUrl);
        Player player = game.getPlayer(accountId);
        Target target = player.getTarget(targetId);
        return TargetDtoFactory.getDtoForTarget(target);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/game/{gameUrl}/target/{targetId}", method = RequestMethod.POST)
    @ResponseBody
    public void achieveTarget(@PathVariable("gameUrl") String gameUrl,
                              @PathVariable("targetId") Integer targetId,
                              @RequestBody TargetAchievement targetAchievement,
                              HttpServletResponse httpServletResponse) throws GameNotFoundException, TargetNotFoundException
    {
        int accountId = getAuthenticatedUser().getId();
        Game game = gameFactory.getGameForUrl(gameUrl);
        Player player = game.getPlayer(accountId);
        if (!game.achieveTarget(player, targetId, targetAchievement.getCode())) {
            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
    }

    @PreAuthorize("isAuthenticated()")
    public Player joinGame(String gameUrl) throws GameNotFoundException {
        Game game = gameFactory.getGameForUrl(gameUrl);
        return game.addUserAsPlayer(getAuthenticatedUser());
    }

    private Account getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ((SubrosaUser) authentication.getPrincipal()).getAccount();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public NotificationList handleGameNotFoundException(GameNotFoundException e) {
        Notification notification = new Notification(
                GeneralCode.DOMAIN_OBJECT_NOT_FOUND, Severity.ERROR,
                GeneralCode.DOMAIN_OBJECT_NOT_FOUND.getDefaultMessage());
        return new NotificationList(notification);
    }

    // TODO utility class
    static class ObjectUtils {

        private ObjectUtils() { }

        public static <T> T defaultIfNull(T object, T defaultValue) {
            if (object != null) {
                return object;
            }
            return defaultValue;
        }
    }
}
