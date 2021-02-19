package com.zhiyong.gateway.biz.service;

import com.github.pagehelper.PageInfo;
import com.zhiyong.gateway.common.model.PageRequest;
import com.zhiyong.gateway.dal.domain.PojoTypeJson;
import java.util.List;

/**
 * @ClassName DataStructService
 * @Description: 数据结构服务
 * @Author 毛军锐
 * @Date 2020/12/3 下午1:43
 **/
public interface StructConfigService {

    /**
     * 保存数据结构类型json
     *
     * @param pojoTypeJson
     * @return
     */
    int savePojoTypeJson(PojoTypeJson pojoTypeJson);

    /**
     * 获取所有数据结构类型JSON
     *
     * @return
     */
    List<PojoTypeJson> listAll();

    /**
     * 获取所有数据结构类型JSON
     *
     * @return
     */
    List<PojoTypeJson> getValidList(String typeName);

    /**
     * 分页查询类型配置列表
     * @return
     */
    PageInfo<PojoTypeJson> pagerPojoTypeJson(String typeName, String typeDesc, PageRequest pageRequest);

    /**
     * 根据数据结构名查询数据结构配置
     * @param name
     * @return
     */
    PojoTypeJson queryPojoTypeJsonByName(String name);

    /**
     * 根据ID查询数据结构配置
     * @param id
     * @return
     */
    PojoTypeJson getPojoTypeJsonById(Integer id);

    /**
     * 变更状态
     * @param userName
     * @param id
     * @param nowState
     * @param newState
     * @return
     */
    int changeState(String userName, Integer id, Integer nowState, Integer newState);
}
