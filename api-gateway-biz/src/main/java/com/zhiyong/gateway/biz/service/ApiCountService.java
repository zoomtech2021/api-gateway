package com.zhiyong.gateway.biz.service;

import com.github.pagehelper.PageInfo;
import com.zhiyong.gateway.common.model.PageRequest;
import com.zhiyong.gateway.dal.domain.ApiCount;
import com.zhiyong.gateway.dal.domain.AppCfg;

/**
 * @ClassName ApiCountService
 * @Description: Api调用统计接口
 * @Author 毛军锐
 * @Date 2020/12/9 下午2:16
 **/
public interface ApiCountService {

    /**
     * 保存API统计数据
     *
     * @param apiCount
     * @return
     */
    int saveApiCount(ApiCount apiCount);

    /**
     * 查询API统计数据
     *
     * @param apiName
     * @param startTime
     * @param endTime
     * @param pageRequest
     * @return
     */
    PageInfo<ApiCount> pagerListApiCount(String apiName, String startTime, String endTime, PageRequest pageRequest);
}
