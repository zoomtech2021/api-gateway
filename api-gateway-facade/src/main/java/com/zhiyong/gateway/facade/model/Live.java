package com.zhiyong.gateway.facade.model;

import java.io.Serializable;
import lombok.Data;

/**
 * @ClassName LiveModel
 * @Description: TODO
 * @Author 毛军锐
 * @Date 2020/12/3 下午11:42
 **/
@Data
public class Live implements Serializable {

    private static final long serialVersionUID = -4679912676096177203L;
    private Long id;
    private Course course;
    private Teacher teacher;
    private Question[] questions;
}
