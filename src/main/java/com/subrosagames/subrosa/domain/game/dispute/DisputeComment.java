package com.subrosagames.subrosa.domain.game.dispute;

import com.subrosagames.subrosa.domain.game.Player;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: jgore
 * Date: 7/17/12
 * Time: 8:00 午後
 * To change this template use File | Settings | File Templates.
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
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
