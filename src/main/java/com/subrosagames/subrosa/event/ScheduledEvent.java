package com.subrosagames.subrosa.event;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: jgore
 * Date: 12/4/12
 * Time: 12:14 午後
 * To change this template use File | Settings | File Templates.
 */
public interface ScheduledEvent extends Event {

    Date getEventDate();
}
