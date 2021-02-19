package com.zhiyong.gateway.facade.tools;

import com.google.gson.internal.$Gson$Types;
import com.google.gson.reflect.TypeToken;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @ClassName PojoTypeParser
 * @Description: 复杂对象参数类型解析器
 * @Author 毛军锐
 * @Date 2020/12/8 下午3:26
 **/
public class PojoTypeParser<T> extends TypeToken {
    private Set<String> typeSet = new HashSet<>();
    private Class<? super T> rawType;
    private Type type;

    /**
     * 构造器
     */
    public PojoTypeParser() {
        super();
        type = super.getType();
        rawType = super.getRawType();
    }

    /**
     * 执行POJO对象及参数类型解析操作
     *
     * @param typeName 类型名称
     * @return
     */
    public TypeStruct run(String typeName) {
        TypeStruct typeStruct = new TypeStruct();
        typeStruct.setName(typeName);
        Class rootClass = parseRootClass(typeStruct, this.type, this.rawType);
        typeStruct.setDesc(getPojoTypeDesc(rootClass));
        parsePojoParamType(rootClass, typeStruct);
        return typeStruct;
    }

    /**
     * 获取类上的swagger注解类型描述
     *
     * @param clazz
     * @return
     */
    private String getPojoTypeDesc(Class<?> clazz) {
        boolean hasAnnotation = clazz.isAnnotationPresent(ApiModel.class);
        if (hasAnnotation) {
            ApiModel apiModel = clazz.getAnnotation(ApiModel.class);
            return apiModel.value().equals("") ? apiModel.description() : apiModel.value();
        }
        return null;
    }

    /**
     * 获取类属性字段上的swagger注解类型描述
     *
     * @param field
     * @return
     */
    private String getPojoFieldDesc(Field field) {
        boolean hasAnnotation = field.isAnnotationPresent(ApiModelProperty.class);
        if (hasAnnotation) {
            ApiModelProperty apiModelProperty = field.getAnnotation(ApiModelProperty.class);
            return apiModelProperty.value().equals("") ? apiModelProperty.name() : apiModelProperty.value();
        }
        return null;
    }

    /**
     * 解析POJO根类型
     *
     * @param typeStruct
     * @param rootType
     * @param rootClass
     * @return
     */
    private Class parseRootClass(TypeStruct typeStruct, Type rootType, Class rootClass) {
        if (rootClass.isArray()) {
            typeStruct.setType(rootClass.getTypeName());
            rootClass = rootClass.getComponentType();
            return rootClass;
        }

        if (rootType instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) rootType;
            String ptName = pt.getRawType().getTypeName();
            Type argumentType = null;
            if (ptName.equals(List.class.getName())
                    || ptName.equals(Set.class.getName())
                    || ptName.equals(Collection.class.getName())) {
                argumentType = pt.getActualTypeArguments()[0];
            } else if (ptName.equals(Map.class.getName())) {
                argumentType = pt.getActualTypeArguments()[1];
            } else {
                throw new RuntimeException("The genericType is not support!");
            }
            if (typeStruct.getCollectionType() == null) {
                typeStruct.setCollectionType(ptName);
            }
            rootClass = $Gson$Types.getRawType(argumentType);
            if (rootClass.isArray() || argumentType instanceof ParameterizedType) {
                return parseRootClass(typeStruct, argumentType, rootClass);
            }
        }

        typeStruct.setType(rootClass.getName());
        return rootClass;
    }

    /**
     * 解析POJO字段类型
     *
     * @param clazz
     * @param typeStruct
     */
    private void parsePojoParamType(Class clazz, TypeStruct typeStruct) {
        Map<String, Field> map = getBeanPropertyFields(clazz);
        for (String name : map.keySet()) {
            Field field = map.get(name);
            Class fieldType = field.getType();
            if (fieldType.isEnum()) {
                continue;
            }
            if (fieldType.isArray()) {
                fieldType = fieldType.getComponentType();
            }
            if (isPrimitive(fieldType)) {
                continue;
            }
            Type genericType = field.getGenericType();
            if (fieldType.isAssignableFrom(Collection.class)
                    || fieldType.isAssignableFrom(List.class)
                    || fieldType.isAssignableFrom(Set.class)) {
                handleListGenericTypeParamType(field, genericType, typeStruct, false);
            } else if (fieldType.isAssignableFrom(Map.class)) {
                handleMapGenericTypeParamType(field, genericType, typeStruct, true);
            } else if (isPojo(fieldType)) {
                handlePojoType(field, typeStruct, field.getType(), false);
            }
        }
    }

    /**
     * 处理Map泛型
     *
     * @param field       字段名
     * @param genericType 泛型类型
     * @param typeStruct  类型结果描述语言
     * @param isMap       字段是否是Map
     */
    private void handleMapGenericTypeParamType(Field field, Type genericType, TypeStruct typeStruct,
                                               boolean isMap) {
        if (genericType instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) genericType;
            Type type = pt.getActualTypeArguments()[1];
            if (type instanceof ParameterizedType) {
                Type rawType = ((ParameterizedType) type).getRawType();
                if (rawType != null && rawType.getTypeName().equals(Map.class.getName())) {
                    handleMapGenericTypeParamType(field, type, typeStruct, isMap);
                } else {
                    handleListGenericTypeParamType(field, type, typeStruct, isMap);
                }
            } else {
                handlePojoType(field, typeStruct, type, isMap);
            }
        }
    }

    /**
     * 处理List泛型
     *
     * @param field
     * @param genericType
     * @param typeStruct
     * @param isMap
     */
    private void handleListGenericTypeParamType(Field field, Type genericType, TypeStruct typeStruct,
                                                boolean isMap) {
        if (genericType == null) {
            return;
        }
        if (genericType instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) genericType;
            Type type = pt.getActualTypeArguments()[0];
            if (type instanceof ParameterizedType) {
                Type rawType = ((ParameterizedType) type).getRawType();
                if (rawType != null && rawType.getTypeName().equals(Map.class.getName())) {
                    handleMapGenericTypeParamType(field, type, typeStruct, true);
                } else {
                    handleListGenericTypeParamType(field, type, typeStruct, isMap);
                }
            } else {
                handlePojoType(field, typeStruct, type, isMap);
            }
        }
    }

    /**
     * 处理POJO对象字段的类型
     *
     * @param field
     * @param typeStruct
     * @param type
     * @param isMap
     */
    private void handlePojoType(Field field, TypeStruct typeStruct, Type type, boolean isMap) {
        Class typeClass = (Class) type;
        if (isPojo(typeClass)) {
            if (isPrimitive(typeClass)) {
                return;
            }
            if (typeClass.isArray()) {
                typeClass = typeClass.getComponentType();
            }

            TypeStruct child = new TypeStruct();
            child.setName(field.getName());
            child.setDesc(getPojoFieldDesc(field));
            child.setType(typeClass.getName());
            if (isMap) {
                child.setCollectionType(Map.class.getName());
            }

            List<TypeStruct> children = typeStruct.getChildren();
            if (children == null) {
                children = new ArrayList<>();
                typeStruct.setChildren(children);
            }
            children.add(child);

            // 防止属性递归引用死循环
            if (!typeSet.contains(typeClass.getName())) {
                typeSet.add(typeClass.getName());
                // 遍历子属性
                parsePojoParamType(typeClass, child);
            }
        }
    }

    /**
     * 判断是否是一个POJO
     *
     * @param cls
     * @return
     */
    private boolean isPojo(Class<?> cls) {
        return !isPrimitives(cls)
                && !Collection.class.isAssignableFrom(cls)
                && !Map.class.isAssignableFrom(cls);
    }

    /**
     * 是否是基础类型
     *
     * @param cls
     * @return
     */
    private boolean isPrimitives(Class<?> cls) {
        if (cls.isArray()) {
            return isPrimitive(cls.getComponentType());
        }
        return isPrimitive(cls);
    }

    /**
     * 是否是基础类型
     *
     * @param cls
     * @return
     */
    private boolean isPrimitive(Class<?> cls) {
        return cls.isPrimitive() || cls == String.class || cls == Boolean.class || cls == Character.class
                || Number.class.isAssignableFrom(cls) || Date.class.isAssignableFrom(cls);
    }

    /**
     * 获取对象属性集合
     *
     * @param cl
     * @return
     */
    private Map<String, Field> getBeanPropertyFields(Class cl) {
        Map<String, Field> properties = new HashMap<String, Field>();
        for (; cl != null; cl = cl.getSuperclass()) {
            Field[] fields = cl.getDeclaredFields();
            for (Field field : fields) {
                if (Modifier.isTransient(field.getModifiers())
                        || Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                field.setAccessible(true);
                properties.put(field.getName(), field);
            }
        }
        return properties;
    }
}
