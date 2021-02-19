package com.zhiyong.gateway.dal.dao;

import com.zhiyong.gateway.dal.domain.ApiCfgExt;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * @ClassName ApiCfgExtMapper
 * @Description: TODO
 * @Author 毛军锐
 * @Date 2020/11/27 下午8:57
 **/
public interface ApiCfgExtMapper {

    /**
     * 查询API配置明细
     * @param apiName
     * @param apiDesc
     * @return
     */
    List<ApiCfgExt> queryApiDetail(@Param("apiName") String apiName, @Param("apiDesc") String apiDesc,
                                   @Param("groupId") Integer groupId, @Param("sort") String sort);
}
