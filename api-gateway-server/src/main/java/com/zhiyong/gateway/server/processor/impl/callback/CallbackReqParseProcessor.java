package com.zhiyong.gateway.server.processor.impl.callback;

import com.zhiyong.gateway.biz.model.ApiRequest;
import com.zhiyong.gateway.common.enums.ErrorCode;
import com.zhiyong.gateway.common.exception.GatewayException;
import com.zhiyong.gateway.common.utils.CommonUtil;
import com.zhiyong.gateway.server.constant.RequestHeaderConstant;
import com.zhiyong.gateway.server.context.ApiContext;
import com.zhiyong.gateway.server.processor.ApiProcessor;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

/**
 * @ClassName CallbackReqParseProcessor
 * @Description: 回调接口请求参数解析处理器
 * @Author 毛军锐
 * @Date 2020/11/25 上午11:58
 **/
@Component
public class CallbackReqParseProcessor implements ApiProcessor {

    @Override
    public String getName() {
        return CallbackReqParseProcessor.class.getName();
    }

    @Override
    public void run(ApiContext context) throws GatewayException {
        HttpServletRequest request = context.getRequest();
        ApiRequest apiRequest = context.getApiRequest();
        apiRequest.setClientIp(CommonUtil.getClientIp(request));
        apiRequest.setReqMethod(request.getMethod());
        apiRequest.setHost(request.getHeader(RequestHeaderConstant.HOST));
        apiRequest.setContentType(request.getContentType());
        apiRequest.setReferer(request.getHeader(RequestHeaderConstant.REFERER));
        apiRequest.setUserAgent(request.getHeader(RequestHeaderConstant.USER_AGENT));
        apiRequest.setPath(request.getPathInfo());
        apiRequest.setQueryStr(request.getQueryString());
        String version = request.getParameter(RequestHeaderConstant.VERSION);
        apiRequest.setVersion(StringUtils.isEmpty(version) ? "1.0" : version);
        apiRequest.setTenantId(request.getParameter(RequestHeaderConstant.TENANT_ID));
        // 参数校验
        checkRequestType(apiRequest);
        // 回写上下文
        context.setApiRequest(apiRequest);
    }

    /**
     * 校验请求类型
     *
     * @param apiRequest
     */
    private void checkRequestType(ApiRequest apiRequest) {
        if (!StringUtils.equals(apiRequest.getReqMethod(), HttpMethod.GET.name())
                && !StringUtils.equals(apiRequest.getReqMethod(), HttpMethod.POST.name())) {
            throw new GatewayException(ErrorCode.ISV_NOT_SUPPORT_REQ_TYPE);
        }
    }
}
