package com.zhiyong.gateway.facade.tools;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import java.util.List;
import lombok.Data;

/**
 * @ClassName TypeStruct
 * @Description: POJO复杂对象类型数据结构
 * @Author 毛军锐
 * @Date 2020/12/3 下午8:51
 **/
@Data
public class TypeStruct {

    /**
     * 属性ID
     */
    private Long id;

    /**
     * 属性名
     */
    @JSONField(ordinal = 1)
    private String name;

    /**
     * 注释
     */
    private String desc;

    /**
     * 属性类型（POJO：完整的类名）
     */
    @JSONField(ordinal = 2)
    private String type;

    /**
     * 只需要根类型指定：只能是集合类型
     */
    @JSONField(ordinal = 3)
    private String collectionType;

    /**
     * 属性类型集合
     */
    @JSONField(ordinal = 4)
    private List<TypeStruct> children;

    /**
     * 转配置JSON
     * @return
     */
    public String toJson() {
        if (this != null) {
            return JSON.toJSONString(this);
        }
        return "";
    }
}
