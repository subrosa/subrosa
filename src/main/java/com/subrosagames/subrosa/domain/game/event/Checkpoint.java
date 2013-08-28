package com.subrosagames.subrosa.domain.game.event;

import java.util.Date;

import com.subrosagames.subrosa.domain.game.assassins.AssassinGame;

/**
 * Used to represent checkpoints which require players to register a {@link Checkin} in time.
 */
public class Checkpoint {

    private AssassinGame game;
    private Date startTime;
    private Date endTime;
    private String secret;
    private String message;

    public AssassinGame getGame() {
        return game;
    }

    public void setGame(AssassinGame game) {
        this.game = game;
    }

    public Date getStartTime() {
        return startTime == null ? null : new Date(startTime.getTime());
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime == null ? null : new Date(startTime.getTime());
    }

    public Date getEndTime() {
        return endTime == null ? null : new Date(endTime.getTime());
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime == null ? null : new Date(endTime.getTime());
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
