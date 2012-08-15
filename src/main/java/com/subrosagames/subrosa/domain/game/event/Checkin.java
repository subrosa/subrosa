package com.subrosagames.subrosa.domain.game.event;

import com.subrosagames.subrosa.domain.game.Player;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: jgore
 * Date: 7/17/12
 * Time: 7:47 午後
 * To change this template use File | Settings | File Templates.
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
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
