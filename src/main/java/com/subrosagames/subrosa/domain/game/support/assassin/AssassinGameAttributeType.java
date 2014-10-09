package com.subrosagames.subrosa.domain.game.support.assassin;

import com.subrosagames.subrosa.domain.game.GameAttributeType;
import com.subrosagames.subrosa.domain.game.GameAttributeValue;

/**
 *
 */
public enum AssassinGameAttributeType implements GameAttributeType {

    ORDNANCE_TYPE(OrdnanceType.class);

    private final Class<? extends GameAttributeValue> enumType;

    AssassinGameAttributeType(Class<? extends GameAttributeValue> enumType) {
        this.enumType = enumType;
    }

}
