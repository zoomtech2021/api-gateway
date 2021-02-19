package com.zhiyong.gateway.common.enums;

/**
 * @ClassName ApiType
 * @Description: TODO
 * @Author 毛军锐
 * @Date 2020/11/27 下午10:03
 **/
public enum ApiType {
    API(0, "普通API"),
    CALL_BACK(1, "回调API"),
    ;

    private int code;
    private String desc;

    ApiType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
