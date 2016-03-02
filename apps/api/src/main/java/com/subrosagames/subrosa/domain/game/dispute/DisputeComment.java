package com.subrosagames.subrosa.domain.game.dispute;

import java.util.Date;

import com.subrosagames.subrosa.domain.player.Player;

/**
 * Represents a comment on a {@link Dispute}.
 */
public class DisputeComment {

    private Player author;
    private Date timestamp;

    public Player getAuthor() {
        return author;
    }

    public void setAuthor(Player author) {
        this.author = author;
    }

    public Date getTimestamp() {
        return timestamp == null ? null : new Date(timestamp.getTime());
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp == null ? null : new Date(timestamp.getTime());
    }

}
