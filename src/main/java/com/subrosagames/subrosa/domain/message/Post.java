package com.subrosagames.subrosa.domain.message;

import java.util.Date;
import java.util.List;

import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.game.AssassinsGame;
import com.subrosagames.subrosa.domain.game.event.GameEvent;

/**
 * Encapsulates a user post in a game.
 */
public class Post {

    private AssassinsGame game;
    private Account account;
    private GameEvent event;
    private String content;
    private List<Comment> comments;
    private Date timestamp;

    public AssassinsGame getGame() {
        return game;
    }

    public void setGame(AssassinsGame game) {
        this.game = game;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public GameEvent getEvent() {
        return event;
    }

    public void setEvent(GameEvent event) {
        this.event = event;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
