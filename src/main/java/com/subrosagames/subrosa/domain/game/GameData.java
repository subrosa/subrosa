package com.subrosagames.subrosa.domain.game;

import java.math.BigDecimal;
import java.util.Date;

import com.subrosagames.subrosa.domain.image.Image;

/**
 */
public interface GameData {

    /**
     * Game id.
     * @return game id
     */
    Integer getId();

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
}
