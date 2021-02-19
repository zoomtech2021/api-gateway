package com.zhiyong.gateway.biz.job;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.zhiyong.gateway.biz.service.ApiConfigService;
import com.zhiyong.gateway.biz.service.ApiCountService;
import com.zhiyong.gateway.biz.service.MonitorService;
import com.zhiyong.gateway.common.enums.ApiState;
import com.zhiyong.gateway.dal.domain.ApiCfg;
import com.zhiyong.gateway.dal.domain.ApiCount;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @ClassName CountApiInvokeDataJob
 * @Description: TODO
 * @Author 毛军锐
 * @Date 2020/12/9 下午2:14
 **/
@Component
public class CountApiInvokeDataJob implements SimpleJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(CountApiInvokeDataJob.class);

    @Resource
    private ApiConfigService apiConfigService;
    @Resource
    private MonitorService monitorService;
    @Resource
    private ApiCountService apiCountService;

    @Override
    public void execute(ShardingContext shardingContext) {
        boolean res = false;

        try {
            Thread.sleep(30000);
            List<ApiCfg> apiCfgList = apiConfigService.listApi();
            for (ApiCfg apiCfg : apiCfgList) {
                if (apiCfg.getState() != ApiState.UP_LINE.getCode()) {
                    continue;
                }
                ApiCount apiCount = monitorService.getCallApiCount(apiCfg.getApiName(),
                        apiCfg.getApiVersion(), new Date());
                if (apiCount != null) {
                    apiCountService.saveApiCount(apiCount);
                }
            }
            res = true;
        } catch (Exception e) {
            LOGGER.error("API调用数据定时统计出错：", e);
        } finally {
            LOGGER.info("=====统计API调用数据结束, 执行结果：{}======", res ? "成功" : "失败");
        }
    }
}
