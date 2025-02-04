package com.ksh.beam.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ksh.beam.common.utils.ToolUtil;
import com.ksh.beam.system.dao.DeptMapper;
import com.ksh.beam.system.entity.sys.Dept;
import com.ksh.beam.system.service.DeptService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 部门管理
 */
@Service
public class DeptServiceImpl extends ServiceImpl<DeptMapper, Dept> implements DeptService {

    @Override
    public List<Dept> treeDeptList(Long deptId, Dept dept) {
        List<Dept> deptList ;
        if(ToolUtil.isNotEmpty(dept.getName())){
            deptList = this.list(new QueryWrapper<Dept>().lambda().like(Dept::getName,dept.getName()));
        }
        else {
            deptList =  queryListParentId(deptId);
        }
        return getAllDeptTreeList(deptList);
    }

    @Override
    public List<Dept> queryListParentId(Long parentId) {
        return baseMapper.queryListParentId(parentId);
    }

    /**
     * 部门树形表格
     */
    private List<Dept> getAllDeptTreeList(List<Dept> deptList){
        List<Dept> subDeptList = new ArrayList<>();
        for(Dept entity : deptList){
            entity.setChildren(getAllDeptTreeList(queryListParentId(entity.getId())));
            subDeptList.add(entity);
        }
        return subDeptList;
    }
}
