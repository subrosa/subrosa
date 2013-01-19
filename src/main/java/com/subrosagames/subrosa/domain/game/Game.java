package com.subrosagames.subrosa.domain.game;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.image.Image;
import com.subrosagames.subrosa.domain.message.Post;
import com.subrosagames.subrosa.domain.player.Player;
import com.subrosagames.subrosa.domain.player.TargetNotFoundException;
import com.subrosagames.subrosa.event.Event;
import com.subrosagames.subrosa.event.TriggeredEvent;
import com.subrosagames.subrosa.event.message.EventMessage;

/**
 * The minimum information shared by all games.
 */
public interface Game {

    /**
     * Handle a player's achievement of one of their targets.
     * @param player player
     * @param targetId target id
     * @param code achievement code
     * @return whether achievement succeeded
     * @throws TargetNotFoundException if the player does not have the specified target
     */
    boolean achieveTarget(Player player, int targetId, String code) throws TargetNotFoundException;

    /**
     * Perform actions that occur at the start of a game (assignments, for example).
     */
    void startGame();

    /**
     * Add the provided account as a player in this game.
     * @param account account
     * @return game player
     */
    Player addUserAsPlayer(Account account);

    /**
     * Get the player associated with the provided account id.
     * @param accountId account id
     * @return player
     */
    Player getPlayer(int accountId);

    /**
     * Get all of the players enrolled in the game.
     * @return set of players in the game
     */
    List<Player> getPlayers();

    /**
     * Set a game attribute on this game.
     * @param attributeType attribute type
     * @param attributeValue attribute value
     */
    void setAttribute(Enum<? extends GameAttributeType> attributeType, Enum<? extends GameAttributeValue> attributeValue);

    /**
     * Posts in the game feed.
     * @return list of posts
     */
    List<Post> getPosts();

    /**
     * Get the game rules.
     * @return a categorized list of rules
     */
    Map<RuleType, List<String>> getRules();

    /**
     * Game id.
     * @return game id
     */
    int getId();

    /**
     * Game name.
     * @return game name
     */
    String getName();

    /**
     * Game url.
     * @return game url
     */
    String getUrl();

    /**
     * Game description.
     * @return game description
     */
    String getDescription();

    /**
     * Game type.
     * @return game type
     */
    GameType getGameType();

    /**
     * Game price.
     * @return game price
     */
    BigDecimal getPrice();

    /**
     * Game start time.
     * @return game start time
     */
    Date getStartTime();

    /**
     * Game end time.
     * @return game end time
     */
    Date getEndTime();

    /**
     * Registration start time.
     * @return registration start time
     */
    Date getRegistrationStartTime();

    /**
     * Registration end time.
     * @return registration end time
     */
    Date getRegistrationEndTime();

    /**
     * Timezone in which game runs.
     * @return game timezone
     */
    String getTimezone();

    /**
     * Password to join game.
     * @return game password
     */
    String getPassword();

    /**
     * Image representing game.
     * @return game image
     */
    Image getImage();

    /**
     * Minimum age to play game.
     * @return minimum age to play
     */
    Integer getMinimumAge();

    /**
     * Maximum team size for game.
     * @return maximum team size
     */
    Integer getMaximumTeamSize();

    void addTriggeredEvent(EventMessage eventType, Event trigger);

    List<TriggeredEvent> getEventsTriggeredBy(EventMessage eventMessage);
}
