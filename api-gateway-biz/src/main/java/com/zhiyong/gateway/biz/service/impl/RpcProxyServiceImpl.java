package com.zhiyong.gateway.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.zhiyong.gateway.biz.model.ServiceRequest;
import com.zhiyong.gateway.biz.service.RpcProxyService;
import com.zhiyong.gateway.common.enums.ErrorCode;
import com.zhiyong.gateway.common.exception.GatewayException;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.utils.ReferenceConfigCache;
import org.apache.dubbo.rpc.service.GenericService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @ClassName RpcProxyServiceImpl
 * @Description: 远程RPC服务调用代理实现
 * @Author 毛军锐
 * @Date 2020/11/24 下午8:52
 **/
@Service
public class RpcProxyServiceImpl implements RpcProxyService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcProxyServiceImpl.class);
    private static final Logger DIGEST_LOGGER = LoggerFactory.getLogger("RPC-DIGEST-LOGGER");

    @Override
    public Object invoke(ServiceRequest request) throws GatewayException {
        // 获取远程服务
        Object result = null;
        boolean success = false;

        long startTime = System.currentTimeMillis();
        String serviceName = request.getInterfaceName() + "#" + request.getMethod() + ":" + request.getVersion();
        try {
            GenericService genericService = getServiceInstance(request.getInterfaceName(),
                    request.getVersion(), request.getTimeout());
            // 远程服务调用
            String[] paramTypes = request.getParamTypes();
            if (paramTypes == null) {
                paramTypes = new String[]{};
            }
            Object[] paramValues = request.getParamValues();
            if (paramValues == null) {
                paramValues = new Object[]{};
            }
            result = genericService.$invoke(request.getMethod(), paramTypes, paramValues);
            success = true;
        } catch (Exception e) {
            LOGGER.error("远程服务[" + serviceName + "]调用失败：", e);
            throw new GatewayException(ErrorCode.ISP_SYSTEM_ERROR, "远程服务调用失败：" + parseException(e));
        } finally {
            printRpcLog(request, startTime, success);
        }
        return result;
    }

    /**
     * 打印RPC调用日志
     */
    private void printRpcLog(ServiceRequest request, long startTime, boolean success) {
        try {
            long endTime = System.currentTimeMillis();
            DIGEST_LOGGER.info("Invoke remote method, request={}, success={}, spends={}",
                    JSON.toJSONString(request), success, (endTime - startTime));
        } catch (Exception e) {
            //ignore
        }
    }

    /**
     * dubbo远程调用常见异常处理
     *
     * @param ex
     * @return
     */
    private String parseException(Exception ex) {
        String msg = ex.getMessage();
        if (StringUtils.contains(msg, "java.lang.NoSuchMethodException")) {
            return "API对应远程方法不存在或参数列表不匹配（包括：参数个数、顺序、类型必须保持一致）:java.lang.NoSuchMethodException";
        } else if (StringUtils.contains(msg, "org.apache.dubbo.remoting.TimeoutException")) {
            return "网关调用远程方法超时了:org.apache.dubbo.remoting.TimeoutException";
        }
        return ex.getMessage();
    }

    /**
     * 获取dubbo服务的实例引用
     *
     * @return
     */
    private GenericService getServiceInstance(String interfaceName, String version, int timeout) {
        ReferenceConfig<GenericService> reference = new ReferenceConfig<>();
        reference.setInterface(interfaceName);
        reference.setVersion(version);
        reference.setGeneric(true);
        reference.setRetries(0);
        reference.setTimeout(timeout);
        GenericService genericService = ReferenceConfigCache.getCache().get(reference);
        if (genericService == null) {
            ReferenceConfigCache.getCache().destroy(reference);
            throw new GatewayException(ErrorCode.ISP_API_PROVIDER_NOT_FOUND);
        }
        return genericService;
    }
}
