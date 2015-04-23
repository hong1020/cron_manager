package com.cron_manager.scenario;

import com.cron_manager.manager.JobManager;
import com.cron_manager.manager.JobScheduleChangeManager;
import com.cron_manager.manager.JobScheduleManager;
import com.cron_manager.model.Job;
import com.cron_manager.queue.JobScheduleQueue;
import com.cron_manager.redis.RedisCommand;
import com.cron_manager.redis.RedisService;
import com.cron_manager.scheduler.LocalScheduler;
import com.cron_manager.scheduler.SimpleEventScheduler;
import com.cron_manager.scheduler.SimpleScheduler;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import redis.clients.jedis.Jedis;

import javax.sql.DataSource;
import java.util.Date;
import java.util.concurrent.Executors;

/**
 * Created by honcheng on 2015/4/16.
 */
public class ScheduleScenarioTest {
    /**
     * scenario test: create job, activate, deactivate, delete.
     */
    public void testScenarioJobBasic() throws Exception {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring/testApplicationContext.xml");

        System.out.println("flush the redis db");
        RedisService redisService = applicationContext.getBean(RedisService.class);
        redisService.executeCommand(new RedisCommand() {
            @Override
            public Object call(Jedis jedis) throws Exception {
                return jedis.flushDB();
            }
        });

        System.out.println("flush the db");
        ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();
        resourceDatabasePopulator.addScript(new ClassPathResource("database/schema.sql"));
        resourceDatabasePopulator.execute((DataSource)applicationContext.getBean("dataSource"));

        SimpleEventScheduler scheduler = new SimpleEventScheduler("s1");
        Executors.newSingleThreadExecutor().submit(scheduler);

        JobManager jobManager = applicationContext.getBean(JobManager.class);
        Job job = new Job();
        job.setTitle("test");
        job.setDescription("test");
        job.setCron_expression("0/15 * * * * ?");
        job.setTimezone("GMT+8");
        job.setTimeout(100);
        job.setRetry(3);
        job.setRetry_interval(10);
        job.setRun_type(Job.JOB_RUN_TYPE_ONEHOST);
        job.setFail_strategy(Job.JOB_FAIL_STRATEGY_CONTINUE);
        job.setJob_group_name("t1");
        job.setStatus(Job.JOB_STATUS_INACTIVE);
        job.setRun_as("tester");
        job.setCreated_date(new Date(System.currentTimeMillis()));
        job.setCreated_by("h");
        jobManager.create(job);

        ObjectMapper objectMapper = new ObjectMapper();
        Job insertedJob = jobManager.findById(job.getId());
        System.out.println("Insert Job:");
        System.out.println(objectMapper.writeValueAsString(insertedJob));

        JobScheduleChangeManager jobScheduleChangeManager = applicationContext.getBean(JobScheduleChangeManager.class);
        System.out.println("Activate Job:");
        jobScheduleChangeManager.activateJob(insertedJob);

        System.out.println("schedule started");

        //schedule for 100s
        Thread.sleep(100000);
        System.out.println("schedule end");

        System.out.println("reschedule");
        jobScheduleChangeManager.rescheduleJob(insertedJob, "1/10 * * * * ?");
        //schedule for 100s
        Thread.sleep(100000);

        System.out.println("deactivate job");
        jobManager.deactivate(insertedJob.getId());
        //schedule for 100s
        Thread.sleep(100000);

        //System.out.println("delete Job:");
        //jobManager.delete(insertedJob.getId());
    }

    public static void main(String[] args) {
        ScheduleScenarioTest test = new ScheduleScenarioTest();
        System.out.println("start testing");
        try {
            test.testScenarioJobBasic();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("testing failed");
        }

        System.out.println("testing done");
    }
}
