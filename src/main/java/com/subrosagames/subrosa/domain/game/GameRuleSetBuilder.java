package com.subrosagames.subrosa.domain.game;

import com.subrosagames.subrosa.event.Event;

/**
 * Created with IntelliJ IDEA.
 * User: jgore
 * Date: 10/3/12
 * Time: 1:08 午後
 * To change this template use File | Settings | File Templates.
 */
public interface GameRuleSetBuilder {

    GameRuleSetBuilder setProperty(String key, String value);
    GameRuleSetBuilder addEvent(Event event);
    GameRuleSet build();
}
