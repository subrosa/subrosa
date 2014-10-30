package com.subrosagames.subrosa.api.dto;

import com.google.common.base.Optional;

/**
 * Encapsulates the information necessary to create and update an account player profile.
 */
public class PlayerProfileDescriptor {

    private Optional<String> name;
    private Optional<Integer> image;

    public Optional<String> getName() {
        return name;
    }

    public void setName(Optional<String> name) {
        this.name = name;
    }

    public Optional<Integer> getImage() {
        return image;
    }

    public void setImage(Optional<Integer> image) {
        this.image = image;
    }
}
