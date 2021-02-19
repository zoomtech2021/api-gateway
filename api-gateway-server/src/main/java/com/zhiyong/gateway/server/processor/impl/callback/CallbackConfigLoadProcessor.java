package com.zhiyong.gateway.server.processor.impl.callback;

import com.zhiyong.gateway.biz.cache.ApiConfigCache;
import com.zhiyong.gateway.biz.model.ApiCache;
import com.zhiyong.gateway.biz.model.ApiRequest;
import com.zhiyong.gateway.common.enums.ApiState;
import com.zhiyong.gateway.common.enums.ErrorCode;
import com.zhiyong.gateway.common.exception.GatewayException;
import com.zhiyong.gateway.common.util.AssertUtil;
import com.zhiyong.gateway.dal.domain.AppCfg;
import com.zhiyong.gateway.server.constant.RequestHeaderConstant;
import com.zhiyong.gateway.server.context.ApiContext;
import com.zhiyong.gateway.server.processor.ApiProcessor;
import javax.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * @ClassName ApiConfigLoadProcessor
 * @Description: API配置加载处理器
 * @Author 毛军锐
 * @Date 2020/11/25 下午4:23
 **/
@Component
public class CallbackConfigLoadProcessor implements ApiProcessor {

    @Resource
    private ApiConfigCache apiConfigCache;

    @Override
    public String getName() {
        return CallbackConfigLoadProcessor.class.getName();
    }

    @Override
    public void run(ApiContext context) throws GatewayException {
        ApiRequest apiRequest = context.getApiRequest();
        ApiCache apiCache = apiConfigCache.getApi(apiRequest.getMethod(), apiRequest.getVersion());
        AssertUtil.notNull("The method or version not exist!", apiCache);
        String apiName = apiRequest.getMethod() + ":" + apiRequest.getVersion();
        AssertUtil.notNull(RequestHeaderConstant.METHOD + ":" + apiName, apiCache.getApiCfg());
        if (context.getApiType().getCode() != apiCache.getApiCfg().getApiType()) {
            throw new GatewayException(ErrorCode.ISV_NO_PERMISSION, "请求地址与API类型不匹配");
        }
        // API状态检查
        if (apiCache.getApiCfg().getState() != ApiState.UP_LINE.getCode()) {
            throw new GatewayException(ErrorCode.ISP_API_STATE_INVALID, "该API处于非发布状态，不能被调用");
        }
        // 回写参数
        context.setApiCfg(apiCache.getApiCfg());
        context.setApiParamCfgList(apiCache.getParamCfgs());
        context.setApiResultCfgList(apiCache.getResultCfgs());
    }
}
