package com.subrosagames.subrosa.domain.game;

import java.util.List;
import java.util.Map;

import com.subrosagames.subrosa.api.dto.PlayerDescriptor;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.game.event.GameEvent;
import com.subrosagames.subrosa.domain.game.persistence.PostEntity;
import com.subrosagames.subrosa.domain.location.Zone;
import com.subrosagames.subrosa.domain.message.Post;
import com.subrosagames.subrosa.domain.player.Player;
import com.subrosagames.subrosa.domain.player.PlayerValidationException;
import com.subrosagames.subrosa.domain.player.TargetNotFoundException;
import com.subrosagames.subrosa.event.Event;
import com.subrosagames.subrosa.event.TriggeredEvent;
import com.subrosagames.subrosa.event.message.EventMessage;

/**
 * The minimum information shared by all games.
 */
public interface Game extends GameData {

    /**
     * Get owner.
     * @return owner
     */
    Account getOwner();

    /**
     * Get the game rules.
     * @return a categorized list of rules
     */
    Map<RuleType, List<String>> getRules();

    /**
     * Handle a player's achievement of one of their targets.
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
     * @param account account
     * @param playerDescriptor
     * @return game player
     */
    Player addUserAsPlayer(Account account, PlayerDescriptor playerDescriptor) throws PlayerValidationException;

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
     * @param attributeType  attribute type
     * @param attributeValue attribute value
     */
    void setAttribute(Enum<? extends GameAttributeType> attributeType, Enum<? extends GameAttributeValue> attributeValue);

    void addTriggeredEvent(EventMessage eventType, Event trigger);

    List<TriggeredEvent> getEventsTriggeredBy(EventMessage eventMessage);

    List<GameEvent> getHistory();

    /**
     * Posts in the game feed.
     * @return list of posts
     */
    List<Post> getPosts();

    List<Zone> getZones();

    Lifecycle getLifecycle();

    Game create() throws GameValidationException;

    Game update(Game game) throws GameValidationException;

    Game publish() throws GameValidationException;

    Post addPost(PostEntity postEntity);
}
