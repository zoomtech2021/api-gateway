package com.zhiyong.gateway.admin.controller;

import com.github.pagehelper.PageInfo;
import com.zhiyong.gateway.biz.service.ApiCountService;
import com.zhiyong.gateway.biz.service.OptLogService;
import com.zhiyong.gateway.common.model.PageRequest;
import com.zhiyong.gateway.common.model.PageResult;
import com.zhiyong.gateway.dal.domain.ApiCount;
import com.zhiyong.gateway.dal.domain.OptLog;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName MonitorController
 * @Description: TODO
 * @Author 毛军锐
 * @Date 2020/12/9 下午3:23
 **/
@RestController
@RequestMapping("/admin")
public class MonitorController {

    @Resource
    private ApiCountService apiCountService;

    @Resource
    private OptLogService optLogService;

    /**
     * 获取API列表
     *
     * @return
     */
    @RequestMapping("/getApiCountList.do")
    public PageResult getApiCountList(PageRequest pageRequest, String apiName, String startTime, String endTime) {
        if (StringUtils.isNotBlank(startTime)) {
            startTime = startTime.replaceAll("-", "");
        }
        if (StringUtils.isNotBlank(endTime)) {
            endTime = endTime.replaceAll("-", "");
        }
        PageInfo<ApiCount> pageInfo = apiCountService.pagerListApiCount(apiName, startTime, endTime, pageRequest);
        return PageResult.builder().total(pageInfo.getTotal())
                .rows(pageInfo.getList())
                .pageNum(pageInfo.getPageNum())
                .pageSize(pageInfo.getPageSize())
                .build();
    }

    /**
     * 获取操作日志列表
     *
     * @return
     */
    @RequestMapping("/getOptLogList.do")
    public PageResult getOptLogList(PageRequest pageRequest, String content) {
        PageInfo<OptLog> pageInfo = optLogService.pagerOptLog(content, pageRequest);
        return PageResult.builder().total(pageInfo.getTotal())
                .rows(pageInfo.getList())
                .pageNum(pageInfo.getPageNum())
                .pageSize(pageInfo.getPageSize())
                .build();
    }
}
