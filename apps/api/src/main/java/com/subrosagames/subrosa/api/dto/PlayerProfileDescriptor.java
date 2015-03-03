package com.subrosagames.subrosa.api.dto;

import com.google.common.base.Optional;

/**
 * Encapsulates the information necessary to create and update an account player profile.
 */
public class PlayerProfileDescriptor {

    private Optional<String> name;
    private Optional<Integer> imageId;

    public Optional<String> getName() {
        return name;
    }

    public void setName(Optional<String> name) {
        this.name = name;
    }

    public Optional<Integer> getImageId() {
        return imageId;
    }

    public void setImageId(Optional<Integer> imageId) {
        this.imageId = imageId;
    }
}
