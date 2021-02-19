package com.zhiyong.gateway.admin.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.zhiyong.gateway.admin.model.LoginUser;
import com.zhiyong.gateway.biz.service.ApiConfigService;
import com.zhiyong.gateway.common.constant.CommonConstant;
import com.zhiyong.gateway.common.model.ApiResponse;
import com.zhiyong.gateway.common.model.PageRequest;
import com.zhiyong.gateway.common.model.PageResult;
import com.zhiyong.gateway.dal.domain.AppCfg;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName AppController
 * @Description: TODO
 * @Author 毛军锐
 * @Date 2020/11/26 下午6:59
 **/
@RestController
@RequestMapping("/admin")
public class AppController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppController.class);

    @Resource
    private ApiConfigService apiConfigService;

    /**
     * 保存应用
     *
     * @param appCfg
     * @return
     */
    @RequestMapping("/saveApp.do")
    public ApiResponse<Boolean> saveApp(HttpServletRequest request, AppCfg appCfg) {
        int res = 0;
        final LoginUser loginUser = getLoginUser(request);

        try {
            if (appCfg.getId() == null) {
                appCfg.setCreator(loginUser.getUserName());
            } else {
                appCfg.setUpdater(loginUser.getUserName());
            }
            res = apiConfigService.saveApp(appCfg);
        } catch (Exception e) {
            LOGGER.error("saveApp error:", e);
            return ApiResponse.buildCommonErrorResponse(e.getMessage());
        } finally {
            saveOptLog(loginUser, "保存APP信息", JSON.toJSONString(appCfg), res > 0);
        }
        return ApiResponse.buildSuccessResponse(res > 0);
    }

    /**
     * 删除应用
     *
     * @param id
     * @return
     */
    @RequestMapping("/delApp.do")
    public ApiResponse<Boolean> delApp(HttpServletRequest request, Integer id) {
        int res = 0;
        final LoginUser loginUser = getLoginUser(request);

        try {
            AppCfg appCfg = new AppCfg();
            appCfg.setId(id);
            appCfg.setUpdater(loginUser.getUserName());
            appCfg.setDeleted(CommonConstant.DELETED);
            res = apiConfigService.delApp(appCfg);
        } catch (Exception e) {
            LOGGER.error("delApp error:", e);
            return ApiResponse.buildCommonErrorResponse(e.getMessage());
        } finally {
            saveOptLog(loginUser, "删除APP信息", "appId=" + id, res > 0);
        }
        return ApiResponse.buildSuccessResponse(res > 0);
    }

    /**
     * 获取APP列表
     *
     * @return
     */
    @RequestMapping("/getAppList.do")
    public PageResult getAppList(PageRequest pageRequest, String appName, String appKey) {
        PageInfo<AppCfg> pageInfo = apiConfigService.pagerListApp(appName, appKey, pageRequest);
        return PageResult.builder().total(pageInfo.getTotal())
                .rows(pageInfo.getList())
                .pageNum(pageInfo.getPageNum())
                .pageSize(pageInfo.getPageSize())
                .build();
    }

    /**
     * 获取APP秘钥
     *
     * @param appKey
     * @return
     */
    @RequestMapping("/getAppSecret.do")
    public ApiResponse<String> getAppSecret(String appKey) {
        try {
            AppCfg appCfg = apiConfigService.getAppCfg(appKey);
            if (appCfg != null) {
                return ApiResponse.buildSuccessResponse(appCfg.getAppSecret());
            }
        } catch (Exception e) {
            LOGGER.error("getAppSecret error:", e);
            return ApiResponse.buildCommonErrorResponse(e.getMessage());
        }
        return ApiResponse.buildSuccessResponse(null);
    }
}
