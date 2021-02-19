package com.zhiyong.gateway.admin.model;

import java.util.List;
import java.util.Map;
import lombok.Data;

/**
 * @ClassName TypeStruct
 * @Description: TODO
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
    private String name;

    /**
     * 注释
     */
    private String desc;

    /**
     * 属性类型（POJO：完整的类名）
     */
    private String type;

    /**
     * 只需要根类型指定：只能是集合类型
     */
    private String collectionType;

    /**
     * map类型属性
     * key:属性名 value:属性值类型
     */
    private Map<String, TypeStruct> mapping;

    /**
     * 属性类型集合
     */
    private List<TypeStruct> children;
}
