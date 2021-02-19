package com.zhiyong.gateway.biz.service;

import com.zhiyong.gateway.dal.domain.ApiCount;
import java.util.Date;

/**
 * @ClassName MonitorService
 * @Description: 监控服务
 * @Author 毛军锐
 * @Date 2020/12/1 下午11:44
 **/
public interface MonitorService {

    /**
     * 统计API调用情况
     * @param method api名称
     * @param version 版本号
     * @param spends 耗时
     * @param isSucc 是否成功
     */
    void countCallApi(String method, String version, long spends, boolean isSucc);

    /**
     * 获取API调用统计数据
     * @param method
     * @param version
     * @param date
     */
    ApiCount getCallApiCount(String method, String version, Date date);
}
