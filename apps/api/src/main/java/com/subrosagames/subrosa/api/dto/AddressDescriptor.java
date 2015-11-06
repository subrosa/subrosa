package com.subrosagames.subrosa.api.dto;

import java.util.Optional;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Encapsulates the necessary information to create or update an address.
 */
@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class AddressDescriptor {

    private Optional<String> fullAddress;
    private Optional<String> label;
}
