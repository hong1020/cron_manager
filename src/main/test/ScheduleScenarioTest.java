import com.cron_manager.manager.JobManager;
import com.cron_manager.manager.JobScheduleChangeManager;
import com.cron_manager.manager.JobScheduleManager;
import com.cron_manager.model.Job;
import com.cron_manager.queue.JobScheduleQueue;
import com.cron_manager.scheduler.Scheduler;
import com.cron_manager.scheduler.SimpleScheduler;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.context.ApplicationContext;

import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by honcheng on 2015/4/16.
 */
public class ScheduleScenarioTest {
    /**
     * scenario test: create job, activate, deactivate, delete.
     */
    public void testScenarioJobBasic() throws Exception {
        //TODO
        ApplicationContext applicationContext = null;

        SimpleScheduler simpleScheduler = new SimpleScheduler("s1",
                applicationContext.getBean(JobScheduleManager.class),
                applicationContext.getBean(JobScheduleQueue.class));
        simpleScheduler.register();

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
        Executors.newSingleThreadExecutor().submit(simpleScheduler);

        //schedule for 100s
        Thread.sleep(100000);
        System.out.println("schedule end");

        System.out.println("deactivate job");
        jobManager.deactivate(insertedJob);
        //schedule for 100s
        Thread.sleep(100000);

        System.out.println("delete Job:");
        jobManager.delete(insertedJob);
    }
}
