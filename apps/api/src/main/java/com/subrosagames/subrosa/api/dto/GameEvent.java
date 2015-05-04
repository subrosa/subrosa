package com.subrosagames.subrosa.api.dto;

import java.util.Date;

/**
 * Game event.
 */
public class GameEvent {

    private String type;
    private Date date;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getDate() {
        return date == null ? null : new Date(date.getTime());
    }

    public void setDate(Date date) {
        this.date = date == null ? null : new Date(date.getTime());
    }
}
