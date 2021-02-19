package com.zhiyong.gateway.common.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

/**
 * @ClassName TypeConvertUtil
 * @Description: 数据类型转换工具类
 * @Author 毛军锐
 * @Date 2020/11/28 上午9:50
 **/
public class TypeConvertUtil {
    private static final ConversionService converter = new DefaultConversionService();
    private static final Map<String, Class> TYPE_REGISTER = new HashMap<>();

    static {
        TYPE_REGISTER.put("java.lang.String", String.class);
        TYPE_REGISTER.put("java.lang.Integer", Integer.class);
        TYPE_REGISTER.put("java.lang.Long", Long.class);
        TYPE_REGISTER.put("java.lang.Float", Float.class);
        TYPE_REGISTER.put("java.lang.Double", Double.class);
        TYPE_REGISTER.put("java.lang.Boolean", String.class);
        TYPE_REGISTER.put("java.lang.String[]", String[].class);
        TYPE_REGISTER.put("java.lang.Integer[]", Integer[].class);
        TYPE_REGISTER.put("java.lang.Long[]", Long[].class);
        TYPE_REGISTER.put("java.lang.Float[]", Float[].class);
        TYPE_REGISTER.put("java.lang.Double[]", Double[].class);
        TYPE_REGISTER.put("java.lang.Boolean[]", Boolean[].class);
        TYPE_REGISTER.put("java.util.List", ArrayList.class);
        TYPE_REGISTER.put("java.util.Set", HashSet.class);
        TYPE_REGISTER.put("java.util.Map", HashMap.class);
    }

    /**
     * 获取注册的java类型
     * @return
     */
    public static Map<String, Class> getTypeRegister() {
        return TYPE_REGISTER;
    }

    /**
     * 获取参数类型
     *
     * @param paramType
     * @return
     */
    public static Class getClassType(String paramType) {
        return TYPE_REGISTER.get(paramType);
    }

    /**
     * 转换对象类型
     *
     * @param source
     * @param targetType
     * @return
     */
    public static Object convert(Object source, String targetType) {
        if (source == null) {
            return null;
        }
        Class clazz = getClassType(targetType);
        if (clazz == null) {
            return source;
        }
        boolean canConvert = converter.canConvert(source.getClass(), clazz);
        if (canConvert) {
            Object value = converter.convert(source, clazz);
            return value;
        }
        return source;
    }

    /**
     * 解析JSON对象
     *
     * @param json
     * @return
     */
    public static Object parseJson(String json) {
        if (StringUtils.isBlank(json)) {
            return json;
        }
        try {
            return JSONObject.parseObject(json);
        } catch (Exception e) {
            try {
                return JSONArray.parseArray(json);
            } catch (Exception ex) {
                return json;
            }
        }
    }
}
