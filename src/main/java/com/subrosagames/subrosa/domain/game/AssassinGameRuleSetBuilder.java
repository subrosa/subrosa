package com.subrosagames.subrosa.domain.game;

import com.subrosagames.subrosa.event.Event;
import com.subrosagames.subrosa.event.ScheduledEvent;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jgore
 * Date: 10/3/12
 * Time: 1:09 午後
 * To change this template use File | Settings | File Templates.
 */
public class AssassinGameRuleSetBuilder implements GameRuleSetBuilder {

    ScheduledEvent registrationStart;
    ScheduledEvent registrationEnd;
    ScheduledEvent gameStart;
    ScheduledEvent gameEnd;
    List<ScheduledEvent> scheduledEvents;

    private Date registrationStartTime;

    public AssassinGameRuleSetBuilder setRegistrationStartTime(Date date) {
        this.registrationStartTime = registrationStartTime;
        return this;
    }

    public AssassinGameRuleSetBuilder setRegistrationEndTime(Date date) {
        this.registrationStartTime = registrationStartTime;
        return this;
    }

    public AssassinGameRuleSetBuilder setGameStartTime(Date date) {
        this.registrationStartTime = registrationStartTime;
        return this;
    }

    public AssassinGameRuleSetBuilder setGameEndTime(Date date) {
        this.registrationStartTime = registrationStartTime;
        return this;
    }

    @Override
    public GameRuleSetBuilder setProperty(String key, String value) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public GameRuleSetBuilder addEvent(Event event) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public GameRuleSet build() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
