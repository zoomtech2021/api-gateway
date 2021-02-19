package com.zhiyong.gateway.common.constant;

/**
 * @ClassName CommonConstant
 * @Description: 公共常量定义
 * @Author 毛军锐
 * @Date 2020/11/26 上午11:52
 **/
public class CommonConstant {

    /**
     * 是
     */
    public static final int YES = 1;

    /**
     * 否
     */
    public static final int NO = 0;

    /**
     * 有效
     */
    public static final int VALID = 0;

    /**
     * 删除
     */
    public static final int DELETED = 1;

    /**
     * 需要
     */
    public static final int NEED = 0;

    /**
     * 不需要
     */
    public static final int NO_NEED = 1;

    /**
     * 不限制
     */
    public static final int NO_LIMIT = -1;

    /**
     * API配置缓存KEY前缀
     */
    public static final String APP_CFG_KEY_PRE = "gateway-api:app-cfg:";
    public static final String API_CFG_KEY_PRE = "gateway-api:api-cfg:";
    public static final String API_COUNT_KEY_PRE = "gateway-api:api-count:";
    public static final String DATA_STRUCT_JSON_PRE = "gateway-api:data-struct-json:";
}
