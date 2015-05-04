package com.subrosagames.subrosa.domain.game;

import java.util.List;
import java.util.Map;

import com.subrosagames.subrosa.api.dto.GameDescriptor;
import com.subrosagames.subrosa.api.dto.GameEventDescriptor;
import com.subrosagames.subrosa.api.dto.JoinGameRequest;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.account.AddressNotFoundException;
import com.subrosagames.subrosa.domain.account.PlayerProfileNotFoundException;
import com.subrosagames.subrosa.domain.game.event.GameEvent;
import com.subrosagames.subrosa.domain.game.event.GameEventNotFoundException;
import com.subrosagames.subrosa.domain.game.event.GameHistory;
import com.subrosagames.subrosa.domain.game.persistence.PostEntity;
import com.subrosagames.subrosa.domain.game.validation.GameEventValidationException;
import com.subrosagames.subrosa.domain.game.validation.GameValidationException;
import com.subrosagames.subrosa.domain.game.validation.PostValidationException;
import com.subrosagames.subrosa.domain.image.ImageNotFoundException;
import com.subrosagames.subrosa.domain.location.Zone;
import com.subrosagames.subrosa.domain.message.Post;
import com.subrosagames.subrosa.domain.player.Player;
import com.subrosagames.subrosa.domain.player.PlayerNotFoundException;
import com.subrosagames.subrosa.domain.player.PlayerValidationException;
import com.subrosagames.subrosa.domain.player.TargetNotFoundException;

/**
 * The minimum functionality shared by all games.
 */
public interface Game extends GameData {

    /**
     * Get owner.
     *
     * @return owner
     */
    Account getOwner();

    /**
     * Set the account that owns the game.
     *
     * @param account owning account
     */
    void setOwner(Account account);

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
     * @throws TargetNotFoundException if the player does not have the specified target
     */
    void achieveTarget(Player player, int targetId, String code) throws TargetNotFoundException;

    /**
     * Perform actions that occur at the start of a game (assignments, for example).
     */
    void startGame();

    /**
     * Get the player associated with the provided account id.
     *
     * @param accountId account id
     * @return player
     */
    Player getPlayerForUser(int accountId);

    /**
     * Get list of the players enrolled in the game.
     *
     * @param limit  number of players to return
     * @param offset offset into list
     * @return set of players in the game
     */
    List<Player> getPlayers(Integer limit, Integer offset);

    /**
     * Get list of all players enrolled in the game.
     *
     * @return list of players in the game
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
     * @throws ImageNotFoundException  if image is not found
     */
    Game update(GameDescriptor game) throws GameValidationException, ImageNotFoundException;

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
     * @param gameEventDescriptor game event to add
     * @return added game event
     * @throws GameEventValidationException if the game event is invalid
     */
    GameEvent addEvent(GameEventDescriptor gameEventDescriptor) throws GameEventValidationException;

    /**
     * Updates an event.
     *
     * @param eventId             game event id
     * @param gameEventDescriptor game event information
     * @return updated game event
     * @throws GameEventNotFoundException   if game event does not exist
     * @throws GameEventValidationException if the game event is invalid
     */
    GameEvent updateEvent(int eventId, GameEventDescriptor gameEventDescriptor) throws GameEventNotFoundException, GameEventValidationException;

    /**
     * Add the provided account as a player in this game.
     *
     * @param account         account
     * @param joinGameRequest player information
     * @return game player
     * @throws com.subrosagames.subrosa.domain.player.InsufficientInformationException if game requires more information to join
     * @throws com.subrosagames.subrosa.domain.player.PlayRestrictedException          if player does not meet requirements to play
     * @throws PlayerValidationException                                               if player information is invalid
     * @throws IllegalArgumentException                                                if either parameter is null
     * @throws AddressNotFoundException                                                if address is not found
     * @throws ImageNotFoundException                                                  if image is not found
     * @throws PlayerProfileNotFoundException                                          if the specified player profile is not found
     */
    Player joinGame(Account account, JoinGameRequest joinGameRequest)
            throws PlayerValidationException, AddressNotFoundException, ImageNotFoundException, PlayerProfileNotFoundException;

    /**
     * Get specified player.
     *
     * @param playerId player id
     * @return player
     * @throws PlayerNotFoundException if specified player is not in game
     */
    Player getPlayer(Integer playerId) throws PlayerNotFoundException;

    /**
     * Updates specified player.
     *
     * @param playerId        player id
     * @param joinGameRequest player information
     * @return updated player
     * @throws PlayerNotFoundException        if player is not found
     * @throws AddressNotFoundException       if address is not found
     * @throws ImageNotFoundException         if image is not found
     * @throws PlayerProfileNotFoundException if the specified player profile is not found
     */
    Player updatePlayer(Integer playerId, JoinGameRequest joinGameRequest)
            throws PlayerNotFoundException, AddressNotFoundException, ImageNotFoundException, PlayerProfileNotFoundException;

    /**
     * Whether game is published.
     *
     * @return whether game is published
     */
    boolean isPublished();

}
