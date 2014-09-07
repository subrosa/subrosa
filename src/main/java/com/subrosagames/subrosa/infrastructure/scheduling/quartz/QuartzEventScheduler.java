package com.subrosagames.subrosa.infrastructure.scheduling.quartz;

import java.util.Collection;
import java.util.Date;

import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import org.springframework.stereotype.Component;
import com.subrosagames.subrosa.event.EventException;
import com.subrosagames.subrosa.event.EventExecutor;
import com.subrosagames.subrosa.event.EventScheduler;
import com.subrosagames.subrosa.event.ScheduledEvent;
import com.subrosagames.subrosa.event.TriggeredEvent;
import com.subrosagames.subrosa.event.message.EventMessage;


/**
 * Schedules game events using Quartz.
 *
 * {@link ScheduledEvent}s and {@link TriggeredEvent}s are scheduled using
 * Quartz's {@link SchedulerFactoryBean}.
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
        JobDetail jobDetail = createJobDetail(EventMessage.valueOf(event.getEvent()), gameId);
        try {
            schedulerFactoryBean.getScheduler().triggerJob(jobDetail.getKey(), jobDetail.getJobDataMap());
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
        SimpleTrigger trigger = createTrigger(jobDetail, eventDate);
        try {
            schedulerFactoryBean.getScheduler().scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            throw new EventException("Failed to schedule event of type " + eventMessage + " at date " + eventDate, e);
        }
    }

    @Override
    public void scheduleEvent(ScheduledEvent event, int gameId) throws EventException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Scheduling event type {} for game {} at date {}", new Object[] { event.getClass(), gameId, event.getDate() });
        }
        JobDetail jobDetail = createJobDetail(EventMessage.valueOf(event.getEvent()), gameId);
        SimpleTrigger trigger = createTrigger(jobDetail, event.getDate());
        try {
            schedulerFactoryBean.getScheduler().scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            throw new EventException("Failed to schedule event of type " + event.getClass() + " at date " + event.getDate(), e);
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

    private SimpleTrigger createTrigger(JobDetail jobDetail, Date eventDate) {
        SimpleTriggerFactoryBean factory = new SimpleTriggerFactoryBean();
        factory.setBeanName("trigger-" + jobDetail.getKey().getName());
        factory.setJobDetail(jobDetail);
        factory.setRepeatCount(0);
        factory.setStartTime(eventDate);
        factory.afterPropertiesSet();
        return factory.getObject();
    }

}
