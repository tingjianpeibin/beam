package com.ksh.beam.system.entity.sys;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ksh.beam.common.base.entity.RestEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 部门管理
 */
@Data
@NoArgsConstructor
@TableName("sys_dept")
public class Dept extends RestEntity<Long> {

    //主键id
    @TableId
    private Long id;
    // 上级部门ID，一级部门为0
    @TableField(value = "parent_id")
    private Long parentId;
    // 部门名称
    @TableField(value = "name")
    private String name;
    // 排序
    @TableField(value = "order_num")
    private Integer orderNum;
    // 是否删除  -1：已删除  0：正常
    @TableField(value = "del_flag")
    private Integer delFlag;

    @TableField(exist = false)
    private List<?> children;

    @TableField(exist = false)
    private String label;

    @TableField(exist = false)
    private String pname;


    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
