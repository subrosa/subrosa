package com.subrosagames.subrosa.domain.player;

/**
 * A player that can be eliminated.
 */
public interface Killable {

    /**
     * Code for player elimination.
     *
     * @return kill code
     */
    String getKillCode();
}
