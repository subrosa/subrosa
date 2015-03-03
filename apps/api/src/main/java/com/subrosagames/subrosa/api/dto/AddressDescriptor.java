package com.subrosagames.subrosa.api.dto;

import com.google.common.base.Optional;

/**
 * Encapsulates the necessary information to create or update an address.
 */
public class AddressDescriptor {

    private Optional<String> fullAddress;
    private Optional<String> label;

    public void setFullAddress(Optional<String> fullAddress) {
        this.fullAddress = fullAddress;
    }

    public Optional<String> getFullAddress() {
        return fullAddress;
    }

    public Optional<String> getLabel() {
        return label;
    }

    public void setLabel(Optional<String> label) {
        this.label = label;
    }
}
