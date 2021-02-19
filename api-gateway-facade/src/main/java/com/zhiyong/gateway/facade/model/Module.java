package com.zhiyong.gateway.facade.model;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * @ClassName Module
 * @Description: TODO
 * @Author 毛军锐
 * @Date 2020/12/3 下午11:44
 **/
@Data
public class Module implements Serializable {
    private static final long serialVersionUID = 1486339284747460095L;
    private Long id;
    private String name;
    private List<Course> courses;
    private List<Live> lives;
}
