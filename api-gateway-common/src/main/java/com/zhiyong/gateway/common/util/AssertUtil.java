package com.zhiyong.gateway.common.util;

import com.zhiyong.gateway.common.enums.ErrorCode;
import com.zhiyong.gateway.common.exception.GatewayException;
import org.apache.commons.lang3.StringUtils;

/**
 * @ClassName AssertUtil
 * @Description: 断言
 * @Author 毛军锐
 * @Date 2020/11/25 下午3:31
 **/
public class AssertUtil {

    /**
     * 非空校验
     * @param name
     * @param value
     */
    public static void notNull(String name, Object value) {
        if (value == null) {
            throw new GatewayException(ErrorCode.ISV_ILLEGAL_ARGUMENT, name + "不存在");
        }
    }

    /**
     * 非空校验
     * @param name
     * @param value
     */
    public static void notEmpty(String name, String value) {
        if (StringUtils.isBlank(value)) {
            throw new GatewayException(ErrorCode.ISV_ILLEGAL_ARGUMENT, name + "不能为空");
        }
    }

    /**
     * 数字校验
     * @param name
     * @param value
     */
    public static void isNumber(String name, String value) {
        if (StringUtils.isNumeric(value)) {
            throw new GatewayException(ErrorCode.ISV_ILLEGAL_ARGUMENT, name + "必须是数字格式");
        }
    }
}
