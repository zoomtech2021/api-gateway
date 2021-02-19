package com.zhiyong.gateway.server.controller;

import com.zhiyong.gateway.biz.model.ApiRequest;
import com.zhiyong.gateway.common.enums.ApiType;
import com.zhiyong.gateway.server.processor.CallbackProcessorChain;
import java.util.concurrent.Executor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.zhiyong.gateway.biz.model.ApiResult;
import com.zhiyong.gateway.biz.service.MonitorService;
import com.zhiyong.gateway.common.enums.ErrorCode;
import com.zhiyong.gateway.common.exception.GatewayException;
import com.zhiyong.gateway.common.utils.CommonUtil;
import com.zhiyong.gateway.server.context.ApiContext;
import com.zhiyong.gateway.server.processor.ApiProcessorChain;

/**
 * @ClassName GatewayApiController
 * @Description: 网关API统一请求入口
 * @Author 毛军锐
 * @Date 2020/11/25 上午10:57
 **/
@RestController
@RequestMapping("/router")
public class GatewayApiController {
    private static final Logger LOGGER = LoggerFactory.getLogger(GatewayApiController.class);

    @Resource
    private Executor threadExecutor;
    @Resource
    private ApiProcessorChain apiProcessorChain;
    @Resource
    private CallbackProcessorChain callbackProcessorChain;
    @Resource
    private MonitorService monitorService;

    /**
     * API接口路由
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/rest", method = {RequestMethod.POST, RequestMethod.GET})
    public ApiResult routerRestApi(HttpServletRequest request, HttpServletResponse response) {
        ApiContext context = ApiContext.builder()
                .timestamp(System.currentTimeMillis())
                .requestId(CommonUtil.generateUid())
                .apiType(ApiType.API)
                .request(request)
                .response(response)
                .build();

        boolean success = true;
        try {
            // 执行API调用链
            ApiResult result = apiProcessorChain.execute(context);
            return result;
        } catch (Exception e) {
            success = false;
            return handleException(context, e);
        } finally {
            doApiMonitor(context, success);
        }
    }

    /**
     * 回调接口路由
     *
     * @param request
     * @param response
     * @param method   回调API名称
     * @return
     */
    @RequestMapping(value = "/callback/{method}", method = {RequestMethod.POST, RequestMethod.GET})
    public Object routerCallbackApi(HttpServletRequest request, HttpServletResponse response,
                                       @PathVariable("method") String method) {
        ApiContext context = ApiContext.builder()
                .timestamp(System.currentTimeMillis())
                .requestId(CommonUtil.generateUid())
                .apiType(ApiType.CALL_BACK)
                .request(request)
                .response(response)
                .build();
        ApiRequest apiRequest = new ApiRequest();
        apiRequest.setMethod(method);
        apiRequest.setAppKey("third_callback");
        context.setApiRequest(apiRequest);

        boolean success = true;
        try {
            // 执行API调用链
            ApiResult result = callbackProcessorChain.execute(context);
            return result.getData();
        } catch (Exception e) {
            success = false;
            return handleException(context, e);
        } finally {
            doApiMonitor(context, success);
        }
    }

    /**
     * 处理异常情况
     *
     * @param context
     * @param ex
     * @return
     */
    private ApiResult handleException(ApiContext context, Exception ex) {
        ApiResult apiResult = null;
        // session异常不需要添加errorCode.这样会导致前端显示错误信息时有问题.
        if (ex instanceof BizSessionException) {
            GatewayException ge = (GatewayException) ex;
            apiResult = ApiResult.buildErrorResult(ge.getErrorCode().getState(), ex.getMessage());
            LOGGER.warn("{} - 失败原因：{}", context.toString(), ge.getMessage());
        } else if (ex instanceof GatewayException) {
            GatewayException ge = (GatewayException) ex;
            String errorMsg = ge.getErrorCode().getCode() + "：" + ge.getMessage();
            apiResult = ApiResult.buildErrorResult(ge.getErrorCode().getState(), errorMsg);
            LOGGER.warn("{} - 失败原因：{}", context.toString(), errorMsg);
        } else {
            LOGGER.error(context.toString() + " - 异常：", ex);
            apiResult = ApiResult.buildErrorResult(ErrorCode.ISP_SYSTEM_ERROR.getState(),
                    ErrorCode.ISP_SYSTEM_ERROR.getDesc());
        }
        apiResult.setRequestId(context.getRequestId());
        return apiResult;
    }

    /**
     * API调用监控
     *
     * @param context
     * @param success
     */
    public void doApiMonitor(ApiContext context, boolean success) {
        if (context.getApiRequest() == null) {
            return;
        }
        threadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String appKey = context.getApiRequest().getAppKey();
                    String method = context.getApiRequest().getMethod();
                    String version = context.getApiRequest().getVersion();
                    long spends = System.currentTimeMillis() - context.getTimestamp();
                    monitorService.countCallApi(method, version, spends, success);
                    LOGGER.info("API调用：[AppKey={}][Method={}:{}][结果={}][耗时={}ms]", appKey, method, version,
                            success, spends);
                } catch (Exception e) {
                    LOGGER.warn("异常执行监控统计出错：" + e.getMessage());
                }
            }
        });
    }
}
