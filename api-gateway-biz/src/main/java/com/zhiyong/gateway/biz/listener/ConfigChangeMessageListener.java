package com.zhiyong.gateway.biz.listener;

import com.zhiyong.gateway.biz.cache.ApiConfigLocalCache;
import com.zhiyong.gateway.biz.cache.FlowControlRuleCache;
import com.zhiyong.gateway.biz.model.ApiCache;
import com.zhiyong.gateway.dal.resdis.RedisMapper;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @ClassName ConfigChangeMessageListener
 * @Description: 配置变更redis消息通知
 * @Author 毛军锐
 * @Date 2020/12/21 下午3:50
 **/
@Component
public class ConfigChangeMessageListener implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigChangeMessageListener.class);

    @Resource
    private RedisMapper redisMapper;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private ApiConfigLocalCache localCache;
    @Resource
    private FlowControlRuleCache flowControlRuleCache;

    /**
     * 接收消息
     *
     * @param message
     * @param pattern
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String content = (String) redisTemplate.getValueSerializer()
                    .deserialize(message.getBody());
            if (StringUtils.isEmpty(content)) {
                return;
            }
            localCache.delete(content);
            updateFlowControlRule(content);
            LOGGER.info("====刷新配置[{}]的本地缓存", content);
        } catch (Exception e) {
            LOGGER.error("清除本地缓存配置redis消息异常:", e);
        }
    }

    /**
     * 更新API流控规则
     * @param content
     */
    private void updateFlowControlRule(String content) {
        ApiCache apiCache = (ApiCache) redisMapper.getValue(content);
        if (apiCache == null || apiCache.getApiCfg() == null) {
            return;
        }
        flowControlRuleCache.updateRule(apiCache.getApiCfg());
    }
}
