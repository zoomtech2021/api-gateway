package com.zhiyong.gateway.admin.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.zhiyong.gateway.admin.model.LoginUser;
import com.zhiyong.gateway.biz.service.ApiConfigService;
import com.zhiyong.gateway.common.constant.CommonConstant;
import com.zhiyong.gateway.common.model.ApiResponse;
import com.zhiyong.gateway.common.model.PageRequest;
import com.zhiyong.gateway.common.model.PageResult;
import com.zhiyong.gateway.dal.domain.ApiGroupCfg;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName ApiGroupController
 * @Description: TODO
 * @Author 毛军锐
 * @Date 2020/11/26 下午6:59
 **/
@RestController
@RequestMapping("/admin")
public class ApiGroupController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiGroupController.class);

    @Resource
    private ApiConfigService apiConfigService;

    /**
     * 保存API类目
     *
     * @param apiGroupCfg
     * @return
     */
    @RequestMapping("/saveApiGroup.do")
    public ApiResponse<Boolean> saveApiGroup(HttpServletRequest request, ApiGroupCfg apiGroupCfg) {
        int res = 0;
        final LoginUser loginUser = getLoginUser(request);

        try {
            if (apiGroupCfg.getId() == null) {
                apiGroupCfg.setCreator(loginUser.getUserName());
            } else {
                apiGroupCfg.setUpdater(loginUser.getUserName());
            }
            res = apiConfigService.saveApiGroup(apiGroupCfg);
        } catch (Exception e) {
            LOGGER.error("saveApiGroup error:", e);
            return ApiResponse.buildCommonErrorResponse(e.getMessage());
        } finally {
            saveOptLog(loginUser, "保存API类目信息", JSON.toJSONString(apiGroupCfg), res > 0);
        }
        return ApiResponse.buildSuccessResponse(res > 0);
    }

    /**
     * 删除应用
     *
     * @param id
     * @return
     */
    @RequestMapping("/delApiGroup.do")
    public ApiResponse<Boolean> delApiGroup(HttpServletRequest request, Integer id) {
        int res = 0;
        final LoginUser loginUser = getLoginUser(request);

        try {
            ApiGroupCfg apiGroupCfg = new ApiGroupCfg();
            apiGroupCfg.setId(id);
            apiGroupCfg.setUpdater(loginUser.getUserName());
            apiGroupCfg.setDeleted(CommonConstant.DELETED);
            res = apiConfigService.delApiGroup(apiGroupCfg);
        } catch (Exception e) {
            LOGGER.error("delApiGroup error:", e);
            return ApiResponse.buildCommonErrorResponse(e.getMessage());
        } finally {
            saveOptLog(loginUser, "删除API类目信息", "id=" + id, res > 0);
        }
        return ApiResponse.buildSuccessResponse(res > 0);
    }

    /**
     * 获取APP列表
     *
     * @return
     */
    @RequestMapping("/getAllApiGroup.do")
    public ApiResponse<List<ApiGroupCfg>> getAllApiGroup() {
        List<ApiGroupCfg> apiGroupCfgList = apiConfigService.listApiGroup();
        return ApiResponse.buildSuccessResponse(apiGroupCfgList);
    }

    /**
     * 获取APP列表
     *
     * @return
     */
    @RequestMapping("/getApiGroupList.do")
    public PageResult getApiGroupList(PageRequest pageRequest, String groupName) {
        PageInfo<ApiGroupCfg> pageInfo = apiConfigService.pagerApiGroup(groupName, pageRequest);
        return PageResult.builder().total(pageInfo.getTotal())
                .rows(pageInfo.getList())
                .pageNum(pageInfo.getPageNum())
                .pageSize(pageInfo.getPageSize())
                .build();
    }
}
