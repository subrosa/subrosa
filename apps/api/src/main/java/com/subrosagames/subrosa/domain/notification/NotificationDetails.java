package com.subrosagames.subrosa.domain.notification;

/**
 * Specifies the details for notification.
 */
public class NotificationDetails {

    private int gameId;

    private NotificationCode code;

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public NotificationCode getCode() {
        return code;
    }

    public void setCode(NotificationCode code) {
        this.code = code;
    }
}
