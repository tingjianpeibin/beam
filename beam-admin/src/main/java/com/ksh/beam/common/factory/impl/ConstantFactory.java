package com.ksh.beam.common.factory.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ksh.beam.common.constant.cache.Cache;
import com.ksh.beam.common.constant.cache.CacheKey;
import com.ksh.beam.common.factory.IConstantFactory;
import com.ksh.beam.common.utils.SpringContextHolder;
import com.ksh.beam.common.utils.ToolUtil;
import com.ksh.beam.system.dao.DeptMapper;
import com.ksh.beam.system.dao.DictMapper;
import com.ksh.beam.system.dao.RoleMapper;
import com.ksh.beam.system.dao.UserMapper;
import com.ksh.beam.system.entity.sys.Dept;
import com.ksh.beam.system.entity.sys.Dict;
import com.ksh.beam.system.entity.sys.Role;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 常量的生产工厂
 */
@Component
@DependsOn("springContextHolder")
public class ConstantFactory implements IConstantFactory {

    private RoleMapper roleMapper = SpringContextHolder.getBean(RoleMapper.class);
    private DeptMapper deptMapper = SpringContextHolder.getBean(DeptMapper.class);
    private UserMapper userMapper = SpringContextHolder.getBean(UserMapper.class);
    private DictMapper dictMapper = SpringContextHolder.getBean(DictMapper.class);

    public static IConstantFactory me() {
        return SpringContextHolder.getBean("constantFactory");
    }

    @Override
    public List<Long> getRoleIdsById(Long userId) {
        return userMapper.getRoleIdsById(userId);
    }

    /**
     * 通过角色id获取角色名称
     */
    @Override
    @Cacheable(value = Cache.CONSTANT, key = "'" + CacheKey.SINGLE_ROLE_NAME + "'+#roleId")
    public String getSingleRoleName(Long roleId) {
        if (0 == roleId) {
            return "--";
        }
        Role roleObj = roleMapper.selectById(roleId);
        if (ToolUtil.isNotEmpty(roleObj) && ToolUtil.isNotEmpty(roleObj.getRoleName())) {
            return roleObj.getRoleName();
        }
        return "";
    }

    /**
     * 获取部门名称
     */
    @Override
    @Cacheable(value = Cache.CONSTANT, key = "'" + CacheKey.DEPT_NAME + "'+#deptId")
    public String getDeptName(Long deptId) {
        Dept dept = deptMapper.selectById(deptId);
        if (ToolUtil.isNotEmpty(dept) && ToolUtil.isNotEmpty(dept.getName())) {
            return dept.getName();
        }
        return "";
    }

    @Override
    public String getDictsByCode(String pcode, String code) {
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<Dict>().eq("code", pcode);
        Dict dict = dictMapper.selectOne(queryWrapper);
        if (dict == null) {
            return "";
        } else {
            QueryWrapper<Dict> wrapper = new QueryWrapper<>();
            wrapper = wrapper.eq("pid", dict.getId());
            List<Dict> dicts = dictMapper.selectList(wrapper);
            for (Dict item : dicts) {
                if (item.getCode() != null && item.getCode().equals(code)) {
                    return item.getName();
                }
            }
            return "";
        }
    }

    @Override
    public List<Dict> getDictListByCode(String pcode) {
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<Dict>().eq("code", pcode);
        Dict dict = dictMapper.selectOne(queryWrapper);
        if (dict == null) {
            return null;
        } else {
            QueryWrapper<Dict> wrapper = new QueryWrapper<>();
            wrapper = wrapper.eq("pid", dict.getId());
            List<Dict> dicts = dictMapper.selectList(wrapper);
            return dicts;
        }
    }
}
