package com.subrosagames.subrosa.domain.player;

import java.util.Set;
import javax.validation.ConstraintViolation;

import com.subrosagames.subrosa.domain.DomainObjectValidationException;
import com.subrosagames.subrosa.domain.player.persistence.PlayerEntity;

/**
 */
public class PlayerValidationException extends DomainObjectValidationException {

    public PlayerValidationException() {
    }

    public PlayerValidationException(String s) {
        super(s);
    }

    public PlayerValidationException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public PlayerValidationException(Throwable throwable) {
        super(throwable);
    }

    public PlayerValidationException(Set<ConstraintViolation<PlayerEntity>> violations) {
        super(violations);
    }
}
