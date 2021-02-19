package com.zhiyong.gateway.biz.service.impl;

import com.zhiyong.gateway.biz.service.MonitorService;
import com.zhiyong.gateway.common.constant.CommonConstant;
import com.zhiyong.gateway.common.enums.ApiMetrics;
import com.zhiyong.gateway.dal.domain.ApiCount;
import com.zhiyong.gateway.dal.resdis.RedisMapper;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * @ClassName MonitorServiceImpl
 * @Description: TODO
 * @Author 毛军锐
 * @Date 2020/12/1 下午11:47
 **/
@Service
public class MonitorServiceImpl implements MonitorService {
    private static final int EXPIRED = 3 * 24 * 3600;

    @Resource
    private RedisMapper redisMapper;

    @Override
    public void countCallApi(String method, String version, long spends, boolean isSucc) {
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        String key = CommonConstant.API_COUNT_KEY_PRE + method + ":" + version + ":" + df.format(new Date());
        if (!redisMapper.hasKey(key)) {
            redisMapper.setExpired(key, EXPIRED);
        }
        redisMapper.incrHashValue(key, ApiMetrics.TOTAL_COUNT.getCode(), 1);
        redisMapper.incrHashValue(key, ApiMetrics.TOTAL_SPENDS.getCode(), spends);
        if (isSucc) {
            redisMapper.incrHashValue(key, ApiMetrics.SUCC_COUNT.getCode(), 1);
        }
        String maxSpend = (String) redisMapper.getHashValue(key, ApiMetrics.MAX_SPENDS.getCode());
        if (maxSpend == null || Long.parseLong(maxSpend) < spends) {
            redisMapper.setHashValue(key, ApiMetrics.MAX_SPENDS.getCode(), spends + "");
        }
    }

    @Override
    public ApiCount getCallApiCount(String method, String version, Date date) {
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        String day = df.format(date);
        String key = CommonConstant.API_COUNT_KEY_PRE + method + ":" + version + ":" + day;
        if (!redisMapper.hasKey(key)) {
            return null;
        }
        ApiCount apiCount = new ApiCount();
        apiCount.setCountDate(day);
        apiCount.setApiName(method);
        apiCount.setApiVersion(version);

        String totalCount = (String) redisMapper.getHashValue(key, ApiMetrics.TOTAL_COUNT.getCode());
        String totalSpends = (String) redisMapper.getHashValue(key, ApiMetrics.TOTAL_SPENDS.getCode());
        String succCount = (String) redisMapper.getHashValue(key, ApiMetrics.SUCC_COUNT.getCode());
        String maxSpends = (String) redisMapper.getHashValue(key, ApiMetrics.MAX_SPENDS.getCode());
        apiCount.setTotalCount(totalCount == null ? 0 : Integer.parseInt(totalCount));
        apiCount.setTotalSpends(totalSpends == null ? 0 : Integer.parseInt(totalSpends));
        apiCount.setSuccCount(succCount == null ? 0 : Integer.parseInt(succCount));
        apiCount.setMaxSpends(maxSpends == null ? 0 : Integer.parseInt(maxSpends));
        return apiCount;
    }
}
