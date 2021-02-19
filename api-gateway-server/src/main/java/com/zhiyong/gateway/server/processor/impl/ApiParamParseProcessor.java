package com.zhiyong.gateway.server.processor.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.zhiyong.gateway.admin.model.TypeStruct;
import com.zhiyong.gateway.biz.cache.ApiConfigCache;
import com.zhiyong.gateway.biz.model.ApiParams;
import com.zhiyong.gateway.biz.model.ApiRequest;
import com.zhiyong.gateway.common.constant.CommonConstant;
import com.zhiyong.gateway.common.enums.ErrorCode;
import com.zhiyong.gateway.common.exception.GatewayException;
import com.zhiyong.gateway.common.utils.CommonUtil;
import com.zhiyong.gateway.common.utils.TypeConvertUtil;
import com.zhiyong.gateway.dal.domain.ApiParamCfg;
import com.zhiyong.gateway.dal.domain.PojoTypeJson;
import com.zhiyong.gateway.server.constant.RequestHeaderConstant;
import com.zhiyong.gateway.server.context.ApiContext;
import com.zhiyong.gateway.server.processor.ApiProcessor;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

/**
 * @ClassName ApiParamParseProcessor
 * @Description: Api参数解析处理器
 * @Author 毛军锐
 * @Date 2020/11/25 下午1:37
 **/
@Component
public class ApiParamParseProcessor implements ApiProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiParamParseProcessor.class);

    @Resource
    private ApiConfigCache apiConfigCache;

    @Override
    public String getName() {
        return ApiParamParseProcessor.class.getName();
    }

    @Override
    public void run(ApiContext context) throws GatewayException {
        ApiRequest apiRequest = context.getApiRequest();
        String reqMethod = apiRequest.getReqMethod();
        ApiParams apiParams = null;

        // 请求方式检查
        if (!StringUtils.equals(reqMethod, context.getApiCfg().getHttpMethod())) {
            throw new GatewayException(ErrorCode.ISV_ARGUMENT_TYPE_ERROR, "该API仅支持"
                    + context.getApiCfg().getHttpMethod() + "类型请求");
        }
        // Get请求
        if (StringUtils.equals(reqMethod, HttpMethod.GET.name())) {
            apiParams = parseGetRequest(context);
        } else {
            //post请求
            apiParams = parsePostRequest(context);
        }
        // 回填参数
        context.setApiParams(apiParams);
    }

    /**
     * 解析Post请求参数
     *
     * @param context
     * @return
     */
    private ApiParams parsePostRequest(ApiContext context) {
        List<ApiParamCfg> paramCfgList = context.getApiParamCfgList();
        if (CollectionUtils.isEmpty(paramCfgList)) {
            return ApiParams.builder().build();
        }
        String contentType = context.getApiRequest().getContentType();
        if (StringUtils.containsIgnoreCase(contentType, RequestHeaderConstant.CONTENT_TYPE_FORM)) {
            return parseGetRequest(context);
        } else if (StringUtils.containsIgnoreCase(contentType, RequestHeaderConstant.CONTENT_TYPE_JSON)) {
            return handleJsonBodyParam(context);
        } else {
            throw new GatewayException(ErrorCode.ISV_ILLEGAL_ARGUMENT, "POST请求的CONTENT-TYPE不支持");
        }
    }

    /**
     * 处理application/json方式请求参数
     *
     * @throws IOException
     */
    private ApiParams handleJsonBodyParam(ApiContext context) {
        try {
            List<ApiParamCfg> paramCfgList = context.getApiParamCfgList();
            Map<String, String> signParams = new HashMap<>();
            List<String> paramTypeList = Lists.newArrayList();
            List<Object> paramValueList = Lists.newArrayList();
            for (ApiParamCfg paramCfg : paramCfgList) {
                String paramName = paramCfg.getParamName();
                boolean bodyParam = paramCfg.getParamSource() == CommonConstant.YES;
                PojoTypeJson pojoTypeJson = apiConfigCache.getPojoTypeJson(paramCfg.getParamTypeId());
                if (pojoTypeJson == null) {
                    throw new GatewayException(ErrorCode.ISP_CONFIG_ERROR, "参数[" + paramName + "]未找到类型配置");
                }
                // URL参数
                if (!bodyParam) {
                    handUrlReqParam(context, signParams, paramTypeList, paramValueList, paramCfg,
                            paramName, pojoTypeJson);
                } else {
                    handleBodyReqParam(context, paramTypeList, paramValueList, pojoTypeJson, paramCfg);
                }
            }
            return ApiParams.builder().paramTypes(paramTypeList.toArray(new String[paramTypeList.size()]))
                    .paramValues(paramValueList.toArray()).signParams(signParams).build();
        } catch (Exception e) {
            LOGGER.error("处理Body请求参数失败:", e);
            throw new GatewayException(ErrorCode.ISP_SYSTEM_ERROR, "处理Body请求参数失败:" + e.getMessage());
        }
    }

    /**
     * 处理Body体里面的请求参数
     *
     * @throws IOException
     */
    private void handleBodyReqParam(ApiContext context, List<String> paramTypeList, List<Object> paramValueList,
                                    PojoTypeJson pojoTypeJson, ApiParamCfg paramCfg) throws IOException {
        String paramValue = CommonUtil.getRequestBody(context.getRequest());
        if (paramCfg.getIsRequired() == CommonConstant.NEED
                && StringUtils.isBlank(paramValue)) {
            throw new GatewayException(ErrorCode.ISV_ILLEGAL_ARGUMENT, "参数[" + paramCfg.getParamName() + "]是必填项");
        }
        // java基础类型
        if (pojoTypeJson.getIsPojo() == CommonConstant.NO) {
            paramTypeList.add(pojoTypeJson.getTypeName());
            paramValueList.add(paramValue);
        } else {
            // POJO
            String typeJson = pojoTypeJson.getTypeJson();
            if (StringUtils.isBlank(typeJson)) {
                throw new GatewayException(ErrorCode.ISP_CONFIG_ERROR, "缺少POJO类型的数据结构配置");
            }
            TypeStruct typeStruct = JSON.parseObject(typeJson, TypeStruct.class);
            // 只需要在参数类型的根类型上指定，相当于接口方法参数自身是否是一个带有结合的复杂Pojo对象
            String collectionType = typeStruct.getCollectionType();
            String paramType = typeStruct.getType();

            if (StringUtils.isNotBlank(collectionType)) {
                if (!StringUtils.equals(collectionType, "java.util.List")
                        && !StringUtils.equals(collectionType, "java.util.Set")
                        && !StringUtils.equals(collectionType, "java.util.Map")
                        && !StringUtils.endsWith(collectionType, "[]")) {
                    throw new GatewayException(ErrorCode.ISP_CONFIG_ERROR, "集合类型只支持配List、Set、Map、数组");
                }
                paramTypeList.add(collectionType);
            } else {
                paramTypeList.add(paramType);
            }

            Object object = TypeConvertUtil.parseJson(paramValue);
            if (object == null) {
                paramValueList.add(null);
            } else {
                paramValueList.add(object);
                // 复杂对象子参数类型处理
                handleObjectParamType(object, typeStruct);
            }
        }
    }

    private void handleObjectParamType(Object object, TypeStruct typeStruct) {
        if (object instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) object;
            for (int ii = 0; ii < jsonArray.size(); ii++) {
                handleObjectParamType(jsonArray.get(ii), typeStruct);
            }
        } else if (object instanceof JSONObject) {
            handleJsonObjectParamType(object, typeStruct);
        }
    }

    private void handleJsonObjectParamType(Object object, TypeStruct typeStruct) {
        String objCollectionClass = typeStruct.getCollectionType();
        String objClass = typeStruct.getType();
        JSONObject jsonValue = (JSONObject) object;
        if (StringUtils.equalsIgnoreCase(objCollectionClass, "java.util.Map")) {
            typeStruct.setCollectionType(null);
            handleTypeStructMap(jsonValue, typeStruct);
        } else {
            jsonValue.put("class", objClass);
            handleTypeStructChildren(jsonValue, typeStruct);
        }
    }

    private void handleTypeStructMap(JSONObject jsonValue, TypeStruct typeStruct) {
        Map<String, TypeStruct> objClassMap = typeStruct.getMapping();
        if (!MapUtils.isEmpty(objClassMap)) {
            // 通配符，所有key对应的value是同一种类型
            TypeStruct genericTypeStruct = objClassMap.get("*");
            for (String key : jsonValue.keySet()) {
                Object mapValue = jsonValue.get(key);
                if (mapValue == null) {
                    continue;
                }
                if (genericTypeStruct != null) {
                    handleObjectParamType(mapValue, genericTypeStruct);
                } else {
                    TypeStruct mapObjTypeStruct = objClassMap.get(key);
                    if (mapObjTypeStruct == null) {
                        continue;
                    }
                    handleObjectParamType(mapValue, mapObjTypeStruct);
                }
            }
        } else {
            for (String key : jsonValue.keySet()) {
                Object mapValue = jsonValue.get(key);
                if (mapValue == null) {
                    continue;
                }
                handleObjectParamType(mapValue, typeStruct);
            }
        }
    }

    private void handleTypeStructChildren(JSONObject jsonValue, TypeStruct typeStruct) {
        List<TypeStruct> typeStructChildren = typeStruct.getChildren();
        if (CollectionUtils.isEmpty(typeStructChildren)) {
            return;
        }
        for (TypeStruct child : typeStructChildren) {
            Object objVal = jsonValue.get(child.getName());
            handleObjectParamType(objVal, child);
        }
    }

    /**
     * 处理URL请求的参数
     */
    private void handUrlReqParam(ApiContext context, Map<String, String> signParams, List<String> paramTypeList,
                                 List<Object> paramValueList, ApiParamCfg paramCfg, String paramName,
                                 PojoTypeJson pojoTypeJson) {
        paramTypeList.add(pojoTypeJson.getTypeName());
        String[] paramValues = context.getRequest().getParameterValues(paramName);
        // 参数必填
        if (paramCfg.getIsRequired() == CommonConstant.NEED) {
            if (paramValues == null || paramValues.length == 0) {
                throw new GatewayException(ErrorCode.ISV_ILLEGAL_ARGUMENT, "参数[" + paramName + "]是必填项");
            }
        }
        if (paramValues != null && paramValues.length == 1
                && !paramCfg.getParamType().endsWith("[]")
                && !paramCfg.getParamType().equals("java.util.List")
                && !paramCfg.getParamType().equals("java.util.Set")) {
            paramValueList.add(paramValues[0]);
            signParams.put(paramName, paramValues[0]);
        } else {
            paramValueList.add(paramValues);
            signParams.put(paramName, StringUtils.join(paramValues, ","));
        }
    }

    /**
     * 解析Get请求参数
     *
     * @param context
     * @return
     */
    private ApiParams parseGetRequest(ApiContext context) {
        List<ApiParamCfg> paramCfgList = context.getApiParamCfgList();
        if (CollectionUtils.isEmpty(paramCfgList)) {
            return ApiParams.builder().build();
        }
        Map<String, String[]> paramMap = context.getRequest().getParameterMap();
        String[] paramTypes = new String[paramCfgList.size()];
        Object[] paramValues = new Object[paramCfgList.size()];
        Map<String, String> signParams = new HashMap<>();
        int index = 0;
        for (ApiParamCfg paramCfg : paramCfgList) {
            String paramName = paramCfg.getParamName();
            PojoTypeJson pojoTypeJson = apiConfigCache.getPojoTypeJson(paramCfg.getParamTypeId());
            if (pojoTypeJson == null || pojoTypeJson.getIsPojo() == CommonConstant.YES) {
                throw new GatewayException(ErrorCode.ISV_ILLEGAL_ARGUMENT,
                        "参数[" + paramName + "]配置的类型[" + paramCfg.getParamType() + "]当前请求方式不支持");
            }
            // 参数类型
            paramTypes[index] = pojoTypeJson.getTypeName();
            // 参数值解析
            String[] paramValue = paramMap.get(paramName);
            // url参数decode
            if (paramValue != null) {
                for (int ii = 0; ii < paramValue.length; ii++) {
                    paramValue[ii] = CommonUtil.urlDecode(paramValue[ii]);
                }
            }
            // 参数必填
            if (paramCfg.getIsRequired() == CommonConstant.NEED) {
                if (paramValue == null || paramValue.length == 0) {
                    throw new GatewayException(ErrorCode.ISV_ILLEGAL_ARGUMENT, "参数[" + paramName + "]是必填项");
                }
            }
            if (paramValue != null && paramValue.length == 1
                    && !paramCfg.getParamType().endsWith("[]")
                    && !paramCfg.getParamType().equals("java.util.List")
                    && !paramCfg.getParamType().equals("java.util.Set")) {
                paramValues[index] = paramValue[0];
                signParams.put(paramName, paramValue[0]);
            } else {
                paramValues[index] = paramValue;
                signParams.put(paramName, StringUtils.join(paramValue, ","));
            }
            index++;
        }
        return ApiParams.builder().paramTypes(paramTypes).paramValues(paramValues)
                .signParams(signParams).build();
    }
}
