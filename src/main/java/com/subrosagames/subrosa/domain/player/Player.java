package com.subrosagames.subrosa.domain.player;

import java.util.List;

import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.game.Participant;
import com.subrosagames.subrosa.domain.game.Target;
import com.subrosagames.subrosa.domain.player.persistence.PlayerEntity;

/**
 * Model for Players.
 */
public class Player implements Participant {

    private final PlayerEntity playerEntity;

    public Player(PlayerEntity playerEntity) {
        this.playerEntity = playerEntity;
    }

    public Integer getId() {
        return playerEntity.getId();
    }

    public Account getAccount() {
        return playerEntity.getAccount();
    }

    public String getName() {
        return playerEntity.getAccount().getUsername();
    }

    public List<? extends Target> getTargets() {
        return playerEntity.getTargets();
    }
}
