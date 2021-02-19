package com.zhiyong.gateway.common.enums;

/**
 * @ClassName ApiMetrics
 * @Description: 监控指标
 * @Author 毛军锐
 * @Date 2020/12/2 上午12:42
 **/
public enum ApiMetrics {

    TOTAL_COUNT("total_count", "总次数"),
    SUCC_COUNT("succ_count", "成功次数"),
    TOTAL_SPENDS("total_spends", "总耗时"),
    MAX_SPENDS("max_spends", "最大耗时"),
    ;

    private String code;
    private String desc;

    ApiMetrics(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
