package com.zhiyong.gateway.biz.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhiyong.gateway.biz.cache.ApiConfigRedisCache;
import com.zhiyong.gateway.biz.service.StructConfigService;
import com.zhiyong.gateway.common.constant.CommonConstant;
import com.zhiyong.gateway.common.enums.ErrorCode;
import com.zhiyong.gateway.common.enums.TypeStructState;
import com.zhiyong.gateway.common.exception.GatewayException;
import com.zhiyong.gateway.common.model.PageRequest;
import com.zhiyong.gateway.dal.dao.PojoTypeJsonMapper;
import com.zhiyong.gateway.dal.domain.PojoTypeJson;
import com.zhiyong.gateway.dal.domain.PojoTypeJsonExample;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * @ClassName DataStructServiceImpl
 * @Description: TODO
 * @Author 毛军锐
 * @Date 2020/12/3 下午1:43
 **/
@Service
public class StructConfigServiceImpl implements StructConfigService {

    @Resource
    private PojoTypeJsonMapper pojoTypeJsonMapper;
    @Resource
    private ApiConfigRedisCache apiConfigRedisCache;

    @Transactional
    @Override
    public int savePojoTypeJson(PojoTypeJson pojoTypeJson) {
        int res = 0;
        PojoTypeJson cfg = queryPojoTypeJsonByName(pojoTypeJson.getTypeName());
        if (pojoTypeJson.getId() != null) {
            if (cfg != null && cfg.getId().intValue() != pojoTypeJson.getId().intValue()) {
                throw new GatewayException(ErrorCode.ISP_SYSTEM_ERROR, "数据类型名称已存在");
            }
            pojoTypeJson.setUpdateTime(new Date());
            res = pojoTypeJsonMapper.updateByPrimaryKeySelective(pojoTypeJson);
        } else {
            if (cfg != null) {
                throw new GatewayException(ErrorCode.ISP_SYSTEM_ERROR, "数据类型名称已存在");
            }
            pojoTypeJson.setIsPojo(CommonConstant.YES);
            pojoTypeJson.setCreateTime(new Date());
            res = pojoTypeJsonMapper.insertSelective(pojoTypeJson);
        }
        // 刷redis缓存
        apiConfigRedisCache.refreshPojoTypeJsonCache(pojoTypeJson.getId());
        return res;
    }

    @Override
    public List<PojoTypeJson> listAll() {
        PojoTypeJsonExample example = new PojoTypeJsonExample();
        PojoTypeJsonExample.Criteria criteria = example.createCriteria();
        criteria.andDeletedNotEqualTo(CommonConstant.DELETED);
        example.setOrderByClause(" id desc ");
        return pojoTypeJsonMapper.selectByExample(example);
    }

    @Override
    public List<PojoTypeJson> getValidList(String typeName) {
        PojoTypeJsonExample example = new PojoTypeJsonExample();
        PojoTypeJsonExample.Criteria criteria = example.createCriteria();
        criteria.andDeletedNotEqualTo(CommonConstant.DELETED)
                .andStateEqualTo(TypeStructState.UP_LINE.getCode());
        if (StringUtils.isNotBlank(typeName)) {
            criteria.andTypeNameLike("%" + typeName + "%");
        }
        example.setOrderByClause(" is_pojo, id desc ");
        return pojoTypeJsonMapper.selectByExample(example);
    }

    @Override
    public PageInfo<PojoTypeJson> pagerPojoTypeJson(String typeName, String typeDesc, PageRequest pageRequest) {
        try {
            PageHelper.startPage(pageRequest.getPage(), pageRequest.getRows());
            PojoTypeJsonExample example = new PojoTypeJsonExample();
            PojoTypeJsonExample.Criteria criteria = example.createCriteria();
            criteria.andDeletedNotEqualTo(CommonConstant.DELETED).andIsPojoEqualTo(1);
            if (StringUtils.isNotBlank(typeName)) {
                criteria.andTypeNameLike("%" + typeName + "%");
            }
            if (StringUtils.isNotBlank(typeDesc)) {
                criteria.andTypeDescLike("%" + typeDesc + "%");
            }
            if (StringUtils.isBlank(pageRequest.getSortColumn())) {
                example.setOrderByClause(" state, id desc ");
            } else {
                example.setOrderByClause(" " + pageRequest.getSortColumn() + " " + pageRequest.getOrder());
            }
            List<PojoTypeJson> typeJsons = pojoTypeJsonMapper.selectByExample(example);
            PageInfo<PojoTypeJson> pageInfo = new PageInfo<>(typeJsons);
            return pageInfo;
        } finally {
            PageHelper.clearPage();
        }
    }

    @Override
    public PojoTypeJson queryPojoTypeJsonByName(String name) {
        PojoTypeJsonExample example = new PojoTypeJsonExample();
        example.createCriteria().andDeletedNotEqualTo(CommonConstant.DELETED).andTypeNameEqualTo(name);
        List<PojoTypeJson> jsonList = pojoTypeJsonMapper.selectByExample(example);
        return CollectionUtils.isEmpty(jsonList) ? null : jsonList.get(0);
    }

    @Override
    public PojoTypeJson getPojoTypeJsonById(Integer id) {
        return pojoTypeJsonMapper.selectByPrimaryKey(id);
    }

    @Transactional
    @Override
    public int changeState(String userName, Integer id, Integer nowState, Integer newState) {
        PojoTypeJsonExample example = new PojoTypeJsonExample();
        example.createCriteria().andIdEqualTo(id)
                .andDeletedEqualTo(CommonConstant.VALID).andStateEqualTo(nowState);
        PojoTypeJson typeJson = new PojoTypeJson();
        typeJson.setUpdater(userName);
        typeJson.setUpdateTime(new Date());
        typeJson.setState(newState);
        int res = pojoTypeJsonMapper.updateByExampleSelective(typeJson, example);

        // 刷redis缓存
        apiConfigRedisCache.refreshPojoTypeJsonCache(id);
        return res;
    }
}
