package com.zhiyong.gateway.admin.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.zhiyong.gateway.admin.model.LoginUser;
import com.zhiyong.gateway.admin.model.TypeStruct;
import com.zhiyong.gateway.biz.service.StructConfigService;
import com.zhiyong.gateway.common.enums.ApiState;
import com.zhiyong.gateway.common.enums.ErrorCode;
import com.zhiyong.gateway.common.enums.TypeStructState;
import com.zhiyong.gateway.common.exception.GatewayException;
import com.zhiyong.gateway.common.model.ApiResponse;
import com.zhiyong.gateway.common.model.PageRequest;
import com.zhiyong.gateway.common.model.PageResult;
import com.zhiyong.gateway.common.util.AssertUtil;
import com.zhiyong.gateway.dal.domain.PojoTypeJson;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName DataStructController
 * @Description: TODO
 * @Author 毛军锐
 * @Date 2020/12/3 上午10:05
 **/
@RestController
@RequestMapping("/admin")
public class DataStructController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataStructController.class);

    @Resource
    private StructConfigService structConfigService;

    /**
     * 获取参数类型列表
     *
     * @param keyword
     * @return
     */
    @RequestMapping("/getParamTypeList.do")
    public ApiResponse<List<PojoTypeJson>> getParamTypeList(String keyword) {
        List<PojoTypeJson> typeJsons = structConfigService.getValidList(keyword);
        if (!CollectionUtils.isEmpty(typeJsons)) {
            typeJsons = typeJsons.size() > 50 ? typeJsons.subList(0, 50) : typeJsons;
        }
        return ApiResponse.buildSuccessResponse(typeJsons);
    }

    /**
     * 根据ID获取类型配置
     *
     * @param id
     * @return
     */
    @RequestMapping("/getTypeJsonById.do")
    public ApiResponse<PojoTypeJson> getTypeJsonById(Integer id) {
        PojoTypeJson typeJson = structConfigService.getPojoTypeJsonById(id);
        return ApiResponse.buildSuccessResponse(typeJson);
    }

    /**
     * 获取数据类型配置列表
     *
     * @param pageRequest
     * @param typeName
     * @param typeDesc
     * @return
     */
    @RequestMapping("/getDataStructList.do")
    public PageResult getDataStructList(PageRequest pageRequest, String typeName, String typeDesc) {
        PageInfo<PojoTypeJson> pageInfo = structConfigService.pagerPojoTypeJson(typeName, typeDesc, pageRequest);
        return PageResult.builder().total(pageInfo.getTotal())
                .rows(pageInfo.getList())
                .pageNum(pageInfo.getPageNum())
                .pageSize(pageInfo.getPageSize())
                .build();
    }

    /**
     * 保存类型
     *
     * @param request
     * @param typeJson
     * @return
     */
    @RequestMapping("/saveType.do")
    public ApiResponse<Boolean> saveType(HttpServletRequest request, PojoTypeJson typeJson) {
        int res = 0;
        final LoginUser loginUser = getLoginUser(request);

        try {
            AssertUtil.notEmpty("类型名称", typeJson.getTypeName());
            AssertUtil.notEmpty("类型描述", typeJson.getTypeDesc());
            AssertUtil.notEmpty("类型配置JSON", typeJson.getTypeJson());
            checkTypeJson(typeJson.getTypeJson());
            if (typeJson.getId() == null) {
                typeJson.setCreator(loginUser.getUserName());
                typeJson.setState(ApiState.TEMP.getCode());
            } else {
                typeJson.setUpdater(loginUser.getUserName());
            }
            res = structConfigService.savePojoTypeJson(typeJson);
        } catch (Exception e) {
            LOGGER.error("saveType error:", e);
            return ApiResponse.buildCommonErrorResponse(e.getMessage());
        } finally {
            saveOptLog(loginUser, "保存POJO对象类型信息", JSON.toJSONString(typeJson), res > 0);
        }
        return ApiResponse.buildSuccessResponse(null);
    }

    /**
     * 校验格式
     *
     * @param typeJson
     */
    private void checkTypeJson(String typeJson) {
        TypeStruct typeStruct = JSON.parseObject(typeJson, TypeStruct.class);
        checkTypeStruct(typeStruct);
    }

    /**
     * 校验格式
     *
     * @param typeStruct
     */
    private void checkTypeStruct(TypeStruct typeStruct) {
        if (StringUtils.isBlank(typeStruct.getName())) {
            throw new GatewayException(ErrorCode.ISP_CONFIG_ERROR, "类型配置JSON中参数[name]不能为空");
        }
        /*if (StringUtils.isBlank(typeStruct.getDesc())) {
            throw new GatewayException(ErrorCode.ISP_CONFIG_ERROR, "类型配置JSON中参数[desc]不能为空");
        }*/
        if (StringUtils.isBlank(typeStruct.getType())) {
            throw new GatewayException(ErrorCode.ISP_CONFIG_ERROR, "类型配置JSON中参数[type]不能为空");
        }
        String collectionType = typeStruct.getCollectionType();
        if (StringUtils.isNotBlank(collectionType)) {
            if (!StringUtils.equals(collectionType, "java.util.List")
                    && !StringUtils.equals(collectionType, "java.util.Set")
                    && !StringUtils.equals(collectionType, "java.util.Map")
                    && !StringUtils.endsWith(collectionType, "[]")) {
                throw new GatewayException(ErrorCode.ISP_CONFIG_ERROR,
                        "类型配置JSON中参数[collectionType]只支持java.util.List、java.util.Set、java.util.Map、POJO数组");
            }
        }
        List<TypeStruct> children = typeStruct.getChildren();
        if (!CollectionUtils.isEmpty(children)) {
            for (TypeStruct child : children) {
                checkTypeStruct(child);
            }
        }
    }

    /**
     * 变更API状态
     *
     * @param id
     * @return
     */
    @RequestMapping("/editTypeState.do")
    public ApiResponse<Boolean> editTypeState(HttpServletRequest request, Integer id,
                                              Integer nowState, Integer newState) {
        int res = 0;
        final LoginUser loginUser = getLoginUser(request);

        try {
            boolean check = TypeStructState.changeState(nowState, newState);
            if (!check) {
                throw new GatewayException(ErrorCode.ISV_ILLEGAL_ARGUMENT, "不允许的状态变更");
            }
            res = structConfigService.changeState(loginUser.getUserName(), id, nowState, newState);
        } catch (Exception e) {
            LOGGER.error("editTypeState error:", e);
            return ApiResponse.buildCommonErrorResponse(e.getMessage());
        } finally {
            saveOptLog(loginUser, "编辑POJO对象类型状态", "类型ID：" + id
                    + ",原状态：" + nowState + ",新状态：" + newState, res > 0);
        }
        return ApiResponse.buildSuccessResponse(res > 0);
    }
}
