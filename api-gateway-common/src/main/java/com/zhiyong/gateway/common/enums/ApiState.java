package com.zhiyong.gateway.common.enums;

/**
 * @ClassName ApiState
 * @Description: TODO
 * @Author 毛军锐
 * @Date 2020/11/27 下午10:03
 **/
public enum ApiState {
    TEMP(0, "草稿"),
    UP_LINE(1, "已发布"),
    DOWN_LINE(2, "已下线"),
    ;

    private int code;
    private String desc;

    ApiState(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 状态机
     * @param oldState
     * @param newState
     * @return
     */
    public static boolean changeState(int oldState, int newState) {
        switch (oldState) {
            case 1: {
                if (newState == 0) {
                    return false;
                }
                return true;
            }
            case 2: {
                if (newState == 0 || newState == 1) {
                    return false;
                }
                return true;
            }
            default: {
                return true;
            }
        }
    }
}
