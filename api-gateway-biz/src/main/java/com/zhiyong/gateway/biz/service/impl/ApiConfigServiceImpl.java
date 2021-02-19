package com.zhiyong.gateway.biz.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhiyong.gateway.biz.cache.ApiConfigRedisCache;
import com.zhiyong.gateway.biz.service.ApiConfigService;
import com.zhiyong.gateway.common.constant.CommonConstant;
import com.zhiyong.gateway.common.enums.ErrorCode;
import com.zhiyong.gateway.common.exception.GatewayException;
import com.zhiyong.gateway.common.model.PageRequest;
import com.zhiyong.gateway.common.util.AssertUtil;
import com.zhiyong.gateway.dal.dao.ApiCfgExtMapper;
import com.zhiyong.gateway.dal.dao.ApiCfgMapper;
import com.zhiyong.gateway.dal.dao.ApiGroupCfgMapper;
import com.zhiyong.gateway.dal.dao.ApiParamCfgMapper;
import com.zhiyong.gateway.dal.dao.ApiResultCfgMapper;
import com.zhiyong.gateway.dal.dao.AppCfgMapper;
import com.zhiyong.gateway.dal.domain.ApiCfg;
import com.zhiyong.gateway.dal.domain.ApiCfgExample;
import com.zhiyong.gateway.dal.domain.ApiCfgExt;
import com.zhiyong.gateway.dal.domain.ApiGroupCfg;
import com.zhiyong.gateway.dal.domain.ApiGroupCfgExample;
import com.zhiyong.gateway.dal.domain.ApiParamCfg;
import com.zhiyong.gateway.dal.domain.ApiParamCfgExample;
import com.zhiyong.gateway.dal.domain.ApiResultCfg;
import com.zhiyong.gateway.dal.domain.ApiResultCfgExample;
import com.zhiyong.gateway.dal.domain.AppCfg;
import com.zhiyong.gateway.dal.domain.AppCfgExample;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * @ClassName ApiConfigServiceImpl
 * @Description: TODO
 * @Author 毛军锐
 * @Date 2020/11/24 下午6:48
 **/
@Service
public class ApiConfigServiceImpl implements ApiConfigService {
    @Resource
    private AppCfgMapper appCfgMapper;
    @Resource
    private ApiGroupCfgMapper apiGroupCfgMapper;
    @Resource
    private ApiCfgMapper apiCfgMapper;
    @Resource
    private ApiCfgExtMapper apiCfgExtMapper;
    @Resource
    private ApiParamCfgMapper apiParamCfgMapper;
    @Resource
    private ApiResultCfgMapper apiResultCfgMapper;
    @Resource
    private ApiConfigRedisCache apiConfigRedisCache;

    @Override
    @Transactional
    public int saveApp(AppCfg appCfg) {
        int res = 0;
        AppCfg cfg = queryAppByName(appCfg.getAppName());
        AppCfg cfg2 = getAppCfg(appCfg.getAppKey());
        if (appCfg.getId() != null) {
            if (cfg != null && appCfg.getId().intValue() != cfg.getId().intValue()) {
                throw new GatewayException(ErrorCode.ISP_SYSTEM_ERROR, "应用名已存在");
            }
            if (cfg2 != null && appCfg.getId().intValue() != cfg2.getId().intValue()) {
                throw new GatewayException(ErrorCode.ISP_SYSTEM_ERROR, "AppKey已存在");
            }
            appCfg.setUpdateTime(new Date());
            res = appCfgMapper.updateByPrimaryKeySelective(appCfg);
        } else {
            if (cfg != null) {
                throw new GatewayException(ErrorCode.ISP_SYSTEM_ERROR, "应用名已存在");
            }
            if (cfg2 != null) {
                throw new GatewayException(ErrorCode.ISP_SYSTEM_ERROR, "AppKey已存在");
            }
            appCfg.setCreateTime(new Date());
            res = appCfgMapper.insertSelective(appCfg);
        }
        // 刷Redis缓存
        apiConfigRedisCache.refreshAppCache(appCfg.getAppKey());
        return res;
    }

    @Override
    @Transactional
    public int delApp(AppCfg appCfg) {
        int res = 0;
        res = appCfgMapper.updateByPrimaryKeySelective(appCfg);
        // 刷Redis缓存
        apiConfigRedisCache.refreshAppCache(appCfg.getId());
        return res;
    }

    @Override
    public List<AppCfg> listApp() {
        AppCfgExample example = new AppCfgExample();
        example.createCriteria().andDeletedEqualTo(CommonConstant.VALID);
        example.setOrderByClause(" id desc ");
        List<AppCfg> appCfgList = appCfgMapper.selectByExample(example);
        return appCfgList;
    }

    @Override
    public PageInfo<AppCfg> pagerListApp(String appName, String appKey, PageRequest pageRequest) {
        try {
            PageHelper.startPage(pageRequest.getPage(), pageRequest.getRows());
            AppCfgExample example = new AppCfgExample();
            AppCfgExample.Criteria criteria = example.createCriteria();
            if (StringUtils.isNotBlank(appName)) {
                criteria.andAppNameLike("%" + appName + "%");
            }
            if (StringUtils.isNotBlank(appKey)) {
                criteria.andAppKeyLike("%" + appKey + "%");
            }
            criteria.andDeletedEqualTo(CommonConstant.VALID);
            if (StringUtils.isBlank(pageRequest.getSortColumn())) {
                example.setOrderByClause(" id desc ");
            } else {
                example.setOrderByClause(" " + pageRequest.getSortColumn() + " " + pageRequest.getOrder());
            }
            List<AppCfg> appCfgList = appCfgMapper.selectByExample(example);
            PageInfo<AppCfg> pageInfo = new PageInfo<>(appCfgList);
            return pageInfo;
        } finally {
            PageHelper.clearPage();
        }
    }

    @Override
    public AppCfg queryAppByName(String name) {
        AppCfgExample example = new AppCfgExample();
        example.createCriteria().andAppNameEqualTo(name).andDeletedEqualTo(CommonConstant.VALID);
        List<AppCfg> appCfgList = appCfgMapper.selectByExample(example);
        return CollectionUtils.isEmpty(appCfgList) ? null : appCfgList.get(0);
    }

    @Override
    public AppCfg findAppById(Integer id) {
        return appCfgMapper.selectByPrimaryKey(id);
    }

    @Override
    public AppCfg getAppCfg(String appKey) {
        AppCfgExample example = new AppCfgExample();
        example.createCriteria().andDeletedEqualTo(CommonConstant.VALID).andAppKeyEqualTo(appKey);
        List<AppCfg> appCfgs = appCfgMapper.selectByExample(example);
        return CollectionUtils.isEmpty(appCfgs) ? null : appCfgs.get(0);
    }


    @Override
    public int saveApiGroup(ApiGroupCfg apiGroupCfg) {
        ApiGroupCfg cfg = queryApiGroupByName(apiGroupCfg.getGroupName());
        if (apiGroupCfg.getId() != null) {
            if (cfg != null && cfg.getId().intValue() != apiGroupCfg.getId().intValue()) {
                throw new GatewayException(ErrorCode.ISP_SYSTEM_ERROR, "API分组名已存在");
            }
            apiGroupCfg.setUpdateTime(new Date());
            return apiGroupCfgMapper.updateByPrimaryKeySelective(apiGroupCfg);
        } else {
            if (cfg != null) {
                throw new GatewayException(ErrorCode.ISP_SYSTEM_ERROR, "API分组名已存在");
            }
            apiGroupCfg.setCreateTime(new Date());
            return apiGroupCfgMapper.insertSelective(apiGroupCfg);
        }
    }

    @Override
    public int delApiGroup(ApiGroupCfg apiGroupCfg) {
        return apiGroupCfgMapper.updateByPrimaryKeySelective(apiGroupCfg);
    }

    @Override
    public List<ApiGroupCfg> listApiGroup() {
        ApiGroupCfgExample example = new ApiGroupCfgExample();
        example.createCriteria().andDeletedEqualTo(CommonConstant.VALID);
        example.setOrderByClause(" id desc ");
        return apiGroupCfgMapper.selectByExample(example);
    }

    @Override
    public PageInfo<ApiGroupCfg> pagerApiGroup(String groupName, PageRequest pageRequest) {
        try {
            PageHelper.startPage(pageRequest.getPage(), pageRequest.getRows());
            ApiGroupCfgExample example = new ApiGroupCfgExample();
            ApiGroupCfgExample.Criteria criteria = example.createCriteria();
            if (StringUtils.isNotBlank(groupName)) {
                criteria.andGroupNameLike("%" + groupName + "%");
            }
            criteria.andDeletedEqualTo(CommonConstant.VALID);
            if (StringUtils.isBlank(pageRequest.getSortColumn())) {
                example.setOrderByClause(" id desc ");
            } else {
                example.setOrderByClause(" " + pageRequest.getSortColumn() + " " + pageRequest.getOrder());
            }
            List<ApiGroupCfg> apiGroupCfgList = apiGroupCfgMapper.selectByExample(example);
            PageInfo<ApiGroupCfg> pageInfo = new PageInfo<>(apiGroupCfgList);
            return pageInfo;
        } finally {
            PageHelper.clearPage();
        }
    }

    @Override
    public ApiGroupCfg queryApiGroupByName(String name) {
        ApiGroupCfgExample example = new ApiGroupCfgExample();
        example.createCriteria().andGroupNameEqualTo(name).andDeletedEqualTo(CommonConstant.VALID);
        List<ApiGroupCfg> apiGroupCfgList = apiGroupCfgMapper.selectByExample(example);
        return CollectionUtils.isEmpty(apiGroupCfgList) ? null : apiGroupCfgList.get(0);
    }

    @Transactional
    @Override
    public int saveApi(ApiCfg apiCfg) {
        int res = 0;
        AssertUtil.notEmpty("API描述", apiCfg.getApiDesc());
        AssertUtil.notNull("API分类", apiCfg.getApiGroup());
        AssertUtil.notNull("超时时间", apiCfg.getTimeout());
        AssertUtil.notEmpty("请求方式", apiCfg.getHttpMethod());
        AssertUtil.notEmpty("RPC接口名", apiCfg.getRpcInterface());
        AssertUtil.notEmpty("RPC接口方法", apiCfg.getRpcMethod());
        AssertUtil.notEmpty("PRC接口版本", apiCfg.getRpcVersion());
        AssertUtil.notNull("是否检查登录", apiCfg.getNeedLogin());
        AssertUtil.notNull("是否需要授权", apiCfg.getNeedAuth());
        AssertUtil.notNull("限流次数", apiCfg.getCallLimit());

        if (apiCfg.getId() != null) {
            apiCfg.setUpdateTime(new Date());
            res = apiCfgMapper.updateByPrimaryKeySelective(apiCfg);
        } else {
            AssertUtil.notEmpty("API名称", apiCfg.getApiName());
            AssertUtil.notEmpty("API版本", apiCfg.getApiVersion());
            ApiCfg cfg = getApi(apiCfg.getApiName(), apiCfg.getApiVersion());
            if (cfg != null) {
                throw new GatewayException(ErrorCode.ISP_SYSTEM_ERROR, "API已存在");
            }
            apiCfg.setCreateTime(new Date());
            res = apiCfgMapper.insertSelective(apiCfg);
        }
        // 刷Redis缓存
        apiConfigRedisCache.refreshApiCfgCache(apiCfg.getId());
        return res;
    }

    @Transactional
    @Override
    public int changeApiState(String userName, Integer id, Integer nowState, Integer newState) {
        ApiCfgExample example = new ApiCfgExample();
        example.createCriteria().andIdEqualTo(id)
                .andDeletedEqualTo(CommonConstant.VALID).andStateEqualTo(nowState);
        ApiCfg apiCfg = new ApiCfg();
        apiCfg.setUpdater(userName);
        apiCfg.setUpdateTime(new Date());
        apiCfg.setState(newState);
        int res = apiCfgMapper.updateByExampleSelective(apiCfg, example);

        // 刷Redis缓存
        apiConfigRedisCache.refreshApiCfgCache(id);
        return res;
    }

    @Override
    public List<ApiCfg> listApi() {
        ApiCfgExample example = new ApiCfgExample();
        example.createCriteria().andDeletedEqualTo(CommonConstant.VALID);
        return apiCfgMapper.selectByExample(example);
    }

    @Override
    public PageInfo<ApiCfgExt> pagerApi(String apiName, String apiDesc, Integer groupId, PageRequest pageRequest) {
        try {
            PageHelper.startPage(pageRequest.getPage(), pageRequest.getRows());
            String order = " t1.state, t1.id desc";
            if (StringUtils.isNotBlank(pageRequest.getSortColumn())) {
                if (StringUtils.equals(pageRequest.getSortColumn(), "group_name")) {
                    order = " t2." + pageRequest.getSortColumn() + " " + pageRequest.getOrder();
                } else {
                    order = " t1." + pageRequest.getSortColumn() + " " + pageRequest.getOrder();
                }
            }
            List<ApiCfgExt> apiCfgList = apiCfgExtMapper.queryApiDetail(apiName, apiDesc, groupId, order);
            PageInfo<ApiCfgExt> pageInfo = new PageInfo<>(apiCfgList);
            return pageInfo;
        } finally {
            PageHelper.clearPage();
        }
    }

    @Override
    public ApiCfg findApiById(Integer id) {
        return apiCfgMapper.selectByPrimaryKey(id);
    }

    @Override
    public ApiCfg getApi(String apiName, String version) {
        ApiCfgExample example = new ApiCfgExample();
        example.createCriteria().andDeletedEqualTo(CommonConstant.VALID)
                .andApiNameEqualTo(apiName).andApiVersionEqualTo(version);
        List<ApiCfg> apiList = apiCfgMapper.selectByExample(example);
        return CollectionUtils.isEmpty(apiList) ? null : apiList.get(0);
    }

    @Transactional
    @Override
    public int saveApiParam(Integer apiId, List<ApiParamCfg> apiParamCfgs) {
        int res = saveApiParamNoTransaction(apiId, apiParamCfgs);
        // 刷redis缓存
        apiConfigRedisCache.refreshApiCfgCache(apiId);
        return res;
    }

    /**
     * 保存API参数无事务
     *
     * @param apiId
     * @param apiParamCfgs
     * @return
     */
    private int saveApiParamNoTransaction(Integer apiId, List<ApiParamCfg> apiParamCfgs) {
        int res = 0;
        List<ApiParamCfg> allParamCfgs = listApiParam(apiId);
        List<Integer> allIds = allParamCfgs.stream().map(ApiParamCfg::getId).collect(Collectors.toList());
        String userName = null;
        for (ApiParamCfg apiParamCfg : apiParamCfgs) {
            AssertUtil.notEmpty("参数名", apiParamCfg.getParamName());
            AssertUtil.notEmpty("参数描述", apiParamCfg.getParamDesc());
            AssertUtil.notEmpty("参数类型", apiParamCfg.getParamType());
            AssertUtil.notNull("参数来源", apiParamCfg.getParamSource());
            AssertUtil.notNull("是否必填", apiParamCfg.getIsRequired());
            AssertUtil.notNull("参数顺序", apiParamCfg.getParamIndex());
            apiParamCfg.setApiId(apiId);

            ApiParamCfg cfg = queryApiParamByName(apiParamCfg.getApiId(), apiParamCfg.getParamName());
            if (apiParamCfg.getId() != null) {
                if (cfg != null && cfg.getId().intValue() != apiParamCfg.getId()) {
                    throw new GatewayException(ErrorCode.ISP_SYSTEM_ERROR, "参数名已存在");
                }
                allIds.remove(apiParamCfg.getId());
                userName = apiParamCfg.getUpdater();
                apiParamCfg.setUpdateTime(new Date());
                res += apiParamCfgMapper.updateByPrimaryKeySelective(apiParamCfg);
            } else {
                if (cfg != null) {
                    throw new GatewayException(ErrorCode.ISP_SYSTEM_ERROR, "参数名已存在");
                }
                apiParamCfg.setCreateTime(new Date());
                res += apiParamCfgMapper.insertSelective(apiParamCfg);
            }
        }

        // 处理删除的参数
        if (allIds.size() > 0) {
            for (Integer id : allIds) {
                ApiParamCfg delParamCfg = new ApiParamCfg();
                delParamCfg.setId(id);
                delParamCfg.setDeleted(CommonConstant.DELETED);
                delParamCfg.setUpdater(userName);
                apiParamCfgMapper.updateByPrimaryKeySelective(delParamCfg);
            }
        }
        return res;
    }

    @Override
    public List<ApiParamCfg> listApiParam(Integer apiId) {
        ApiParamCfgExample example = new ApiParamCfgExample();
        example.createCriteria().andDeletedEqualTo(CommonConstant.VALID).andApiIdEqualTo(apiId);
        example.setOrderByClause(" param_index ");
        return apiParamCfgMapper.selectByExample(example);
    }

    @Override
    public ApiParamCfg queryApiParamByName(Integer apiId, String name) {
        ApiParamCfgExample example = new ApiParamCfgExample();
        example.createCriteria().andParamNameEqualTo(name)
                .andDeletedEqualTo(CommonConstant.VALID)
                .andApiIdEqualTo(apiId);
        List<ApiParamCfg> apiParamCfgList = apiParamCfgMapper.selectByExample(example);
        return CollectionUtils.isEmpty(apiParamCfgList) ? null : apiParamCfgList.get(0);
    }

    @Transactional
    @Override
    public int saveApiResult(Integer apiId, List<ApiResultCfg> apiResults) {
        int res = saveApiResultNoTransaction(apiId, apiResults);
        // 刷redis缓存
        apiConfigRedisCache.refreshApiCfgCache(apiId);
        return res;
    }

    /**
     * 保存API结果对象无事务
     *
     * @param apiId
     * @param apiResults
     * @return
     */
    private int saveApiResultNoTransaction(Integer apiId, List<ApiResultCfg> apiResults) {
        int res = 0;
        List<ApiResultCfg> allResultCfgs = listApiResult(apiId);
        List<Integer> allIds = allResultCfgs.stream().map(ApiResultCfg::getId).collect(Collectors.toList());
        String userName = null;
        for (ApiResultCfg apiResultCfg : apiResults) {
            AssertUtil.notEmpty("字段名", apiResultCfg.getFieldName());
            AssertUtil.notEmpty("字段描述", apiResultCfg.getFieldDesc());
            AssertUtil.notEmpty("映射字段", apiResultCfg.getBackFieldName());
            AssertUtil.notNull("字段顺序", apiResultCfg.getFieldIndex());
            apiResultCfg.setApiId(apiId);
            ApiResultCfg resultCfg = queryApiResultByName(apiResultCfg.getApiId(), apiResultCfg.getFieldName());
            if (apiResultCfg.getId() != null) {
                if (resultCfg != null && resultCfg.getId().intValue() != apiResultCfg.getId()) {
                    throw new GatewayException(ErrorCode.ISP_SYSTEM_ERROR, "字段名已存在");
                }
                allIds.remove(apiResultCfg.getId());
                userName = apiResultCfg.getUpdater();
                apiResultCfg.setUpdateTime(new Date());
                res += apiResultCfgMapper.updateByPrimaryKeySelective(apiResultCfg);
            } else {
                if (resultCfg != null) {
                    throw new GatewayException(ErrorCode.ISP_SYSTEM_ERROR, "字段名已存在");
                }
                apiResultCfg.setCreateTime(new Date());
                res += apiResultCfgMapper.insertSelective(apiResultCfg);
            }
        }

        // 处理删除的参数
        if (allIds.size() > 0) {
            for (Integer id : allIds) {
                ApiResultCfg delResultCfg = new ApiResultCfg();
                delResultCfg.setId(id);
                delResultCfg.setDeleted(CommonConstant.DELETED);
                delResultCfg.setUpdater(userName);
                apiResultCfgMapper.updateByPrimaryKeySelective(delResultCfg);
            }
        }
        return res;
    }

    @Override
    public ApiResultCfg queryApiResultByName(Integer apiId, String name) {
        ApiResultCfgExample example = new ApiResultCfgExample();
        example.createCriteria().andDeletedEqualTo(CommonConstant.VALID)
                .andApiIdEqualTo(apiId).andFieldNameEqualTo(name);
        List<ApiResultCfg> resultCfgList = apiResultCfgMapper.selectByExample(example);
        return CollectionUtils.isEmpty(resultCfgList) ? null : resultCfgList.get(0);
    }

    @Override
    public List<ApiResultCfg> listApiResult(Integer apiId) {
        ApiResultCfgExample example = new ApiResultCfgExample();
        example.createCriteria().andDeletedEqualTo(CommonConstant.VALID).andApiIdEqualTo(apiId);
        example.setOrderByClause(" field_index ");
        return apiResultCfgMapper.selectByExample(example);
    }

    @Transactional
    @Override
    public int saveApiAllCfg(ApiCfg apiCfg, List<ApiParamCfg> apiParamCfgs, List<ApiResultCfg> apiResultCfgs) {
        int res = saveApi(apiCfg);
        if (apiCfg.getId() != null) {
            if (apiParamCfgs != null) {
                saveApiParamNoTransaction(apiCfg.getId(), apiParamCfgs);
            }
            if (apiParamCfgs != null) {
                saveApiResultNoTransaction(apiCfg.getId(), apiResultCfgs);
            }
        }
        // 刷redis缓存
        apiConfigRedisCache.refreshApiCfgCache(apiCfg.getId());
        return res;
    }
}
