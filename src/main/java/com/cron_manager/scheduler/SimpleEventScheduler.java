package com.cron_manager.scheduler;

import com.cron_manager.manager.SpringContextDelegate;
import com.cron_manager.model.JobSchedule;
import com.cron_manager.queue.JobScheduleQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.cron_manager.queue.model.ScheduleEvent;

import java.util.List;
import java.util.UUID;

/**
 * Created by honcheng on 2015/4/22.
 */
public class SimpleEventScheduler implements Runnable {
    public static final Logger logger = LoggerFactory.getLogger(SimpleEventScheduler.class);

    ScheduleTime scheduleTime = new ScheduleTimeQuartz();
    JobScheduleQueue jobScheduleQueue;

    //mandatory
    String schedulerGroup;
    final String code = UUID.randomUUID().toString();

    public SimpleEventScheduler(String schedulerGroup) {
        this.schedulerGroup = schedulerGroup;
        jobScheduleQueue = SpringContextDelegate.getBean(JobScheduleQueue.class);
    }

    private boolean isNeedSchedule(ScheduleEvent scheduleEvent) {
        return scheduleEvent.getEventScheduleTime().getTime() - System.currentTimeMillis() < 1000;
    }

    public void register() throws Exception{
        //register the schedule group
        jobScheduleQueue.addScheduleGroup(schedulerGroup);
    }

    private void schedule() {
        while (!Thread.currentThread().isInterrupted()) {
            ScheduleEvent scheduleEvent = tryGetScheduleEvent();
            try {
                boolean successSchedule = false;
                if (scheduleEvent != null && isNeedSchedule(scheduleEvent)) {
                    successSchedule = process(scheduleEvent);
                    logger.info("processed schedule event:" + scheduleEvent.toString());
                }

                if (!successSchedule) {
                    break;
                }
            } finally {
                try {
                    if (scheduleEvent != null) {
                        jobScheduleQueue.releaseLockScheduleEvent(scheduleEvent, code);
                        logger.info("release lock:" + scheduleEvent.toString());
                    }
                } catch (Exception e) {
                    logger.error("fail to release lock:", e);
                }
            }
        }
    }

    private ScheduleEvent tryGetScheduleEvent() {
        int retried = 3;
        while (retried > 0 && !Thread.currentThread().isInterrupted()) {
            try {
                List<ScheduleEvent> scheduleEventList = jobScheduleQueue.topScheduleEvents(schedulerGroup);
                for (ScheduleEvent event : scheduleEventList) {
                    if (isNeedSchedule(event)) {
                        if (jobScheduleQueue.tryLockScheduleEvent(event, code)) {
                            logger.info("get schedule event:" + event.toString());
                            return event;
                        }
                    } else {
                        break;
                    }
                }
                break;
            } catch (Exception e) {
                logger.error("fail to get schedule event.", e);
                retried--;
            }
        }

        return null;
    }

    private boolean process(ScheduleEvent scheduleEvent) {
        boolean success = false;
        int retried = 3;
        while (retried > 0 && !Thread.currentThread().isInterrupted()) {
            try {
                ScheduleEventHandler eventHandler = ScheduleEventHandler.getScheduleEventHandler(schedulerGroup, scheduleEvent);

                eventHandler.handle();

                success = true;
                break;
            } catch (Exception e) {
                logger.error("fail schedule event:" + scheduleEvent.toString(), e);
                retried--;
            }
        }
        return success;
    }

    @Override
    public void run() {
        try {
            register();
        } catch (Exception e) {
            logger.error("simple event scheduler can not start.");
            return;
        }

        while (!Thread.currentThread().isInterrupted()) {
            schedule();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
