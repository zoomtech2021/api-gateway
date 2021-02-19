package com.zhiyong.gateway.biz.service;

import com.github.pagehelper.PageInfo;
import com.zhiyong.gateway.common.model.PageRequest;
import com.zhiyong.gateway.dal.domain.ApiCfg;
import com.zhiyong.gateway.dal.domain.ApiCfgExt;
import com.zhiyong.gateway.dal.domain.ApiGroupCfg;
import com.zhiyong.gateway.dal.domain.ApiParamCfg;
import com.zhiyong.gateway.dal.domain.ApiResultCfg;
import com.zhiyong.gateway.dal.domain.AppCfg;
import java.util.List;

/**
 * @ClassName ApiConfigService
 * @Description: API配置相关的服务接口
 * @Author 毛军锐
 * @Date 2020/11/24 下午6:34
 **/
public interface ApiConfigService {

    /**
     * 保存APP信息
     *
     * @param appCfg
     * @return
     */
    int saveApp(AppCfg appCfg);

    /**
     * 删除APP
     * @param appCfg
     * @return
     */
    int delApp(AppCfg appCfg);

    /**
     * 查询APP列表
     *
     * @return
     */
    List<AppCfg> listApp();

    /**
     * 分页查询APP列表
     * @return
     */
    PageInfo<AppCfg> pagerListApp(String appName, String appKey, PageRequest pageRequest);

    /**
     * 根据名称查询APP
     * @param name
     * @return
     */
    AppCfg queryAppByName(String name);

    /**
     * 根据APPKEY查询APP配置
     * @param appKey
     * @return
     */
    AppCfg getAppCfg(String appKey);

    /**
     * 根据ID查询APP配置
     * @param id
     * @return
     */
    AppCfg findAppById(Integer id);

    /**
     * 保存API分组
     *
     * @param apiGroupCfg
     * @return
     */
    int saveApiGroup(ApiGroupCfg apiGroupCfg);

    /**
     * 删除API分组
     * @param apiGroupCfg
     * @return
     */
    int delApiGroup(ApiGroupCfg apiGroupCfg);

    /**
     * 查询Api分组列表
     *
     * @return
     */
    List<ApiGroupCfg> listApiGroup();

    /**
     * 分页查询APP列表
     * @return
     */
    PageInfo<ApiGroupCfg> pagerApiGroup(String groupName, PageRequest pageRequest);

    /**
     * 根据名称查询ApiGroup
     * @param name
     * @return
     */
    ApiGroupCfg queryApiGroupByName(String name);

    /**
     * 保存API
     *
     * @param apiCfg
     * @return
     */
    int saveApi(ApiCfg apiCfg);

    /**
     * API状态变更
     * @param id
     * @param nowState
     * @param newState
     * @return
     */
    int changeApiState(String userName, Integer id, Integer nowState, Integer newState);

    /**
     * 查询Api列表
     *
     * @return
     */
    List<ApiCfg> listApi();

    /**
     * 分页查询API列表
     * @return
     */
    PageInfo<ApiCfgExt> pagerApi(String apiName, String apiDesc, Integer groupId, PageRequest pageRequest);

    /**
     * 查询API信息
     *
     * @param id
     * @return
     */
    ApiCfg findApiById(Integer id);

    /**
     * 查询API信息
     *
     * @param apiName
     * @param version
     * @return
     */
    ApiCfg getApi(String apiName, String version);

    /**
     * 保存api参数
     *
     * @param apiParamCfgs
     * @return
     */
    int saveApiParam(Integer apiId, List<ApiParamCfg> apiParamCfgs);

    /**
     * 查询Api参数列表
     *
     * @param apiId
     * @return
     */
    List<ApiParamCfg> listApiParam(Integer apiId);

    /**
     * 根据名称查询ApiParam
     * @param apiId
     * @param name
     * @return
     */
    ApiParamCfg queryApiParamByName(Integer apiId, String name);

    /**
     * 保存api结果字段
     *
     * @param apiResultCfgs
     * @return
     */
    int saveApiResult(Integer apiId, List<ApiResultCfg> apiResultCfgs);

    /**
     * 根据参数名查询响应结果字段配置
     * @param apiId
     * @param name
     * @return
     */
    ApiResultCfg queryApiResultByName(Integer apiId, String name);

    /**
     * 查询Api结果字段列表
     *
     * @param apiId
     * @return
     */
    List<ApiResultCfg> listApiResult(Integer apiId);

    /**
     * 全量保存API配置信息
     * @param apiCfg
     * @param apiParamCfgs
     * @param apiResultCfgs
     * @return
     */
    int saveApiAllCfg(ApiCfg apiCfg, List<ApiParamCfg> apiParamCfgs, List<ApiResultCfg> apiResultCfgs);
}
