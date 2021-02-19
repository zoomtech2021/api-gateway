package com.zhiyong.gateway.server.processor.impl;

import com.zhiyong.gateway.biz.model.ApiParams;
import com.zhiyong.gateway.biz.model.ApiRequest;
import com.zhiyong.gateway.common.enums.ErrorCode;
import com.zhiyong.gateway.common.exception.GatewayException;
import com.zhiyong.gateway.common.utils.CommonUtil;
import com.zhiyong.gateway.dal.domain.AppCfg;
import com.zhiyong.gateway.server.constant.RequestHeaderConstant;
import com.zhiyong.gateway.server.context.ApiContext;
import com.zhiyong.gateway.server.processor.ApiProcessor;
import com.zhiyong.gateway.common.utils.SignUtil;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @ClassName SignCheckProcessor
 * @Description: 接口签名检查处理器
 * @Author 毛军锐
 * @Date 2020/11/25 下午1:30
 **/
@Component
public class SignCheckProcessor implements ApiProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SignCheckProcessor.class);

    @Override
    public String getName() {
        return SignCheckProcessor.class.getName();
    }

    @Override
    public void run(ApiContext context) throws GatewayException {
        ApiRequest apiRequest = context.getApiRequest();
        Map<String, String> params = new HashMap<>();
        // 公共参数
        params.put(RequestHeaderConstant.METHOD, apiRequest.getMethod());
        params.put(RequestHeaderConstant.APP_KEY, apiRequest.getAppKey());
        //params.put(RequestHeaderConstant.SESSION, apiRequest.getSession());
        params.put(RequestHeaderConstant.TIMESTAMP, CommonUtil.urlEncode(apiRequest.getTimestamp()));
        params.put(RequestHeaderConstant.VERSION, apiRequest.getVersion());
        // 需要签名的业务参数：
        // get请求所有业务参数都需要加入，数组参数值用逗号连接。
        // post请求表单类型的参数按get方式处理；其它类型的参数如：附件、Body流都不作为签名参数
        ApiParams apiParams = context.getApiParams();
        if (apiParams != null && MapUtils.isNotEmpty(apiParams.getSignParams())) {
            for (Map.Entry<String, String> entry : apiParams.getSignParams().entrySet()) {
                String paramVal = entry.getValue();
                if (StringUtils.isNotBlank(paramVal)) {
                    paramVal = CommonUtil.urlEncode(paramVal);
                }
                params.put(entry.getKey(), paramVal);
            }
        }
        AppCfg appCfg = context.getAppCfg();
        String sign = SignUtil.signTopRequest(params, appCfg.getAppSecret(), SignUtil.SIGN_METHOD_HMAC);
        if (!StringUtils.equals(apiRequest.getSign(), sign)) {
            LOGGER.warn("======The request sign check fail, reqSign={}, sign={}", apiRequest.getSign(), sign);
            throw new GatewayException(ErrorCode.ISV_SIGN_NO_PASS);
        }
    }
}
