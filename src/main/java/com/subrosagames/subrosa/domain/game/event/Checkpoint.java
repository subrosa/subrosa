package com.subrosagames.subrosa.domain.game.event;

import java.util.Date;

import com.subrosagames.subrosa.domain.game.assassins.AssassinsGame;

/**
 * Used to represent checkpoints which require players to register a {@link Checkin} in time.
 */
public class Checkpoint {

    private AssassinsGame game;
    private Date startTime;
    private Date endTime;
    private String secret;
    private String message;

    public AssassinsGame getGame() {
        return game;
    }

    public void setGame(AssassinsGame game) {
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
