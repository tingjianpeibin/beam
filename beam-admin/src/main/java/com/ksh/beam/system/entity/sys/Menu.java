package com.ksh.beam.system.entity.sys;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ksh.beam.common.base.entity.DataEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 菜单管理
 */
@Data
@NoArgsConstructor
@TableName("sys_menu")
public class Menu extends DataEntity<Long> {

	private static final long serialVersionUID = 1L;

	//主键id
	@TableId
	private Long id;
	/**
	 * 父菜单ID，一级菜单为0
	 */
	@TableField(value = "parent_id")
	private Long parentId;
	/**
	 * 菜单名称
	 */
	private String name;
	/**
	 * 菜单URL
	 */
	private String url;
	/**
	 * 授权(多个用逗号分隔，如：business:list,business:create)
	 */
	private String perms;
	/**
	 * 类型   0：目录   1：菜单   2：按钮
	 */
	private Integer type;
	/**
	 * 菜单图标
	 */
	private String icon;
	/**
	 * 排序
	 */
	@TableField(value = "order_num")
	private Integer orderNum;

	@TableField(value = "del_flag")
	private Integer delFlag;

	@TableField(exist = false)
	private String pname;

	@Override
	protected Serializable pkVal() {
        return this.id;
	}
}
