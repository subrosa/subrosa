package com.subrosagames.subrosa.domain.player;

import java.util.Map;

import com.subrosagames.subrosa.api.dto.PlayerDescriptor;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.account.AddressNotFoundException;
import com.subrosagames.subrosa.domain.account.PlayerProfile;
import com.subrosagames.subrosa.domain.image.Image;
import com.subrosagames.subrosa.domain.image.ImageNotFoundException;
import com.subrosagames.subrosa.domain.player.persistence.PlayerAttribute;

/**
 * Model for Players.
 */
public interface Player extends Participant {

    /**
     * Get player id.
     *
     * @return player id
     */
    Integer getId();

    /**
     * Get the owning account.
     *
     * @return account
     */
    Account getAccount();

    /**
     * Get the owning account's player profile used for the game player.
     *
     * @return player profile
     */
    PlayerProfile getPlayerProfile();

    default String getName() {
        return getPlayerProfile().getName();
    }

    default Image getAvatar() {
        return getPlayerProfile().getImage();
    }

    /**
     * Get player attributes.
     *
     * @return player attributes
     */
    Map<String, PlayerAttribute> getAttributes();

    /**
     * Updates player with given information.
     *
     * @param playerDescriptor player information
     * @throws AddressNotFoundException if address is not found
     * @throws ImageNotFoundException   if image is not found
     */
    void update(PlayerDescriptor playerDescriptor) throws AddressNotFoundException, ImageNotFoundException;
}
