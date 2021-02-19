package com.zhiyong.gateway.server.processor;

import com.zhiyong.gateway.biz.model.ApiResult;
import com.zhiyong.gateway.common.enums.ErrorCode;
import com.zhiyong.gateway.common.exception.GatewayException;
import com.zhiyong.gateway.server.context.ApiContext;
import com.zhiyong.gateway.server.processor.impl.ApiConfigLoadProcessor;
import com.zhiyong.gateway.server.processor.impl.ApiParamParseProcessor;
import com.zhiyong.gateway.server.processor.impl.AuthCheckProcessor;
import com.zhiyong.gateway.server.processor.impl.FlowControlProcessor;
import com.zhiyong.gateway.server.processor.impl.LoginCheckProcessor;
import com.zhiyong.gateway.server.processor.impl.RequestParseProcessor;
import com.zhiyong.gateway.server.processor.impl.ResultConverterProcessor;
import com.zhiyong.gateway.server.processor.impl.ServiceInvokeProcessor;
import com.zhiyong.gateway.server.processor.impl.SignCheckProcessor;
import com.zhiyong.gateway.server.processor.impl.TenantContextProcessor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @ClassName ApiProcessorChain
 * @Description: API请求处理链
 * @Author 毛军锐
 * @Date 2020/11/25 下午1:03
 **/
@Component
public final class ApiProcessorChain implements ApplicationContextAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiProcessorChain.class);
    private Map<String, ApiProcessor> processors = new HashMap<>();
    private List<String> registerProcessor = new ArrayList<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, ApiProcessor> beanMap = applicationContext.getBeansOfType(ApiProcessor.class);
        for (ApiProcessor processor : beanMap.values()) {
            processors.put(processor.getName(), processor);
        }
    }

    @PostConstruct
    private void register() {
        // 参数解析
        registerProcessor.add(RequestParseProcessor.class.getName());
        registerProcessor.add(ApiConfigLoadProcessor.class.getName());
        registerProcessor.add(ApiParamParseProcessor.class.getName());
        // 验证+执行
        registerProcessor.add(SignCheckProcessor.class.getName());
        registerProcessor.add(FlowControlProcessor.class.getName());
        registerProcessor.add(TenantContextProcessor.class.getName());
        registerProcessor.add(LoginCheckProcessor.class.getName());
        registerProcessor.add(AuthCheckProcessor.class.getName());
        registerProcessor.add(ServiceInvokeProcessor.class.getName());
        registerProcessor.add(ResultConverterProcessor.class.getName());
        LOGGER.info("====register gateway api processor completed!");
    }

    /**
     * API处理器执行器
     *
     * @param context
     * @return
     */
    public ApiResult execute(ApiContext context) {
        for (String name : registerProcessor) {
            ApiProcessor processor = processors.get(name);
            if (processor == null) {
                throw new GatewayException(ErrorCode.ISP_SYSTEM_ERROR);
            }
            processor.run(context);
        }
        ApiResult apiResult = (ApiResult) context.getApiResult();
        apiResult.setRequestId(context.getRequestId());
        return apiResult;
    }
}
