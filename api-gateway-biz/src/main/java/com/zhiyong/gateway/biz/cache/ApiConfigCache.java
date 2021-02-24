package com.zhiyong.gateway.biz.cache;

import com.zhiyong.gateway.biz.model.ApiCache;
import com.zhiyong.gateway.common.constant.CommonConstant;
import com.zhiyong.gateway.dal.domain.AppCfg;
import com.zhiyong.gateway.dal.domain.PojoTypeJson;
import javax.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * @ClassName ApiConfigCache
 * @Description: API配置缓存
 * @Author 毛军锐
 * @Date 2020/12/21 下午3:42
 **/
@Component
public class ApiConfigCache {

    @Resource
    private ApiConfigLocalCache localCache;
    @Resource
    private ApiConfigRedisCache remoteCache;

    /**
     * 根据APPKEY查询APP配置
     *
     * @param appKey
     * @return
     */
    public AppCfg getAppCfg(String appKey) {
        String key = CommonConstant.APP_CFG_KEY_PRE + appKey;
        AppCfg appCfg = (AppCfg) localCache.getValue(key);
        if (appCfg == null) {
            appCfg = remoteCache.getAppCfg(appKey);
        }
        localCache.setValue(key, appCfg);
        return appCfg;
    }

    /**
     * 查询API信息
     *
     * @param apiName
     * @param version
     * @return
     */
    public ApiCache getApi(String apiName, String version) {
        String key = CommonConstant.API_CFG_KEY_PRE + apiName + ":" + version;
        ApiCache apiCache = (ApiCache) localCache.getValue(key);
        if (apiCache == null) {
            apiCache = remoteCache.getApi(apiName, version);
        }
        localCache.setValue(key, apiCache);
        return apiCache;
    }

    /**
     * 获取数据结构JSON
     *
     * @param id 配置ID
     * @return
     */
    public PojoTypeJson getPojoTypeJson(Integer id) {
        String key = CommonConstant.DATA_STRUCT_JSON_PRE + id;
        PojoTypeJson typeJson = (PojoTypeJson) localCache.getValue(key);
        if (typeJson == null) {
            typeJson = remoteCache.getPojoTypeJson(id);
        }
        localCache.setValue(key, typeJson);
        return typeJson;
    }
}
