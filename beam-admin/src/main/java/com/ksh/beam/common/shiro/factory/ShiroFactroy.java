package com.ksh.beam.common.shiro.factory;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ksh.beam.system.dao.MenuMapper;
import com.ksh.beam.system.dao.UserMapper;
import com.ksh.beam.system.entity.sys.Menu;
import com.ksh.beam.system.entity.sys.User;
import com.ksh.beam.common.constant.Constant;
import com.ksh.beam.common.constant.cache.Cache;
import com.ksh.beam.common.constant.cache.CacheKey;
import com.ksh.beam.common.factory.impl.ConstantFactory;
import com.ksh.beam.common.shiro.IShiro;
import com.ksh.beam.common.shiro.ShiroUser;
import com.ksh.beam.common.utils.SpringContextHolder;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@DependsOn("springContextHolder")
@Transactional(readOnly = true)
public class ShiroFactroy implements IShiro {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MenuMapper menuMapper;

    public static IShiro me() {
        return SpringContextHolder.getBean(IShiro.class);
    }

    @Override
    public User user(String account) {

        //查询用户信息
        User user = userMapper.selectOne(new QueryWrapper<User>().lambda().eq(User::getAccount,account));

        //账号不存在
        if(user == null) {
            throw new UnknownAccountException("账号或密码不正确");
        }

        //账号锁定
        if(user.getStatus() == 0){
            throw new LockedAccountException("账号已被锁定,请联系管理员");
        }
        return user;
    }

    @Override
    public ShiroUser shiroUser(User user) {
        ShiroUser shiroUser = new ShiroUser();

        shiroUser.setId(user.getId());
        shiroUser.setAccount(user.getAccount());
        shiroUser.setDeptId(user.getDeptId());
        shiroUser.setDeptName(ConstantFactory.me().getDeptName(user.getDeptId()));
        shiroUser.setName(user.getName());
        shiroUser.setAvatar(user.getAvatar());

        List<Long> roleList = ConstantFactory.me().getRoleIdsById(user.getId());
        List<String> roleNameList = new ArrayList<>();
        for (Long roleId : roleList) {
            roleNameList.add(ConstantFactory.me().getSingleRoleName(roleId));
        }
        shiroUser.setRoleList(roleList);
        shiroUser.setRoleNames(roleNameList);

        return shiroUser;
    }

    @Cacheable(value = Cache.CONSTANT, key = "'" + CacheKey.USER_MENU + "'+#userId")
    @Override
    public List<String> findPermissionsByUserId(Long userId) {
        List<String> permsList;
        //系统管理员，拥有最高权限
        if(userId == Constant.SUPER_ADMIN){
            List<Menu> menuList = menuMapper.selectList(null);
            permsList = new ArrayList<>(menuList.size());
            for(Menu menu : menuList){
                permsList.add(menu.getPerms());
            }
        }else{
            permsList = userMapper.queryAllPerms(userId);
        }
        return permsList;
    }

    @Override
    public SimpleAuthenticationInfo info(ShiroUser shiroUser, User user, String realmName) {
        String credentials = user.getPassword();

        return new SimpleAuthenticationInfo(shiroUser, credentials, ByteSource.Util.bytes(user.getSalt()), realmName);
    }
}
