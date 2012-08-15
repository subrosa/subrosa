package com.subrosa.event;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerBean;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: jgore
 * Date: 5/17/12
 * Time: 7:42 午後
 * To change this template use File | Settings | File Templates.
 */
@Component
public class TaskScheduler {

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    public void scheduleTask(GameEvent event) throws ClassNotFoundException, NoSuchMethodException, SchedulerException {
        MethodInvokingJobDetailFactoryBean jobDetail = new MethodInvokingJobDetailFactoryBean();
        jobDetail.setTargetObject(event.getEvent());
        jobDetail.setTargetMethod("handle");
        jobDetail.setName("blah");
        jobDetail.afterPropertiesSet();

        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        SimpleTriggerBean trigger = new SimpleTriggerBean();
        trigger.setJobDetail(jobDetail.getObject());
        trigger.setName("blah");
        trigger.setRepeatCount(0);
        trigger.setStartTime(new Date(new Date().getTime() + 1000));

        scheduler.scheduleJob(jobDetail.getObject(), trigger);
    }
}
