package com.subrosagames.subrosa.domain.game;

import com.subrosagames.subrosa.domain.image.Image;
import com.subrosagames.subrosa.domain.message.Post;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *
 */
public interface Game {

    @JsonIgnore
    List<Post> getPosts();

    int getId();

    String getName();

    String getDescription();

    GameType getGameType();

    BigDecimal getPrice();

    Date getStartTime();

    Date getEndTime();

    Date getRegistrationStartTime();

    Date getRegistrationEndTime();

    String getTimezone();

    String getPassword();

    Image getImage();

    Integer getMinimumAge();

    Integer getMaximumTeamSize();
}
