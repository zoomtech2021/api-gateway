package com.zhiyong.gateway.biz.cache;

import com.zhiyong.gateway.biz.model.ApiCache;
import com.zhiyong.gateway.biz.service.ApiConfigService;
import com.zhiyong.gateway.biz.service.StructConfigService;
import com.zhiyong.gateway.common.constant.CacheConstant;
import com.zhiyong.gateway.common.constant.CommonConstant;
import com.zhiyong.gateway.common.enums.ApiState;
import com.zhiyong.gateway.common.enums.TypeStructState;
import com.zhiyong.gateway.dal.domain.ApiCfg;
import com.zhiyong.gateway.dal.domain.ApiParamCfg;
import com.zhiyong.gateway.dal.domain.ApiResultCfg;
import com.zhiyong.gateway.dal.domain.AppCfg;
import com.zhiyong.gateway.dal.domain.PojoTypeJson;
import com.zhiyong.gateway.dal.resdis.RedisMapper;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * @ClassName ApiConfigRedisCache
 * @Description: API配置redis缓存
 * @Author 毛军锐
 * @Date 2020/12/1 下午7:16
 **/
@Component
public class ApiConfigRedisCache {

    @Resource
    private RedisMapper redisMapper;
    @Resource
    private ApiConfigService apiConfigService;
    @Resource
    private StructConfigService structConfigService;

    /**
     * 根据APPKEY查询APP配置
     *
     * @param appKey
     * @return
     */
    public AppCfg getAppCfg(String appKey) {
        String key = CommonConstant.APP_CFG_KEY_PRE + appKey;
        AppCfg appCfg = (AppCfg) redisMapper.getValue(key);
        if (appCfg == null) {
            appCfg = refreshAppCache(appKey);
        }
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
        ApiCache apiCache = (ApiCache) redisMapper.getValue(key);
        if (apiCache == null || apiCache.getApiCfg().getApiType() == null) {
            apiCache = refreshApiCfgCache(apiName, version);
        }
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
        PojoTypeJson typeJson = (PojoTypeJson) redisMapper.getValue(key);
        if (typeJson == null) {
            typeJson = refreshPojoTypeJsonCache(id);
        }
        return typeJson;
    }

    /**
     * 刷新APP缓存
     *
     * @param appKey
     */
    public AppCfg refreshAppCache(String appKey) {
        String key = CommonConstant.APP_CFG_KEY_PRE + appKey;
        try {
            AppCfg appCfg = apiConfigService.getAppCfg(appKey);
            if (appCfg != null && appCfg.getDeleted().intValue() != CommonConstant.DELETED) {
                redisMapper.setValue(key, appCfg, CacheConstant.API_CONFIG_REDIS_EXPIRED);
                return appCfg;
            } else {
                redisMapper.delKey(key);
                return null;
            }
        } finally {
            // 清除所有节点上的本地缓存
            redisMapper.publish(CacheConstant.LOCAL_CACHE_CONFIG_CLEAR_TOPIC, key);
        }
    }

    /**
     * 刷新APP缓存
     *
     * @param appId
     */
    public AppCfg refreshAppCache(Integer appId) {
        AppCfg appCfg = apiConfigService.findAppById(appId);
        if (appCfg == null) {
            return null;
        }
        String key = CommonConstant.APP_CFG_KEY_PRE + appCfg.getAppKey();
        try {
            if (appCfg != null && appCfg.getDeleted().intValue() != CommonConstant.DELETED) {
                redisMapper.setValue(key, appCfg, CacheConstant.API_CONFIG_REDIS_EXPIRED);
                return appCfg;
            } else {
                redisMapper.delKey(key);
                return null;
            }
        } finally {
            // 清除所有节点上的本地缓存
            redisMapper.publish(CacheConstant.LOCAL_CACHE_CONFIG_CLEAR_TOPIC, key);
        }
    }

    /**
     * 刷新API缓存
     *
     * @param apiId
     */
    public ApiCache refreshApiCfgCache(Integer apiId) {
        ApiCfg apiCfg = apiConfigService.findApiById(apiId);
        if (apiCfg == null) {
            return null;
        }
        ApiCache apiCache = handleApiCache(apiCfg);
        return apiCache;
    }

    /**
     * 刷新API缓存
     *
     * @param apiName
     * @param version
     */
    public ApiCache refreshApiCfgCache(String apiName, String version) {
        ApiCfg apiCfg = apiConfigService.getApi(apiName, version);
        if (apiCfg == null) {
            return null;
        }
        ApiCache apiCache = handleApiCache(apiCfg);
        return apiCache;
    }

    private ApiCache handleApiCache(ApiCfg apiCfg) {
        String key = CommonConstant.API_CFG_KEY_PRE + apiCfg.getApiName()
                + ":" + apiCfg.getApiVersion();
        try {
            if (apiCfg.getState() == ApiState.DOWN_LINE.getCode()) {
                redisMapper.delKey(key);
                return null;
            }
            List<ApiParamCfg> paramCfgs = apiConfigService.listApiParam(apiCfg.getId());
            List<ApiResultCfg> resultCfgs = apiConfigService.listApiResult(apiCfg.getId());
            ApiCache apiCache = new ApiCache();
            apiCache.setApiCfg(apiCfg);
            apiCache.setParamCfgs(paramCfgs);
            apiCache.setResultCfgs(resultCfgs);
            redisMapper.setValue(key, apiCache, CacheConstant.API_CONFIG_REDIS_EXPIRED);
            return apiCache;
        } finally {
            // 清除所有节点上的本地缓存
            redisMapper.publish(CacheConstant.LOCAL_CACHE_CONFIG_CLEAR_TOPIC, key);
        }
    }

    /**
     * 刷新数据结构配置json缓存
     *
     * @param id
     */
    public PojoTypeJson refreshPojoTypeJsonCache(Integer id) {
        String key = CommonConstant.DATA_STRUCT_JSON_PRE + id;
        try {
            PojoTypeJson pojoTypeJson = structConfigService.getPojoTypeJsonById(id);
            if (pojoTypeJson != null
                    && pojoTypeJson.getDeleted() != CommonConstant.DELETED
                    && pojoTypeJson.getState() != TypeStructState.DOWN_LINE.getCode()) {
                redisMapper.setValue(key, pojoTypeJson, CacheConstant.API_CONFIG_REDIS_EXPIRED);
                return pojoTypeJson;
            } else {
                redisMapper.delKey(key);
                return null;
            }
        } finally {
            // 清除所有节点上的本地缓存
            redisMapper.publish(CacheConstant.LOCAL_CACHE_CONFIG_CLEAR_TOPIC, key);
        }
    }
}
