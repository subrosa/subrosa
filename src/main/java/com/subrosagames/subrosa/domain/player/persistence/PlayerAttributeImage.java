package com.subrosagames.subrosa.domain.player.persistence;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonValue;
import com.subrosagames.subrosa.domain.image.Image;

/**
 * Persists a reference to an account image as a player attribute.
 */
@Entity
@Table(name = "player_attribute_image")
@DiscriminatorValue(PlayerAttributeImage.ATTRIBUTE_TYPE_IMAGE)
public class PlayerAttributeImage extends PlayerAttribute {

    /**
     * Indicates image attribute type.
     */
    public static final String ATTRIBUTE_TYPE_IMAGE = "IMAGE";

    @ManyToOne
    @JoinColumn(name = "value_ref_id")
    private Image image;

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    @JsonValue
    public Object getJsonValue() {
        return image;
    }

    public String getValueRef() {
        return image.getId().toString();
    }
}
