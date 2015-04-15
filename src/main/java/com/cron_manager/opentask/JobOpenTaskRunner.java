package com.cron_manager.opentask;

/**
 * Created by honcheng on 2015/4/15.
 */
public class JobOpenTaskRunner implements Runnable {

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            //read all the open task
            //do it one by one, eat the exception.
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
