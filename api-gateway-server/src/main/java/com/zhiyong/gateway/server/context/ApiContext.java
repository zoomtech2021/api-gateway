package com.zhiyong.gateway.server.context;

import com.zhiyong.gateway.common.enums.ApiType;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.zhiyong.gateway.biz.model.ApiParams;
import com.zhiyong.gateway.biz.model.ApiRequest;
import com.zhiyong.gateway.biz.model.ApiUser;
import com.zhiyong.gateway.dal.domain.ApiCfg;
import com.zhiyong.gateway.dal.domain.ApiParamCfg;
import com.zhiyong.gateway.dal.domain.ApiResultCfg;
import com.zhiyong.gateway.dal.domain.AppCfg;

import lombok.Builder;
import lombok.Data;

/**
 * @ClassName ApiContext
 * @Description: API操作上下文
 * @Author 毛军锐
 * @Date 2020/11/25 上午11:38
 **/
@Data
@Builder
public class ApiContext {
    /**
     * 请求唯一ID
     */
    private String requestId;

    /**
     * HttpServletRequest
     */
    private HttpServletRequest request;

    /**
     * HttpServletResponse
     */
    private HttpServletResponse response;

    /**
     * API公共请求参数
     */
    private ApiRequest apiRequest;

    /**
     * API类型
     */
    private ApiType apiType;

    /**
     * API接口参数
     */
    private ApiParams apiParams;

    /**
     * API响应结果
     */
    private Object apiResult;

    /**
     * 请求开始时间
     */
    private long timestamp;

    /**
     * 应用配置信息
     */
    private AppCfg appCfg;

    /**
     * API配置信息
     */
    private ApiCfg apiCfg;

    /**
     * 请求参数配置信息
     */
    private List<ApiParamCfg> apiParamCfgList;

    /**
     * 响应结果配置信息
     */
    private List<ApiResultCfg> apiResultCfgList;

    /**
     * 登录用户
     */
    private ApiUser apiUser;


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(requestId).append("]");
        if (apiRequest != null) {
            sb.append("[api=").append(apiRequest.getMethod()).append("] - ");
        }
        if (apiUser != null) {
            sb.append("[userId=").append(apiUser.getUserId()).append("]");
        }
        if (apiRequest != null) {
            sb.append(apiRequest.toString());
        }
        if (apiParams != null) {
            sb.append("[param=").append(JSON.toJSONString(apiParams.getSignParams())).append("]");
        }
        return sb.toString();
    }
}
