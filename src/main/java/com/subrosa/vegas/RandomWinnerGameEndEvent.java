package com.subrosa.vegas;

import com.subrosa.event.EventCallback;
import com.subrosa.event.GameEvent;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Date;


public class RandomWinnerGameEndEvent implements GameEvent {

    private Date time = new Date();
    private EventCallback event = new RandomWinnerGameEndEventCallback();

    public Date getTime() {
        return time;
    }

    @JsonIgnore
    public EventCallback getEvent() {
        return event;
    }
}
