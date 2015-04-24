package com.cron_manager.client.queue;

import com.cron_manager.manager.SpringContextDelegate;
import com.cron_manager.model.JobSchedule;
import com.cron_manager.queue.JobScheduleQueue;
import com.google.common.collect.Maps;
import com.sun.corba.se.impl.orbutil.closure.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.Future;

/**
 * Created by honcheng on 2015/4/23.
 */
public class SimpleJobScheduleClient implements Runnable{
    public static Logger logger = LoggerFactory.getLogger(SimpleJobScheduleClient.class);

    ExecutorService pollExecutor = Executors.newFixedThreadPool(2);
    ExecutorService executeExecutor = Executors.newFixedThreadPool(10);
    Map<Long, Future> executingMap = Maps.newConcurrentMap();

    //TODO
    String jobGroup;
    String code = UUID.randomUUID().toString();
    JobExecuteQueueProxy executeQueueProxy;

    public SimpleJobScheduleClient(String jobGroup) {
        this.jobGroup = jobGroup;
        executeQueueProxy = new JobExecuteQueueProxy(SpringContextDelegate.getBean(JobScheduleQueue.class), code);
    }

    @Override
    public void run() {
        //start polling executor
        startPollingExecutor();
        pollExecutor.submit(new HeartBeatChecker());
    }

    private void startPollingExecutor() {
        pollExecutor.submit(new Runnable() {
            @Override
            public void run(){
                logger.info("client start polling");
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        JobSchedule jobSchedule = executeQueueProxy.takeExecute(jobGroup);
                        if (jobSchedule != null) {
                            JobScheduleExecute jobScheduleExecute = new JobScheduleExecute(jobSchedule);
                            Future task = executeExecutor.submit(jobScheduleExecute);
                            executingMap.put(jobSchedule.getId(), task);
                            logger.info("client has polled job schedule:" + jobSchedule.getId());
                        } else {
                            //idle execution
                            Thread.sleep(1000);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } catch (Exception e) {
                        logger.error("fail to poll schedule", e);
                    }
                }
            }
        });
    }

    class JobScheduleExecute implements Runnable {
        JobSchedule jobSchedule;

        public JobScheduleExecute(JobSchedule jobSchedule) {
            this.jobSchedule = jobSchedule;
        }

        @Override
        public void run() {
            //TODO
            // get script
            //start run
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
            }
            System.out.println("execute the job schedule:" + jobSchedule.getId());
        }
    }

    class HeartBeatChecker implements Runnable {

        @Override
        public void run() {
            logger.info("client start heartbeating.");
            while (!Thread.currentThread().isInterrupted()) {
                for (long id : executingMap.keySet()) {
                    try {
                        Future future = executingMap.get(id);
                        //TODO
                        if (!future.isDone()) {
                            executeQueueProxy.refreshJobScheduleRunning(id);
                            logger.info("send heartbeat for job schedule:" + id);
                        } else {
                            executeQueueProxy.updateJobScheduleState(id, JobSchedule.JOB_SCHEDULE_STATUS_SUCCESS);
                            executingMap.remove(id);
                        }
                    } catch (Exception e) {
                        logger.error("fail to send heart beat.", e);
                    }
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
