package com.zhiyong.gateway.biz.service;

import com.github.pagehelper.PageInfo;
import com.zhiyong.gateway.common.model.PageRequest;
import com.zhiyong.gateway.dal.domain.OptLog;

/**
 * @ClassName OptLogService
 * @Description: TODO
 * @Author 毛军锐
 * @Date 2020/12/22 上午11:01
 **/
public interface OptLogService {

    /**
     * 保存操作日志
     * @param optLog
     */
    void saveOptLog(OptLog optLog);

    /**
     * 分页查询LOG
     * @return
     */
    PageInfo<OptLog> pagerOptLog(String content, PageRequest pageRequest);
}
