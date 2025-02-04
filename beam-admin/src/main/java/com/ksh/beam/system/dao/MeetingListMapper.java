package com.ksh.beam.system.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ksh.beam.system.entity.meeting.MeetingList;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 会议列表
 */
public interface MeetingListMapper extends BaseMapper<MeetingList> {

    IPage<MeetingList> selectPageList(Page page, @Param("meeting") MeetingList meeting);

    List<Map<String, Object>> exportData(@Param("meeting") MeetingList meeting);
}
