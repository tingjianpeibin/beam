package com.ksh.beam.system.controller.sys;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ksh.beam.common.factory.impl.ConstantFactory;
import com.ksh.beam.common.utils.R;
import com.ksh.beam.common.utils.ToolUtil;
import com.ksh.beam.system.entity.sys.ScheduleJob;
import com.ksh.beam.system.service.ScheduleJobService;
import com.ksh.beam.system.wrapper.ScheduleWrapper;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.quartz.CronExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 定时任务
 */
@RestController
@RequestMapping("/sys/schedule")
public class ScheduleJobController {

    @Autowired
    private ScheduleJobService scheduleJobService;

    /**
     * 定时任务列表
     */
    @RequestMapping("/page/list")
    @RequiresPermissions("sys:schedule:list")
    public R list(ScheduleJob scheduleJobEntity) {
        QueryWrapper qw = new QueryWrapper<Map>();
        if (ToolUtil.isNotEmpty(scheduleJobEntity.getStatus())) {
            qw.eq("status", scheduleJobEntity.getStatus());
        }
        if (ToolUtil.isNotEmpty(scheduleJobEntity.getBeanName())) {
            qw.like("bean_name", scheduleJobEntity.getBeanName());
        }
        IPage<com.ksh.beam.common.quartz.ScheduleJob> page = scheduleJobService.page(new Page(scheduleJobEntity.getCurrentPage(), scheduleJobEntity.getPageSize()), qw);
        return R.ok(new ScheduleWrapper(page).wrap());
    }

    /**
     * 新增定时任务
     */
    @RequestMapping("/add")
    @RequiresPermissions("sys:schedule:add")
    public R add(@RequestBody ScheduleJob scheduleJob) {
        if (CronExpression.isValidExpression(scheduleJob.getCronExpression())) {
            scheduleJobService.saveScheduleJob(scheduleJob);
            return R.ok();
        } else {
            return R.fail("cron表达式有误");
        }
    }

    /**
     * 编辑定时任务
     */
    @RequestMapping("/edit")
    @RequiresPermissions("sys:schedule:edit")
    public R edit(@RequestBody ScheduleJob scheduleJob) {
        if (CronExpression.isValidExpression(scheduleJob.getCronExpression())) {
            scheduleJobService.update(scheduleJob);
            return R.ok();
        } else {
            return R.fail("cron表达式有误");
        }
    }

    /**
     * 删除定时任务
     */
    @RequestMapping("/del")
    @RequiresPermissions("sys:schedule:del")
    public R del(@RequestBody Long[] jobIds) {
        scheduleJobService.deleteBatch(jobIds);
        return R.ok();
    }

    /**
     * 立即执行任务
     */
    @RequestMapping("/run")
    @RequiresPermissions("sys:schedule:run")
    public R run(@RequestBody Long[] jobIds) {
        scheduleJobService.run(jobIds);
        return R.ok();
    }

    /**
     * 暂停定时任务
     */
    @RequestMapping("/pause")
    @RequiresPermissions("sys:schedule:pause")
    public R pause(@RequestBody Long[] jobIds) {
        scheduleJobService.pause(jobIds);
        return R.ok();
    }

    /**
     * 恢复定时任务
     */
    @RequestMapping("/resume")
    @RequiresPermissions("sys:schedule:resume")
    public R resume(@RequestBody Long[] jobIds) {
        scheduleJobService.resume(jobIds);
        return R.ok();
    }

    /**
     * 获取定时任务状态下拉框
     */
    @GetMapping("/status/list")
    public R getStatusList() {
        return R.ok(ConstantFactory.me().getDictListByCode("schedule_status"));
    }
}
