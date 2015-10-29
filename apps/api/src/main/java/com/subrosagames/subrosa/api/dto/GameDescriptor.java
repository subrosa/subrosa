package com.subrosagames.subrosa.api.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.subrosagames.subrosa.domain.game.GameStatus;
import com.subrosagames.subrosa.domain.game.GameType;
import lombok.Data;

/**
 * Encapsulates the necessary information to create or update a game.
 */
@Data
public class GameDescriptor {

    private Integer id;
    private Optional<String> name;
    private Optional<String> url;
    private Optional<String> description;
    private Optional<GameType> gameType;
    private Optional<BigDecimal> price;
    private Optional<String> timezone;
    private Optional<Integer> maximumTeamSize;
    private Optional<String> password;
    private Optional<Integer> imageId;
    private Optional<List<EnrollmentFieldDto>> playerInfo;
    private Optional<Date> gameStart;
    private Optional<Date> gameEnd;
    private Optional<Date> registrationStart;
    private Optional<Date> registrationEnd;
    private Optional<GameStatus> status;

}
