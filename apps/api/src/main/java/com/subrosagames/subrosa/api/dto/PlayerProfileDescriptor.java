package com.subrosagames.subrosa.api.dto;

import java.util.Optional;

import lombok.Data;

/**
 * Encapsulates the information necessary to create and update an account player profile.
 */
@Data
public class PlayerProfileDescriptor {

    private Optional<String> name;
    private Optional<Integer> imageId;

}
