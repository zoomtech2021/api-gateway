package com.zhiyong.gateway.server.processor.impl;

import com.zhiyong.gateway.biz.model.ApiRequest;
import com.zhiyong.gateway.common.enums.ErrorCode;
import com.zhiyong.gateway.common.exception.GatewayException;
import com.zhiyong.gateway.common.util.AssertUtil;
import com.zhiyong.gateway.common.utils.CommonUtil;
import com.zhiyong.gateway.server.constant.RequestHeaderConstant;
import com.zhiyong.gateway.server.context.ApiContext;
import com.zhiyong.gateway.server.processor.ApiProcessor;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

/**
 * @ClassName RequestParserProcessor
 * @Description: 请求参数解析处理器
 * @Author 毛军锐
 * @Date 2020/11/25 上午11:58
 **/
@Component
public class RequestParseProcessor implements ApiProcessor {

    @Override
    public String getName() {
        return RequestParseProcessor.class.getName();
    }

    @Override
    public void run(ApiContext context) throws GatewayException {
        HttpServletRequest request = context.getRequest();
        ApiRequest apiRequest = new ApiRequest();
        apiRequest.setClientIp(CommonUtil.getClientIp(request));
        apiRequest.setReqMethod(request.getMethod());
        apiRequest.setHost(request.getHeader(RequestHeaderConstant.HOST));
        apiRequest.setContentType(request.getContentType());
        apiRequest.setReferer(request.getHeader(RequestHeaderConstant.REFERER));
        apiRequest.setUserAgent(request.getHeader(RequestHeaderConstant.USER_AGENT));
        apiRequest.setPath(request.getPathInfo());
        apiRequest.setQueryStr(request.getQueryString());
        apiRequest.setDeviceId(request.getParameter(RequestHeaderConstant.DEVICE_ID));
        apiRequest.setOs(request.getParameter(RequestHeaderConstant.OS));
        apiRequest.setOsVersion(request.getParameter(RequestHeaderConstant.OS_VERSION));
        apiRequest.setAppKey(request.getParameter(RequestHeaderConstant.APP_KEY));
        apiRequest.setMethod(request.getParameter(RequestHeaderConstant.METHOD));
        apiRequest.setSession(request.getParameter(RequestHeaderConstant.SESSION));
        apiRequest.setSign(request.getParameter(RequestHeaderConstant.SIGN));
        apiRequest.setTimestamp(request.getParameter(RequestHeaderConstant.TIMESTAMP));
        apiRequest.setVersion(request.getParameter(RequestHeaderConstant.VERSION));
        apiRequest.setTenantId(request.getParameter(RequestHeaderConstant.TENANT_ID));
        // 参数校验
        AssertUtil.notEmpty(RequestHeaderConstant.APP_KEY, apiRequest.getAppKey());
        AssertUtil.notEmpty(RequestHeaderConstant.METHOD, apiRequest.getMethod());
        AssertUtil.notEmpty(RequestHeaderConstant.SIGN, apiRequest.getSign());
        AssertUtil.notEmpty(RequestHeaderConstant.VERSION, apiRequest.getVersion());
        AssertUtil.notEmpty(RequestHeaderConstant.TIMESTAMP, apiRequest.getTimestamp());
        checkRequestType(apiRequest);
        checkRequestTimestamp(apiRequest);
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

    /**
     * 校验请求时间戳
     *
     * @param apiRequest
     */
    private void checkRequestTimestamp(ApiRequest apiRequest) {
        try {
            String timestamp = CommonUtil.urlDecode(apiRequest.getTimestamp());
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // 请求过期时间默认10分钟
            if (System.currentTimeMillis() - df.parse(timestamp).getTime() > 10 * 60 * 1000) {
                throw new GatewayException(ErrorCode.ISV_ILLEGAL_ARGUMENT, "请求已过期");
            }
            if (System.currentTimeMillis() - df.parse(timestamp).getTime() < -10 * 60 * 1000) {
                throw new GatewayException(ErrorCode.ISV_ILLEGAL_ARGUMENT, "timestamp非有效时间");
            }
            apiRequest.setTimestamp(timestamp);
        } catch (ParseException e) {
            throw new GatewayException(ErrorCode.ISV_ILLEGAL_ARGUMENT, "timestamp格式错误");
        }
    }
}
