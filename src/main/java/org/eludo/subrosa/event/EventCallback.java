package org.eludo.subrosa.event;

import java.io.File;

/**
 * Callback functionality to be executed with the occurrence of an event.
 * <p/>
 * The event could be either time-based or triggered by application logic that
 * constitutes an event.
 * <p/>
 * Currently only time-based is supported.
 */
public interface EventCallback {

    /**
     * Perform necessary actions as a result of an event.
     * <p/>
     * This callback will be registered with the game event framework. The properties
     * are supplied in the form of a .properties file, provided at the time of event
     * registration.
     *
     * @param propertiesFile file with event properties
     */
    public void handle(File propertiesFile);
}
