package com.subrosagames.subrosa.event;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jgore
 * Date: 12/5/12
 * Time: 12:16 午後
 * To change this template use File | Settings | File Templates.
 */
public interface GameEventInitializer {

    void scheduleEvents(List<Event> events);
}
