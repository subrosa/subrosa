package com.subrosagames.subrosa.domain.player;

import java.util.Collection;
import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.game.Participant;
import com.subrosagames.subrosa.domain.game.Target;
import com.subrosagames.subrosa.domain.game.TargetNotFoundException;
import com.subrosagames.subrosa.domain.game.persistence.TargetEntity;
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

    @Override
    public Target getTarget(final int targetId) throws TargetNotFoundException {
        List<? extends Target> targets = getTargets();
        Collection<? extends Target> filtered = Collections2.filter(targets, new Predicate<Target>() {
            @Override
            public boolean apply(Target input) {
                return targetId == input.getId();
            }
        });
        if (filtered.size() == 1) {
            return filtered.iterator().next();
        }
        throw new TargetNotFoundException("Target with id " + targetId + " not found for player id " + getId());
    }
}
