package com.zhiyong.gateway.facade.model;

import java.io.Serializable;
import lombok.Data;

/**
 * @ClassName Answer
 * @Description: TODO
 * @Author 毛军锐
 * @Date 2020/12/5 上午11:24
 **/
@Data
public class Answer implements Serializable {
    private static final long serialVersionUID = 2609403705968421420L;

    private Long id;
    private String indexNo;
    private String desc;
    private int score;
    private String imgUrl;
    private String videoUrl;
}
