package com.zhiyong.gateway.biz.service;

import com.github.pagehelper.PageInfo;
import com.zhiyong.gateway.common.model.PageRequest;
import com.zhiyong.gateway.dal.domain.Auth;
import com.zhiyong.gateway.dal.domain.AuthExt;
import com.zhiyong.gateway.dal.domain.User;

/**
 * @ClassName UserService
 * @Description: 用户服务
 * @Author 毛军锐
 * @Date 2020/11/30 下午7:53
 **/
public interface UserService {

    /**
     * 保存用户
     *
     * @param user
     * @return
     */
    Integer saveUser(User user);

    /**
     * 获取用户列表
     *
     * @return
     */
    PageInfo<User> pagerUser(String account, PageRequest pageRequest);

    /**
     * 获取用户
     *
     * @param account
     * @return
     */
    User getUserByAccount(String account);

    /**
     * 重置密码
     *
     * @param id
     * @param newPwd
     * @return
     */
    boolean resetPassword(Integer id, String newPwd);

    /**
     * 保存权限
     *
     * @param auth
     * @return
     */
    Integer saveAuth(Auth auth);

    /**
     * 获取权限列表
     *
     * @return
     */
    PageInfo<AuthExt> pagerAuth(String account, PageRequest pageRequest);

    /**
     * 删除权限
     *
     * @param id
     * @return
     */
    int delAuth(Integer id);

    /**
     * 获取用户权限
     * @param userId
     * @param apiGroup
     * @return
     */
    Auth getAuth(Integer userId, Integer apiGroup);
}
