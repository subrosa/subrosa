package org.eludo.subrosa.event;

import java.util.Date;

public interface GameEvent {

    public Date getTime();

    public EventCallback getEvent();

}
