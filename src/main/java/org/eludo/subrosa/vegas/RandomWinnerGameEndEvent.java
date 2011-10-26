package org.eludo.subrosa.vegas;

import org.eludo.subrosa.event.EventCallback;
import org.eludo.subrosa.event.GameEvent;

import java.util.Date;

public class RandomWinnerGameEndEvent implements GameEvent {

    public Date getTime() {
        return new Date();
    }

    public EventCallback getEvent() {
        return new RandomWinnerGameEndEventCallback();
    }
}
