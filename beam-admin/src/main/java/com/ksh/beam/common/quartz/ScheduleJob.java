package com.ksh.beam.common.quartz;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 定时任务
 */
public class ScheduleJob extends QuartzJobBean {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private ExecutorService service = Executors.newSingleThreadExecutor(); 
	
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        com.ksh.beam.system.entity.sys.ScheduleJob scheduleJob = (com.ksh.beam.system.entity.sys.ScheduleJob) context.getMergedJobDataMap().get(com.ksh.beam.system.entity.sys.ScheduleJob.JOB_PARAM_KEY);
        
        //任务开始时间
        long startTime = System.currentTimeMillis();
        try {
            //执行任务
        	logger.info("任务准备执行，任务ID：" + scheduleJob.getId());
            ScheduleRunnable task = new ScheduleRunnable(scheduleJob.getBeanName(), scheduleJob.getMethodName(), scheduleJob.getParams());
            Future<?> future = service.submit(task);
			future.get();

			//任务执行总时长
			long times = System.currentTimeMillis() - startTime;
			logger.info("任务执行完毕，任务ID：" + scheduleJob.getId() + "  总共耗时：" + times + "毫秒");
		} catch (Exception e) {
			//任务执行总时长
			long times = System.currentTimeMillis() - startTime;
			logger.error("任务执行失败，任务ID：" + scheduleJob.getId() + "  总共耗时：" + times + "毫秒", e);
		}
	}
}
