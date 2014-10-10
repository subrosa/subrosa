package com.subrosagames.subrosa.domain.game;

import java.math.BigDecimal;
import java.util.Date;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import com.subrosagames.subrosa.domain.game.validation.PublishAction;
import com.subrosagames.subrosa.domain.image.Image;
import com.subrosagames.subrosa.domain.validation.constraints.DateRange;

/**
 * Defines the attributes associated with a game along with their validation constraints.
 */
@DateRange.List({
        @DateRange(start = "gameStart", end = "gameEnd", message = "startBeforeEnd"),
        @DateRange(start = "registrationStart", end = "registrationEnd", message = "startBeforeEnd"),
        @DateRange(start = "registrationEnd", end = "gameStart", allowEmptyRange = true)
})
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
    @NotBlank
    String getName();

    /**
     * Game url.
     * @return game url
     */
    @NotBlank
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
    @NotNull
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
    @Future(groups = PublishAction.class)
    @NotNull(groups = PublishAction.class)
    Date getGameStart();

    /**
     * Game end time.
     * @return game end time
     */
    @Future(groups = PublishAction.class)
    @NotNull(groups = PublishAction.class)
    Date getGameEnd();

    /**
     * Registration start time.
     * @return registration start time
     */
    @NotNull(groups = PublishAction.class)
    Date getRegistrationStart();

    /**
     * Registration end time.
     * @return registration end time
     */
    @Future(groups = PublishAction.class)
    @NotNull(groups = PublishAction.class)
    Date getRegistrationEnd();

    /**
     * Status of game.
     * @return game status
     */
    GameStatus getStatus();
}

