package com.zhiyong.gateway.biz.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhiyong.gateway.biz.service.UserService;
import com.zhiyong.gateway.common.constant.CommonConstant;
import com.zhiyong.gateway.common.enums.ErrorCode;
import com.zhiyong.gateway.common.exception.GatewayException;
import com.zhiyong.gateway.common.model.PageRequest;
import com.zhiyong.gateway.dal.dao.AuthMapper;
import com.zhiyong.gateway.dal.dao.UserExtMapper;
import com.zhiyong.gateway.dal.dao.UserMapper;
import com.zhiyong.gateway.dal.domain.Auth;
import com.zhiyong.gateway.dal.domain.AuthExample;
import com.zhiyong.gateway.dal.domain.AuthExt;
import com.zhiyong.gateway.dal.domain.User;
import com.zhiyong.gateway.dal.domain.UserExample;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * @ClassName UserServiceImpl
 * @Description: TODO
 * @Author 毛军锐
 * @Date 2020/11/30 下午7:58
 **/
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private AuthMapper authMapper;
    @Resource
    private UserExtMapper userExtMapper;

    @Override
    public Integer saveUser(User user) {
        User us = getUserByAccount(user.getAccount());
        if (user.getId() == null) {
            if (us != null) {
                throw new GatewayException(ErrorCode.ISP_SYSTEM_ERROR, "用户名已存在");
            }
            user.setCreateTime(new Date());
            userMapper.insertSelective(user);
        } else {
            if (us != null && user.getId().intValue() != us.getId()) {
                throw new GatewayException(ErrorCode.ISP_SYSTEM_ERROR, "用户名已存在");
            }
            user.setUpdateTime(new Date());
            userMapper.updateByPrimaryKeySelective(user);
        }
        return user.getId();
    }

    @Override
    public PageInfo<User> pagerUser(String account, PageRequest pageRequest) {
        try {
            PageHelper.startPage(pageRequest.getPage(), pageRequest.getRows());
            UserExample example = new UserExample();
            UserExample.Criteria criteria = example.createCriteria();
            criteria.andDeletedEqualTo(CommonConstant.VALID);
            criteria.andAccountNotEqualTo("admin");
            if (StringUtils.isNotBlank(account)) {
                criteria.andAccountLike("%" + account + "%");
            }
            if (StringUtils.isBlank(pageRequest.getSortColumn())) {
                example.setOrderByClause(" id desc ");
            } else {
                example.setOrderByClause(" " + pageRequest.getSortColumn() + " " + pageRequest.getOrder());
            }
            List<User> userList = userMapper.selectByExample(example);
            for (User user : userList) {
                user.setPassword(null);
            }
            PageInfo<User> pageInfo = new PageInfo<>(userList);
            return pageInfo;
        } finally {
            PageHelper.clearPage();
        }
    }

    @Override
    public User getUserByAccount(String account) {
        UserExample example = new UserExample();
        example.createCriteria().andAccountEqualTo(account);
        List<User> userList = userMapper.selectByExample(example);
        if (!CollectionUtils.isEmpty(userList)) {
            User user = userList.get(0);
            return user;
        }
        return null;
    }

    @Override
    public boolean resetPassword(Integer id, String newPwd) {
        User user = new User();
        user.setId(id);
        user.setPassword(newPwd);
        user.setUpdateTime(new Date());
        int res = userMapper.updateByPrimaryKeySelective(user);
        return res > 0;
    }

    @Override
    public Integer saveAuth(Auth auth) {
        Auth existAuth = getAuth(auth.getUserId(), auth.getApiGroup());
        if (existAuth == null) {
            auth.setCreateTime(new Date());
            return authMapper.insertSelective(auth);
        }
        return 0;
    }

    @Override
    public PageInfo<AuthExt> pagerAuth(String account, PageRequest pageRequest) {
        try {
            PageHelper.startPage(pageRequest.getPage(), pageRequest.getRows());
            String order = " t1.id desc";
            if (StringUtils.isNotBlank(pageRequest.getSortColumn())) {
                if (StringUtils.equals(pageRequest.getSortColumn(), "group_name")) {
                    order = " t3." + pageRequest.getSortColumn() + " " + pageRequest.getOrder();
                } else {
                    order = " t1." + pageRequest.getSortColumn() + " " + pageRequest.getOrder();
                }
            }
            List<AuthExt> authExtList = userExtMapper.queryAuthList(account, order);
            PageInfo<AuthExt> pageInfo = new PageInfo<>(authExtList);
            return pageInfo;
        } finally {
            PageHelper.clearPage();
        }
    }

    @Override
    public int delAuth(Integer id) {
        return authMapper.deleteByPrimaryKey(id);
    }

    @Override
    public Auth getAuth(Integer userId, Integer apiGroup) {
        AuthExample example = new AuthExample();
        example.createCriteria().andUserIdEqualTo(userId).andApiGroupEqualTo(apiGroup);
        List<Auth> authList = authMapper.selectByExample(example);
        return CollectionUtils.isEmpty(authList) ? null : authList.get(0);
    }
}
