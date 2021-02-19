package com.zhiyong.gateway.admin.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.zhiyong.gateway.admin.model.ApiVo;
import com.zhiyong.gateway.admin.model.LoginUser;
import com.zhiyong.gateway.biz.service.ApiConfigService;
import com.zhiyong.gateway.common.enums.ApiState;
import com.zhiyong.gateway.common.enums.ErrorCode;
import com.zhiyong.gateway.common.exception.GatewayException;
import com.zhiyong.gateway.common.model.ApiResponse;
import com.zhiyong.gateway.common.model.PageRequest;
import com.zhiyong.gateway.common.model.PageResult;
import com.zhiyong.gateway.common.util.AssertUtil;
import com.zhiyong.gateway.dal.domain.ApiCfg;
import com.zhiyong.gateway.dal.domain.ApiCfgExt;
import com.zhiyong.gateway.dal.domain.ApiParamCfg;
import com.zhiyong.gateway.dal.domain.ApiResultCfg;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName ApiController
 * @Description: TODO
 * @Author 毛军锐
 * @Date 2020/11/26 下午6:59
 **/
@RestController
@RequestMapping("/admin")
public class ApiController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiController.class);

    @Resource
    private ApiConfigService apiConfigService;

    /**
     * 保存API
     *
     * @param apiCfg
     * @return
     */
    @RequestMapping("/saveApiBase.do")
    public ApiResponse<Integer> saveApiBase(HttpServletRequest request, ApiCfg apiCfg) {
        int res = 0;
        final LoginUser loginUser = getLoginUser(request);

        try {
            if (!hasApiOptAuth(loginUser, apiCfg)) {
                return ApiResponse.buildCommonErrorResponse("没有该接口的操作权限");
            }
            if (apiCfg.getId() == null) {
                apiCfg.setCreator(loginUser.getUserName());
                apiCfg.setState(ApiState.TEMP.getCode());
            } else {
                apiCfg.setUpdater(loginUser.getUserName());
            }
            res = apiConfigService.saveApi(apiCfg);
        } catch (Exception e) {
            LOGGER.error("saveApiBase error:", e);
            return ApiResponse.buildCommonErrorResponse(e.getMessage());
        } finally {
            saveOptLog(loginUser, "保存API基本信息", JSON.toJSONString(apiCfg), res > 0);
        }
        return ApiResponse.buildSuccessResponse(apiCfg.getId());
    }

    /**
     * 变更API状态
     *
     * @param id
     * @return
     */
    @RequestMapping("/editApiState.do")
    public ApiResponse<Boolean> editApiState(HttpServletRequest request, Integer id,
                                             Integer nowState, Integer newState) {
        int res = 0;
        final LoginUser loginUser = getLoginUser(request);

        try {
            ApiCfg apiCfg = apiConfigService.findApiById(id);
            if (apiCfg == null) {
                throw new GatewayException(ErrorCode.ISV_ILLEGAL_ARGUMENT, "api不存在");
            }
            if (!hasApiOptAuth(loginUser, apiCfg)) {
                return ApiResponse.buildCommonErrorResponse("没有该接口的操作权限");
            }
            boolean check = ApiState.changeState(nowState, newState);
            if (!check) {
                throw new GatewayException(ErrorCode.ISV_ILLEGAL_ARGUMENT, "不允许的状态变更");
            }
            res = apiConfigService.changeApiState(loginUser.getUserName(), id, nowState, newState);
        } catch (Exception e) {
            LOGGER.error("editApiState error:", e);
            return ApiResponse.buildCommonErrorResponse(e.getMessage());
        } finally {
            saveOptLog(loginUser, "修改API状态", "API:" + id + ", 原状态：" + nowState
                    + "，新状态:" + newState, res > 0);
        }
        return ApiResponse.buildSuccessResponse(res > 0);
    }

    /**
     * 获取API列表
     *
     * @return
     */
    @RequestMapping("/getAllApiList.do")
    public List<ApiVo> getAllApiList(@RequestParam(value = "q", defaultValue = "") String apiName) {
        List<ApiVo> apiVos = Lists.newArrayList();
        PageRequest pageRequest = new PageRequest();
        pageRequest.setRows(100);
        pageRequest.setSort("apiName");
        PageInfo<ApiCfgExt> pageInfo = apiConfigService.pagerApi(apiName, null, null, pageRequest);
        List<ApiCfgExt> apiCfgList = pageInfo.getList();
        for (ApiCfgExt cfg : apiCfgList) {
            apiVos.add(ApiVo.builder()
                    .apiName(cfg.getApiName())
                    .apiDesc(cfg.getApiDesc())
                    .apiVersion(cfg.getApiVersion())
                    .id(cfg.getId())
                    .httpMethod(cfg.getHttpMethod())
                    .needLogin(cfg.getNeedLogin())
                    .method(cfg.getApiName() + ":" + cfg.getApiVersion())
                    .apiType(cfg.getApiType())
                    .build());
        }
        return apiVos;
    }

    /**
     * 获取API列表
     *
     * @return
     */
    @RequestMapping("/getApiList.do")
    public PageResult getApiList(PageRequest pageRequest, String apiName, String apiDesc, Integer groupId) {
        PageInfo<ApiCfgExt> pageInfo = apiConfigService.pagerApi(apiName, apiDesc, groupId, pageRequest);
        return PageResult.builder().total(pageInfo.getTotal())
                .rows(pageInfo.getList())
                .pageNum(pageInfo.getPageNum())
                .pageSize(pageInfo.getPageSize())
                .build();
    }

    /**
     * 获取API基本信息
     *
     * @param id
     * @return
     */
    @RequestMapping("/getApiBaseInfo.do")
    public ApiResponse<ApiCfg> getApiBaseInfo(Integer id) {
        try {
            ApiCfg apiCfg = apiConfigService.findApiById(id);
            return ApiResponse.buildSuccessResponse(apiCfg);
        } catch (Exception e) {
            LOGGER.error("getApiBaseInfo error:", e);
            return ApiResponse.buildCommonErrorResponse(e.getMessage());
        }
    }

    /**
     * 保存API请求参数列表
     *
     * @param apiId
     * @param apiParamsJson
     * @return
     */
    @RequestMapping("/saveApiParams.do")
    public ApiResponse<Boolean> saveApiParams(HttpServletRequest request, Integer apiId, String apiParamsJson) {
        int res = 0;
        final LoginUser loginUser = getLoginUser(request);

        try {
            if (apiId == null || StringUtils.isEmpty(apiParamsJson)) {
                return ApiResponse.buildCommonErrorResponse("没有发现需要保存的参数");
            }
            ApiCfg apiCfg = apiConfigService.findApiById(apiId);
            if (apiCfg == null) {
                throw new GatewayException(ErrorCode.ISV_ILLEGAL_ARGUMENT, "api不存在");
            }
            if (!hasApiOptAuth(loginUser, apiCfg)) {
                return ApiResponse.buildCommonErrorResponse("没有该接口的操作权限");
            }
            List<ApiParamCfg> apiParams = JSON.parseArray(apiParamsJson, ApiParamCfg.class);
            for (ApiParamCfg paramCfg : apiParams) {
                AssertUtil.notEmpty("参数名", paramCfg.getParamName());
                if (paramCfg.getId() == null) {
                    paramCfg.setCreator(loginUser.getUserName());
                } else {
                    paramCfg.setUpdater(loginUser.getUserName());
                }
            }
            res = apiConfigService.saveApiParam(apiId, apiParams);
        } catch (Exception e) {
            LOGGER.error("saveApiParams error:", e);
            return ApiResponse.buildCommonErrorResponse(e.getMessage());
        } finally {
            saveOptLog(loginUser, "保存API请求参数", "API:"
                    + apiId + "，请求参数:" + apiParamsJson, res > 0);
        }
        return ApiResponse.buildSuccessResponse(res > 0);
    }

    /**
     * 获取API请求参数信息
     *
     * @param apiId
     * @return
     */
    @RequestMapping("/getApiParamsList.do")
    public ApiResponse<List<ApiParamCfg>> getApiParamsList(Integer apiId) {
        try {
            if (apiId == null) {
                return ApiResponse.buildCommonErrorResponse("缺少请求参数");
            }
            List<ApiParamCfg> paramCfgs = apiConfigService.listApiParam(apiId);
            return ApiResponse.buildSuccessResponse(paramCfgs);
        } catch (Exception e) {
            LOGGER.error("getApiParamsList error:", e);
            return ApiResponse.buildCommonErrorResponse(e.getMessage());
        }
    }

    /**
     * 保存API响应参数列表
     *
     * @param apiId
     * @param apiResultJson
     * @return
     */
    @RequestMapping("/saveApiResult.do")
    public ApiResponse<Boolean> saveApiResult(HttpServletRequest request, Integer apiId, String apiResultJson) {
        int res = 0;
        final LoginUser loginUser = getLoginUser(request);

        try {
            if (apiId == null || StringUtils.isEmpty(apiResultJson)) {
                return ApiResponse.buildCommonErrorResponse("没有发现需要保存的参数");
            }
            ApiCfg apiCfg = apiConfigService.findApiById(apiId);
            if (apiCfg == null) {
                throw new GatewayException(ErrorCode.ISV_ILLEGAL_ARGUMENT, "api不存在");
            }
            if (!hasApiOptAuth(loginUser, apiCfg)) {
                return ApiResponse.buildCommonErrorResponse("没有该接口的操作权限");
            }
            List<ApiResultCfg> apiResults = JSON.parseArray(apiResultJson, ApiResultCfg.class);
            for (ApiResultCfg apiResult : apiResults) {
                if (apiResult.getId() == null) {
                    apiResult.setCreator(loginUser.getUserName());
                } else {
                    apiResult.setUpdater(loginUser.getUserName());
                }
            }
            res = apiConfigService.saveApiResult(apiId, apiResults);
        } catch (Exception e) {
            LOGGER.error("saveApiResult error:", e);
            return ApiResponse.buildCommonErrorResponse(e.getMessage());
        } finally {
            saveOptLog(loginUser, "保存API响应参数", "API:"
                    + apiId + "，响应参数:" + apiResultJson, res > 0);
        }
        return ApiResponse.buildSuccessResponse(res > 0);
    }

    /**
     * 获取API响应参数信息
     *
     * @param apiId
     * @return
     */
    @RequestMapping("/getApiResultList.do")
    public ApiResponse<List<ApiResultCfg>> getApiResultList(Integer apiId) {
        try {
            if (apiId == null) {
                return ApiResponse.buildCommonErrorResponse("缺少请求参数");
            }
            List<ApiResultCfg> resultCfgs = apiConfigService.listApiResult(apiId);
            return ApiResponse.buildSuccessResponse(resultCfgs);
        } catch (Exception e) {
            LOGGER.error("getApiResultList error:", e);
            return ApiResponse.buildCommonErrorResponse(e.getMessage());
        }
    }

    /**
     * 全量保存API信息
     *
     * @param apiBaseJson
     * @param apiResultJson
     * @param apiResultJson
     * @return
     */
    @RequestMapping("/saveApiAll.do")
    public ApiResponse<Integer> saveApiAll(HttpServletRequest request, String apiBaseJson,
                                           String apiParamsJson, String apiResultJson) {
        int res = 0;
        final LoginUser loginUser = getLoginUser(request);

        ApiCfg apiCfg = null;
        try {
            if (StringUtils.isBlank(apiBaseJson)) {
                return ApiResponse.buildCommonErrorResponse("没有发现需要保存的信息");
            }

            apiCfg = JSON.parseObject(apiBaseJson, ApiCfg.class);
            if (!hasApiOptAuth(loginUser, apiCfg)) {
                return ApiResponse.buildCommonErrorResponse("没有该接口的操作权限");
            }

            if (apiCfg.getId() == null) {
                apiCfg.setCreator(loginUser.getUserName());
                apiCfg.setState(ApiState.TEMP.getCode());
            } else {
                apiCfg.setUpdater(loginUser.getUserName());
            }

            List<ApiParamCfg> apiParams = null;
            if (StringUtils.isNotBlank(apiParamsJson)) {
                apiParams = JSON.parseArray(apiParamsJson, ApiParamCfg.class);
                for (ApiParamCfg paramCfg : apiParams) {
                    if (paramCfg.getId() == null) {
                        paramCfg.setCreator(loginUser.getUserName());
                    } else {
                        paramCfg.setUpdater(loginUser.getUserName());
                    }
                }
            }

            List<ApiResultCfg> apiResults = null;
            if (StringUtils.isNotBlank(apiParamsJson)) {
                apiResults = JSON.parseArray(apiResultJson, ApiResultCfg.class);
                for (ApiResultCfg apiResult : apiResults) {
                    if (apiResult.getId() == null) {
                        apiResult.setCreator(loginUser.getUserName());
                    } else {
                        apiResult.setUpdater(loginUser.getUserName());
                    }
                }
            }
            res = apiConfigService.saveApiAllCfg(apiCfg, apiParams, apiResults);
        } catch (Exception e) {
            LOGGER.error("saveApiAll error:", e);
            return ApiResponse.buildCommonErrorResponse(e.getMessage());
        } finally {
            saveOptLog(loginUser, "保存API全部信息", "基本信息:"
                    + apiBaseJson + "，请求参数:" + apiParamsJson + "，响应参数:" + apiResultJson, res > 0);
        }
        return ApiResponse.buildSuccessResponse(apiCfg.getId());
    }
}
