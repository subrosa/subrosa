package com.subrosagames.subrosa.domain.account;

import com.subrosagames.subrosa.domain.DomainObjectNotFoundException;

/**
 * Exception thrown when a nonexistent address is retrieved.
 */
public class AddressNotFoundException extends DomainObjectNotFoundException {


    private static final long serialVersionUID = -5699735151588930710L;

    /**
     * Default constructor.
     */
    public AddressNotFoundException() {
    }

    /**
     * Construct with message.
     * @param s message
     */
    public AddressNotFoundException(String s) {
        super(s);
    }

    /**
     * Construct with message and cause.
     * @param s message
     * @param throwable cause
     */
    public AddressNotFoundException(String s, Throwable throwable) {
        super(s, throwable);
    }

    /**
     * Construct with cause.
     * @param throwable cause
     */
    public AddressNotFoundException(Throwable throwable) {
        super(throwable);
    }
}
