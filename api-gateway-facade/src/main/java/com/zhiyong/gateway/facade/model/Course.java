package com.zhiyong.gateway.facade.model;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * @ClassName Course
 * @Description: TODO
 * @Author 毛军锐
 * @Date 2020/12/3 下午11:42
 **/
@Data
public class Course implements Serializable {
    private static final long serialVersionUID = -5044959402198845281L;

    private Long id;
    private String name;
    private String desc;
    private int type;
    private int sku;

}
