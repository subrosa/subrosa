package com.subrosagames.subrosa.domain.game;

/**
 * Model for Players.
 */
public class Player implements Participant {

    private String name;

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {

        this.name = name;
    }
}
