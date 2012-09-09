package com.subrosa.web;

import com.subrosa.event.EventCallback;
import com.subrosa.event.GameEvent;
import com.subrosa.event.TaskScheduler;
import com.subrosagames.subrosa.service.GameController;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

//@Controller
public class SchedulingController {

    private static final Logger LOG = LoggerFactory.getLogger(GameController.class);

    @Autowired
    private TaskScheduler taskScheduler;
    
    @RequestMapping("doIt")
    public void scheduleSomething() throws SchedulerException, ClassNotFoundException, NoSuchMethodException {
        LOG.error("hey");
        GameEvent gameEvent = new GameEvent() {
            @Override
            public Date getTime() {
                return new Date();
            }

            @Override
            public EventCallback getEvent() {
                return new EventCallback() {
                    @Override
                    public void handle() {
                        LOG.error("we're doing this!");
                    }
                };
            }
        };
        taskScheduler.scheduleTask(gameEvent);
    }
}
