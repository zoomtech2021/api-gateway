package com.zhiyong.gateway.common.constant;

/**
 * @ClassName CacheConstant
 * @Description: TODO
 * @Author 毛军锐
 * @Date 2020/12/21 上午10:38
 **/
public class CacheConstant {

    /**
     * 默认2年
     */
    public static final int API_CONFIG_REDIS_EXPIRED = 2 * 365 * 24 * 3600;

    /**
     * 默认7天
     */
    public static final int API_CONFIG_LOCAL_EXPIRED = 7 * 24 * 3600;

    /**
     * 本地缓存清除消息topic
     */
    public static final String LOCAL_CACHE_CONFIG_CLEAR_TOPIC = "gateway:local-cache-config-clear";
}
