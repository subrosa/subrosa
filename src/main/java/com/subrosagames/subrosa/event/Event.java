package com.subrosagames.subrosa.event;

import java.util.List;

/**
 */
public interface Event {

    String FIRE_METHOD = "fire";

//    List<TriggeredEvent> getOnSuccessEvents();
//    List<TriggeredEvent> getOnFailureEvents();

    void fire(int gameId);

}
