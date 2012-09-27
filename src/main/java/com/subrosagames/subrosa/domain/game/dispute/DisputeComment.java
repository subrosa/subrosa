package com.subrosagames.subrosa.domain.game.dispute;

import java.util.Date;
import com.subrosagames.subrosa.domain.game.Player;

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
