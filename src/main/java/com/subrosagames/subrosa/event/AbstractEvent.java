package com.subrosagames.subrosa.event;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jgore
 * Date: 12/4/12
 * Time: 1:36 午後
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractEvent implements Event {

    private List<TriggeredEvent> onSuccessEvents;
    private List<TriggeredEvent> onFailureEvents;

    @Override
    public List<TriggeredEvent> getOnSuccessEvents() {
        return onSuccessEvents;
    }

    @Override
    public List<TriggeredEvent> getOnFailureEvents() {
        return onFailureEvents;
    }

    public void setOnSuccessEvents(List<TriggeredEvent> onSuccessEvents) {
        this.onSuccessEvents = onSuccessEvents;
    }

    public void setOnFailureEvents(List<TriggeredEvent> onFailureEvents) {
        this.onFailureEvents = onFailureEvents;
    }
}
