package com.hsshy.beam.sys.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hsshy.beam.sys.entity.OperationLog;

/**
 * 操作日志
 */
public interface IOperationLogService extends IService<OperationLog> {

    IPage<OperationLog> selectPageList(Page page, OperationLog operationLog);

    void  deleteAll();
}
