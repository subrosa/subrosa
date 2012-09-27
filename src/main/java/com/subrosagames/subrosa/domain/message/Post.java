package com.subrosagames.subrosa.domain.message;

import java.util.Date;
import java.util.List;

import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.game.AssassinsGame;
import com.subrosagames.subrosa.domain.game.event.GameEvent;

/**
 * Created with IntelliJ IDEA.
 * User: jgore
 * Date: 7/17/12
 * Time: 8:55 午後
 * To change this template use File | Settings | File Templates.
 */
public class Post {

    private AssassinsGame game;
    private Account account;
    private GameEvent event;
    private String body;
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

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
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
