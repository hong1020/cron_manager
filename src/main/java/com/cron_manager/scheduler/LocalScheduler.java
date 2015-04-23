package com.cron_manager.scheduler;

import com.cron_manager.manager.SpringContextDelegate;
import com.cron_manager.model.JobSchedule;
import com.cron_manager.queue.JobScheduleQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by honcheng on 2015/4/20.
 */
@Deprecated
public class LocalScheduler {
//    public static final Logger logger = LoggerFactory.getLogger(LocalScheduler.class);
//
//    PriorityQueue<ScheduleEvent> queue = new PriorityQueue<ScheduleEvent>();
//    Set<Long> pulledJobScheduleIdSet = new HashSet<Long>();
//    ScheduleTime scheduleTime = new ScheduleTimeQuartz();
//
//    JobScheduleQueue jobScheduleQueue;
//
//    //mandatory
//    String schedulerGroup;
//
//    public LocalScheduler(String schedulerGroup) {
//        this.schedulerGroup = schedulerGroup;
//        jobScheduleQueue = SpringContextDelegate.getBean(JobScheduleQueue.class);
//    }
//
//    private boolean isNeedSchedule(ScheduleEvent scheduleEvent) {
//        return scheduleEvent.getScheduleTime().getTime() - System.currentTimeMillis() < 1000;
//    }
//
//    private void schedule() {
//        //from the schedule queue
//        int retried = 3;
//        while (retried > 0 && !Thread.currentThread().isInterrupted()) {
//            try {
//                JobSchedule jobSchedule = jobScheduleQueue.offerSchedule(schedulerGroup);
//                if (jobSchedule == null) break;
//
//                //local operation, idempotence
//                if (!pulledJobScheduleIdSet.contains(jobSchedule.getId())) {
//                    pulledJobScheduleIdSet.add(jobSchedule.getId());
//                    ScheduleEventJobSchedule scheduleEvent = new ScheduleEventJobSchedule(
//                            jobSchedule,
//                            ScheduleEventJobScheduleEventType.SCHEDULE,
//                            new Date(jobSchedule.getSchedule_datetime().getTime() - 1000));
//                    queue.add(scheduleEvent);
//                    logger.info("pulled schedule: " + jobSchedule.getId());
//                }
//
//                jobScheduleQueue.removeSchedule(schedulerGroup, jobSchedule);
//
//            } catch (Exception e) {
//                logger.error("get job schedule queue failed for " + schedulerGroup);
//                retried--;
//            }
//        }
//
//        //from the local queue
//        while (!Thread.currentThread().isInterrupted()) {
//            ScheduleEvent scheduleEvent = queue.peek();
//            boolean successSchedule = false;
//
//            if (scheduleEvent != null && isNeedSchedule(scheduleEvent)) {
//                retried = 3;
//                while (retried > 0 && !Thread.currentThread().isInterrupted()) {
//                    try {
//                        scheduleEvent.handle();
//
//                        List<ScheduleEvent> nextScheduleEventList = scheduleEvent.getNexScheduleEvent();
//                        if (nextScheduleEventList != null) {
//                            for (ScheduleEvent event : nextScheduleEventList) {
//                                queue.add(event);
//                            }
//                        }
//                        queue.poll();
//                        successSchedule = true;
//                        break;
//                    } catch (Exception e) {
//                        logger.error("fail schedule event:" + scheduleEvent.getScheduleTime(), e);
//                        retried--;
//                    }
//                }
//            }
//
//            if (!successSchedule) {
//                break;
//            }
//        }
//    }
//
//    public void register() throws Exception{
//        //register the schedule group
//        jobScheduleQueue.addScheduleGroup(schedulerGroup);
//    }
//
//    @Override
//    public void run() {
//        try {
//            register();
//        } catch (Exception e) {
//            logger.error("simple scheduler can not start.");
//            return;
//        }
//
//        while (!Thread.currentThread().isInterrupted()) {
//            schedule();
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            }
//        }
//    }
//
//    public String printState() {
//        StringBuilder builder = new StringBuilder();
//        builder.append("current queue state:");
//        for (ScheduleEvent event : queue) {
//            builder.append(event.toString());
//            builder.append(";");
//        }
//        return builder.toString();
//    }
}
