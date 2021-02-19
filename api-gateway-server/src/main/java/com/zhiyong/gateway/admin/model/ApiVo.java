package com.zhiyong.gateway.admin.model;

import lombok.Builder;
import lombok.Data;

/**
 * @ClassName ApiVo
 * @Description: TODO
 * @Author 毛军锐
 * @Date 2020/12/10 下午6:13
 **/
@Data
@Builder
public class ApiVo {
    private Integer id;
    private String method;
    private String apiName;
    private String apiDesc;
    private String apiVersion;
    private Integer apiType;
    private Integer needLogin;
    private String httpMethod;
}
