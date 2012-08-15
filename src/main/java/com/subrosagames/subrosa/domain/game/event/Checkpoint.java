package com.subrosagames.subrosa.domain.game.event;

import com.subrosagames.subrosa.domain.game.Game;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: jgore
 * Date: 7/17/12
 * Time: 7:46 午後
 * To change this template use File | Settings | File Templates.
 */
public class Checkpoint {

    private Game game;
    private Date startTime;
    private Date endTime;
    private String secret;
    private String message;

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
