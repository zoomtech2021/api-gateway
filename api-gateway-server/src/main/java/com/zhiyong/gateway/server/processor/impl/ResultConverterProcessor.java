package com.zhiyong.gateway.server.processor.impl;

import com.alibaba.fastjson.JSON;
import com.zhiyong.gateway.biz.model.ApiResult;
import com.zhiyong.gateway.common.exception.GatewayException;
import com.zhiyong.gateway.dal.domain.ApiResultCfg;
import com.zhiyong.gateway.server.context.ApiContext;
import com.zhiyong.gateway.server.processor.ApiProcessor;
import com.zhiyong.gateway.common.utils.TypeConvertUtil;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.utils.PojoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * @ClassName ResultConverterProcessor
 * @Description: 结果处理器
 * @Author 毛军锐
 * @Date 2020/11/25 下午7:39
 **/
@Component
public class ResultConverterProcessor implements ApiProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResultConverterProcessor.class);

    @Override
    public String getName() {
        return ResultConverterProcessor.class.getName();
    }

    @Override
    public void run(ApiContext context) throws GatewayException {
        // 构造新的响应结果
        ApiResult apiResult = ApiResult.builder().build();
        Object result = context.getApiResult();
        if (result != null && result instanceof Map) {
            Map map = (Map) result;
            apiResult.setCode((Integer) map.get("code"));
            apiResult.setMsg((String) map.get("msg"));
            apiResult.setData(map.get("data"));
        }
        if (apiResult.getCode() == null && apiResult.getMsg() == null
                && apiResult.getData() == null) {
            apiResult.setCode(200);
            apiResult.setData(result);
        }
        // 参数映射
        mappingResult(context, apiResult);
        // 回写结果对象
        context.setApiResult(apiResult);
    }

    /**
     * 结果参数映射
     *
     * @param context
     * @param apiResult
     */
    private void mappingResult(ApiContext context, ApiResult apiResult) {
        Object data = apiResult.getData();
        if (data == null) {
            return;
        }
        List<ApiResultCfg> apiResultCfgList = context.getApiResultCfgList();
        if (CollectionUtils.isEmpty(apiResultCfgList)) {
            return;
        }
        // List
        if (data instanceof List) {
            data = handList(data, apiResultCfgList);
        } else {
            data = paramConvert(data, apiResultCfgList);
        }
        apiResult.setData(data);
    }

    /**
     * 处理List类型的结果
     *
     * @param data
     * @param apiResultCfgList
     * @return
     */
    private Object handList(Object data, List<ApiResultCfg> apiResultCfgList) {
        List result = new ArrayList();
        try {
            List dataList = (List) data;
            for (Object obj : dataList) {
                Object newObj = paramConvert(obj, apiResultCfgList);
                result.add(newObj);
            }
        } catch (Exception e) {
            LOGGER.error("The list pojo realize error:", e);
        }
        return result;
    }

    /**
     * 处理javabean和map类型的结果
     *
     * @param data
     * @param apiResultCfgList
     * @return
     */
    private Object paramConvert(Object data, List<ApiResultCfg> apiResultCfgList) {
        Map<String, Object> result = new LinkedHashMap<>();
        Map dataMap = null;
        if (PojoUtils.isPojo(data.getClass())) {
            dataMap = JSON.parseObject(JSON.toJSONString(data), Map.class);
        } else if (data instanceof Map) {
            dataMap = (Map) data;
        } else {
            return data;
        }
        // 替换参数名和参数值类型
        for (ApiResultCfg cfg : apiResultCfgList) {
            Object value = dataMap.get(cfg.getBackFieldName());
            if (StringUtils.isNotBlank(cfg.getFieldType())) {
                try {
                    value = TypeConvertUtil.convert(value, cfg.getFieldType());
                } catch (Exception e) {
                    // ignore;
                }
            }
            result.put(cfg.getFieldName(), value);
        }
        return result;
    }
}
