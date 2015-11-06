package com.subrosagames.subrosa.domain.player.persistence;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Simple text-based player attribute.
 */
@Entity
@DiscriminatorValue(PlayerAttributeText.ATTRIBUTE_TYPE)
public class PlayerAttributeText extends PlayerAttribute {

    /**
     * Indicates address attribute type.
     */
    public static final String ATTRIBUTE_TYPE = "TEXT";

}
