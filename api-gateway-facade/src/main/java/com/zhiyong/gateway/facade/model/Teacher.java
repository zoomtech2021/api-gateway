package com.zhiyong.gateway.facade.model;

import java.io.Serializable;
import lombok.Data;

/**
 * @ClassName Teacher
 * @Description: TODO
 * @Author 毛军锐
 * @Date 2020/12/3 下午11:43
 **/
@Data
public class Teacher implements Serializable {
    private static final long serialVersionUID = 8017524915434020989L;
    private Long id;
    private String name;
    private String sku;
    private int age;
}
