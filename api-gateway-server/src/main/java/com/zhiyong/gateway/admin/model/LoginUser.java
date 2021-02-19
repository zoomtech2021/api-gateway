package com.zhiyong.gateway.admin.model;

import java.io.Serializable;
import lombok.Builder;
import lombok.Data;

/**
 * @ClassName LoginUser
 * @Description: 登录用户
 * @Author 毛军锐
 * @Date 2020/11/25 下午10:52
 **/
@Data
@Builder
public class LoginUser implements Serializable {

    private static final long serialVersionUID = -4864623167237522411L;
    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 用户名
     */
    private String userName;
}
