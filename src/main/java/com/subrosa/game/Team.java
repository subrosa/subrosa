package com.subrosa.game;

import java.util.List;

/**
 * Model for Teams.
 */
public class Team implements Participant {

    private String name;
    private List<? extends Player> members;

    public List<? extends Player> getMembers() {
        return members;
    }

    public void setMembers(List<? extends Player> members) {
        this.members = members;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
