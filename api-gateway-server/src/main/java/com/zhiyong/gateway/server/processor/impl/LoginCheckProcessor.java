package com.zhiyong.gateway.server.processor.impl;

import com.zhiyong.gateway.common.exception.GatewayException;
import com.zhiyong.gateway.server.context.ApiContext;
import com.zhiyong.gateway.server.processor.ApiProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @ClassName LoginCheckProcessor
 * @Description: 登录检查处理器
 * @Author 毛军锐
 * @Date 2020/11/25 下午1:22
 **/
@Component
@Slf4j
public class LoginCheckProcessor implements ApiProcessor {

    @Override
    public String getName() {
        return LoginCheckProcessor.class.getName();
    }

    @Override
    public void run(ApiContext context) throws GatewayException {

    }
}
