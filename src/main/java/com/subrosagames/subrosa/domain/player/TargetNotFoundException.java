package com.subrosagames.subrosa.domain.player;

/**
 *
 */
public class TargetNotFoundException extends Exception {

    private static final long serialVersionUID = -7956180785472216269L;

    public TargetNotFoundException() {
    }

    public TargetNotFoundException(String s) {
        super(s);
    }

    public TargetNotFoundException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public TargetNotFoundException(Throwable throwable) {
        super(throwable);
    }
}
