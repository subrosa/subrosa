package com.subrosa.vegas;

import com.subrosa.event.EventCallback;
import com.subrosa.event.GameEvent;

import java.util.Date;

public class RandomWinnerGameEndEvent implements GameEvent {

    public Date getTime() {
        return new Date();
    }

    public EventCallback getEvent() {
        return new RandomWinnerGameEndEventCallback();
    }
}
