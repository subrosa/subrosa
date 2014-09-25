package com.subrosagames.subrosa.api.dto;

import java.util.Date;

import com.google.common.base.Optional;

/**
 * Encapsulates the necessary information to create or update a game event.
 */
public class GameEventDescriptor {

    private Integer id;
    private Optional<String> event;
    private Optional<Date> date;
    private Optional<Integer> trigger;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Optional<String> getEvent() {
        return event;
    }

    public void setEvent(Optional<String> event) {
        this.event = event;
    }

    public Optional<Date> getDate() {
        return date;
    }

    public void setDate(Optional<Date> date) {
        this.date = date;
    }

    public Optional<Integer> getTrigger() {
        return trigger;
    }

    public void setTrigger(Optional<Integer> trigger) {
        this.trigger = trigger;
    }
}

