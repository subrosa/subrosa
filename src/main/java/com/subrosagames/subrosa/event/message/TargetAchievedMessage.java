package com.subrosagames.subrosa.event.message;

import com.subrosagames.subrosa.domain.game.event.AbstractMessage;

/**
 *
 */
public class TargetAchievedMessage extends AbstractMessage {

    private int playerId;
    private int targetId;

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getTargetId() {
        return targetId;
    }

    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }
}
