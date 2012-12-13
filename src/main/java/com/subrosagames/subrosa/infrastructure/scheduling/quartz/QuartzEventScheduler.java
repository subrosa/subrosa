package com.subrosagames.subrosa.infrastructure.scheduling.quartz;

import com.subrosagames.subrosa.event.Event;
import com.subrosagames.subrosa.event.EventException;
import com.subrosagames.subrosa.event.EventScheduler;
import com.subrosagames.subrosa.event.ScheduledEvent;
import com.subrosagames.subrosa.event.TriggeredEvent;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerBean;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Collection;
import java.util.Date;

/**
 */
@Component
public class QuartzEventScheduler implements EventScheduler {

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    @Override
    public void triggerEvent(TriggeredEvent event, int gameId) throws EventException {
        JobDetail jobDetail = createJobDetail(event, gameId);
        try {
            schedulerFactoryBean.getScheduler().triggerJob(jobDetail.getName(), jobDetail.getGroup());
        } catch (SchedulerException e) {
            throw new EventException("Failed to immediately trigger event of type " + event.getClass(), e);
        }
    }

    @Override
    public void scheduleEvents(Collection<? extends ScheduledEvent> events, int gameId) throws EventException {
        for (ScheduledEvent event : events) {
            scheduleEvent(event, gameId);
        }
    }

    @Override
    public void scheduleEvent(ScheduledEvent event, int gameId) throws EventException {
        JobDetail jobDetail = createJobDetail(event, gameId);
        SimpleTriggerBean trigger = createTrigger(jobDetail, event.getEventDate());
        try {
            schedulerFactoryBean.getScheduler().scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            throw new EventException("Failed to schedule event of type " + event.getClass() + " at date " + event.getEventDate(), e);
        }
    }

    private JobDetail createJobDetail(Event event, int gameId) {
        MethodInvokingJobDetailFactoryBean jobDetailFactory = new MethodInvokingJobDetailFactoryBean();
        jobDetailFactory.setName("event");
        jobDetailFactory.setTargetObject(event);
        jobDetailFactory.setTargetMethod(Event.FIRE_METHOD);
        jobDetailFactory.setArguments(new Integer[] { gameId });
        jobDetailFactory.setConcurrent(false);
        try {
            jobDetailFactory.afterPropertiesSet();
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
        return jobDetailFactory.getObject();
    }

    private SimpleTriggerBean createTrigger(JobDetail jobDetail, Date eventDate) {
        SimpleTriggerBean trigger = new SimpleTriggerBean();
        trigger.setJobDetail(jobDetail);
        trigger.setRepeatCount(0);
        trigger.setStartTime(eventDate);
        try {
            trigger.afterPropertiesSet();
        } catch (ParseException e) {
            throw new IllegalStateException(e);
        }
        return trigger;
    }

}
