package com.subrosagames.subrosa.domain.game;

import java.util.List;
import java.util.Map;

import com.subrosagames.subrosa.api.dto.GameDescriptor;
import com.subrosagames.subrosa.api.dto.PlayerDescriptor;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.game.event.GameEvent;
import com.subrosagames.subrosa.domain.game.event.GameEventNotFoundException;
import com.subrosagames.subrosa.domain.game.event.GameHistory;
import com.subrosagames.subrosa.domain.game.persistence.EventEntity;
import com.subrosagames.subrosa.domain.game.persistence.PostEntity;
import com.subrosagames.subrosa.domain.game.validation.GameEventValidationException;
import com.subrosagames.subrosa.domain.game.validation.GameValidationException;
import com.subrosagames.subrosa.domain.game.validation.PostValidationException;
import com.subrosagames.subrosa.domain.location.Zone;
import com.subrosagames.subrosa.domain.message.Post;
import com.subrosagames.subrosa.domain.player.Player;
import com.subrosagames.subrosa.domain.player.PlayerValidationException;
import com.subrosagames.subrosa.domain.player.TargetNotFoundException;

/**
 * The minimum information shared by all games.
 */
public interface Game extends GameData {

    /**
     * Get owner.
     *
     * @return owner
     */
    Account getOwner();

    /**
     * Get the game rules.
     *
     * @return a categorized list of rules
     */
    Map<RuleType, List<String>> getRules();

    /**
     * Handle a player's achievement of one of their targets.
     *
     * @param player   player
     * @param targetId target id
     * @param code     achievement code
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
     *
     * @param account          account
     * @param playerDescriptor player information
     * @return game player
     */
    Player addUserAsPlayer(Account account, PlayerDescriptor playerDescriptor) throws PlayerValidationException;

    /**
     * Get the player associated with the provided account id.
     *
     * @param accountId account id
     * @return player
     */
    Player getPlayer(int accountId);

    /**
     * Get all of the players enrolled in the game.
     *
     * @return set of players in the game
     */
    List<Player> getPlayers();

    /**
     * Set a game attribute on this game.
     *
     * @param attributeType  attribute type
     * @param attributeValue attribute value
     */
    void setAttribute(Enum<? extends GameAttributeType> attributeType, Enum<? extends GameAttributeValue> attributeValue);

    /**
     * Get a list of game events.
     *
     * @return list of game events
     */
    List<GameEvent> getEvents();

    /**
     * Get the specified game event.
     *
     * @param eventId game event id
     * @return game event
     * @throws GameEventNotFoundException if the specified game event does not exist
     */
    GameEvent getEvent(int eventId) throws GameEventNotFoundException;

    /**
     * Get a list of historical game events.
     *
     * @return list of historical game events
     */
    List<GameHistory> getHistory();

    /**
     * Posts in the game feed.
     *
     * @return list of posts
     */
    List<Post> getPosts();

    /**
     * Get the zones in which the game occurs.
     *
     * @return list of game zones
     */
    List<Zone> getZones();

    /**
     * Persist a new game from the current state.
     *
     * @return the persisted game
     * @throws GameValidationException if the game is invalid for creation
     */
    Game create() throws GameValidationException;

    /**
     * Update the game with the current state.
     *
     * @param game game information
     * @return updated game
     * @throws GameValidationException if the game state is invalid
     */
    Game update(GameDescriptor game) throws GameValidationException;

    /**
     * Publish the game.
     *
     * @return the published game
     * @throws GameValidationException if the game state is invalid for publishing
     */
    Game publish() throws GameValidationException;

    /**
     * Adds a post to the game feed.
     *
     * @param postEntity post to add
     * @return added post
     * @throws PostValidationException if the post entity is invalid
     */
    Post addPost(PostEntity postEntity) throws PostValidationException;

    /**
     * Adds an event to the game events.
     *
     * @param eventEntity game event to add
     * @return added game event
     * @throws GameEventValidationException if the game event is invalid
     */
    GameEvent addEvent(EventEntity eventEntity) throws GameEventValidationException;
}
