package com.subrosagames.subrosa.event;

/**
 * Created with IntelliJ IDEA.
 * User: jgore
 * Date: 12/4/12
 * Time: 12:40 午後
 * To change this template use File | Settings | File Templates.
 */
public class EventException extends Exception {

    private static final long serialVersionUID = 1301604863966351742L;

    public EventException() {
    }

    public EventException(String s) {
        super(s);
    }

    public EventException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public EventException(Throwable throwable) {
        super(throwable);
    }
}
