package com.zhiyong.gateway.biz.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.zhiyong.gateway.common.constant.CacheConstant;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @ClassName ApiConfigLocalCache
 * @Description: API配置本地缓存
 * @Author 毛军锐
 * @Date 2020/12/21 上午10:33
 **/
@Component
public class ApiConfigLocalCache {

    private Cache<String, Object> localCache = null;

    @PostConstruct
    public void init() {
        localCache = CacheBuilder.newBuilder()
                .initialCapacity(30).maximumSize(5000)
                .expireAfterWrite(CacheConstant.API_CONFIG_LOCAL_EXPIRED, TimeUnit.SECONDS).build();
    }

    public void setValue(String key, Object value) {
        if (StringUtils.isBlank(key) || value == null) {
            return;
        }
        localCache.put(key, value);
    }

    public Object getValue(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        return localCache.getIfPresent(key);
    }

    public void delete(String key) {
        if (StringUtils.isBlank(key)) {
            return;
        }
        localCache.invalidate(key);
    }
}
