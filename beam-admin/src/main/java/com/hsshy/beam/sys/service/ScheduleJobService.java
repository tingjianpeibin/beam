
package com.hsshy.beam.sys.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hsshy.beam.common.utils.PageUtils;
import com.hsshy.beam.sys.entity.ScheduleJobEntity;

import java.util.Map;

/**
 * 定时任务
 */
public interface ScheduleJobService extends IService<ScheduleJobEntity> {

	IPage<ScheduleJobEntity> queryPage(Map<String, Object> params);

	/**
	 * 保存定时任务
	 */
	void saveScheduleJob(ScheduleJobEntity scheduleJob);
	
	/**
	 * 更新定时任务
	 */
	void update(ScheduleJobEntity scheduleJob);
	
	/**
	 * 批量删除定时任务
	 */
	void deleteBatch(Long[] jobIds);
	
	/**
	 * 批量更新定时任务状态
	 */
	int updateBatch(Long[] jobIds, int status);
	
	/**
	 * 立即执行
	 */
	void run(Long[] jobIds);
	
	/**
	 * 暂停运行
	 */
	void pause(Long[] jobIds);
	
	/**
	 * 恢复运行
	 */
	void resume(Long[] jobIds);
}
