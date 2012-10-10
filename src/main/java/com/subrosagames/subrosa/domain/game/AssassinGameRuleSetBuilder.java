package com.subrosagames.subrosa.domain.game;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: jgore
 * Date: 10/3/12
 * Time: 1:09 午後
 * To change this template use File | Settings | File Templates.
 */
public class AssassinGameRuleSetBuilder implements GameRuleSetBuilder {

    private Date registrationStartTime;

    public AssassinGameRuleSetBuilder setRegistrationStartTime(Date registrationStartTime) {
        this.registrationStartTime = registrationStartTime;
        return this;
    }

    @Override
    public GameRuleSet build() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
