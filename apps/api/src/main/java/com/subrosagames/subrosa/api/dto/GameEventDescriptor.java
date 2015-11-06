package com.subrosagames.subrosa.api.dto;

import java.util.Date;
import java.util.Optional;

import lombok.Data;

/**
 * Encapsulates the necessary information to create or update a game event.
 */
@Data
public class GameEventDescriptor {

    private Integer id;
    private Optional<String> event;
    private Optional<Date> date;
    private Optional<Integer> trigger;

}

