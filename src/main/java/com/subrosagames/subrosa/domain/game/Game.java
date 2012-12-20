package com.subrosagames.subrosa.domain.game;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;
import com.subrosagames.subrosa.domain.image.Image;
import com.subrosagames.subrosa.domain.message.Post;
import com.subrosagames.subrosa.domain.player.Player;

/**
 * The minimum information shared by all games.
 */
public interface Game {

    /**
     * Posts in the game feed.
     * @return list of posts
     */
    List<Post> getPosts();

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

    /**
     * Get the player associated with the provided account id.
     * @param accountId account id
     * @return player
     */
    Player getPlayer(int accountId);
}
