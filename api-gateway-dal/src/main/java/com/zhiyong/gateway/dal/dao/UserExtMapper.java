package com.zhiyong.gateway.dal.dao;

import com.zhiyong.gateway.dal.domain.ApiCfg;
import com.zhiyong.gateway.dal.domain.AuthExt;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * @ClassName UserExtMapper
 * @Description: TODO
 * @Author 毛军锐
 * @Date 2020/12/22 下午2:23
 **/
public interface UserExtMapper {

    /**
     * 查询权限列表
     * @param account
     * @param sort
     * @return
     */
    List<AuthExt> queryAuthList(@Param("account") String account, @Param("sort") String sort);
}
