package com.subrosagames.subrosa.infrastructure.scheduling.quartz;

import com.subrosagames.subrosa.event.*;
import com.subrosagames.subrosa.event.message.EventMessage;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOG = LoggerFactory.getLogger(QuartzEventScheduler.class);

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    @Autowired
    private EventExecutor eventExecutor;

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
    public void scheduleEvent(EventMessage eventMessage, Date eventDate, int gameId) throws EventException {
        LOG.debug("Scheduling event {} for game {} for date {}", new Object[] { eventMessage.name(), gameId, eventDate});
        JobDetail jobDetail = createJobDetail(eventMessage, gameId);
        SimpleTriggerBean trigger = createTrigger(jobDetail, eventDate);
        try {
            schedulerFactoryBean.getScheduler().scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            throw new EventException("Failed to schedule event of type " + eventMessage + " at date " + eventDate, e);
        }
    }

    private JobDetail createJobDetail(EventMessage eventMessage, int gameId) {
        MethodInvokingJobDetailFactoryBean jobDetailFactory = new MethodInvokingJobDetailFactoryBean();
        jobDetailFactory.setName("event-" + gameId + "-" + eventMessage);
        jobDetailFactory.setTargetObject(eventExecutor);
        jobDetailFactory.setTargetMethod(EventExecutor.EXECUTE_METHOD);
        jobDetailFactory.setArguments(new Object[]{ eventMessage.name(), gameId });
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

    @Override
    public void scheduleEvent(ScheduledEvent event, int gameId) throws EventException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Scheduling event type {} for game {} at date {}", new Object[] { event.getClass(), gameId, event.getEventDate() });
        }
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
        trigger.setBeanName("trigger-" + jobDetail.getName());
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
