package com.zhiyong.gateway.dal.dao;

import com.zhiyong.gateway.dal.domain.ApiCount;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * @ClassName ApiCountExtMapper
 * @Description: TODO
 * @Author 毛军锐
 * @Date 2020/12/9 下午4:44
 **/
public interface ApiCountExtMapper {

    List<ApiCount> queryApiCount(@Param("apiName") String apiName, @Param("startTime") String startTime,
                                 @Param("endTime") String endTime, @Param("sort") String sort);
}
