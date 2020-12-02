package no.hvl.past.webui.backend.quickRuleEngine;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;


public class ScheduledTimer extends Timer {

    private Scheduler scheduler;
    private final String cronExpression;

    public ScheduledTimer(RuleEngine engine, String cronExpression) {
        super(engine);
        this.cronExpression = cronExpression;

        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        try {
            this.scheduler = schedulerFactory.getScheduler();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public class ScheduledTimerJob implements Job {

        @Override
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            ScheduledTimer.this.trigger();
        }
    }

    @Override
    protected void terminate() {
        try {
            scheduler.shutdown();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void performStartup() {
        try {
            JobDetail job = JobBuilder.newJob(ScheduledTimerJob.class)
                    .build();
            org.quartz.Trigger trigger = TriggerBuilder.newTrigger()
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                    .forJob(job)
                    .build();

            scheduler.scheduleJob(job, trigger);

            scheduler.start();

        } catch (SchedulerException e) {
            e.printStackTrace();
        }

    }




}
