package com.zhiyong.gateway.facade.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.Data;

/**
 * @ClassName Question
 * @Description: TODO
 * @Author 毛军锐
 * @Date 2020/12/3 下午11:45
 **/
@Data
public class Question implements Serializable {
    private static final long serialVersionUID = 2515965040073080345L;

    private Long id;
    private String indexNo;
    private int answerNum;
    private int optionType;
    private int required;
    private String desc;
    private String[] imgUrls;
    private String[] videoUrls;
    private Map<String, List<Answer>> answers;
    private List<List<Answer>> answerAll;
}
