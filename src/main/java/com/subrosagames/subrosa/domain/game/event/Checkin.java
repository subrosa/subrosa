package com.subrosagames.subrosa.domain.game.event;

import java.util.Date;

import com.subrosagames.subrosa.domain.game.Player;

/**
 * Represents a player checking in to a {@link Checkpoint}.
 */
public class Checkin {

    private Player player;
    private Checkpoint checkpoint;
    private Date timestamp;

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Checkpoint getCheckpoint() {
        return checkpoint;
    }

    public void setCheckpoint(Checkpoint checkpoint) {
        this.checkpoint = checkpoint;
    }

    public Date getTimestamp() {
        return timestamp == null ? null : new Date(timestamp.getTime());
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp == null ? null : new Date(timestamp.getTime());
    }
}
