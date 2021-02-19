package com.zhiyong.gateway.biz.model;

import lombok.Builder;
import lombok.Data;

/**
 * @ClassName ApiUser
 * @Description: api请求用户
 * @Author 毛军锐
 * @Date 2020/12/1 下午2:03
 **/
@Data
@Builder
public class ApiUser {
    private Long userId;
    private Long userName;
    private Long tenantId;
}
