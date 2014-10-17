package com.subrosagames.subrosa.domain.game.support.assassin;

import com.subrosagames.subrosa.domain.game.GameAttributeType;
import com.subrosagames.subrosa.domain.game.GameAttributeValue;

/**
 * Enumeration of assassin game attribute types.
 *
 * @see AssassinGame
 */
public enum AssassinGameAttributeType implements GameAttributeType {

    /**
     * Ordnance used in the game.
     */
    ORDNANCE_TYPE(OrdnanceType.class);

    private final Class<? extends GameAttributeValue> enumType;

    AssassinGameAttributeType(Class<? extends GameAttributeValue> enumType) {
        this.enumType = enumType;
    }

    public Class<? extends GameAttributeValue> getEnumType() {
        return enumType;
    }
}
