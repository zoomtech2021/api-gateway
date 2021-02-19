package com.zhiyong.gateway.dal.domain;

import lombok.Data;

/**
 * @ClassName AuthExt
 * @Description: TODO
 * @Author 毛军锐
 * @Date 2020/12/22 下午2:22
 **/
@Data
public class AuthExt extends Auth {

    private String account;
    private String groupName;
}
