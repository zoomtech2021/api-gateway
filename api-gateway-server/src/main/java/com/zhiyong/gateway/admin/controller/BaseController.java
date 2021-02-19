package com.zhiyong.gateway.admin.controller;

import com.zhiyong.gateway.admin.common.AdminConstant;
import com.zhiyong.gateway.admin.model.LoginUser;
import com.zhiyong.gateway.biz.service.ApiConfigService;
import com.zhiyong.gateway.biz.service.OptLogService;
import com.zhiyong.gateway.biz.service.UserService;
import com.zhiyong.gateway.common.enums.ErrorCode;
import com.zhiyong.gateway.common.exception.GatewayException;
import com.zhiyong.gateway.dal.domain.ApiCfg;
import com.zhiyong.gateway.dal.domain.Auth;
import com.zhiyong.gateway.dal.domain.OptLog;
import com.zhiyong.gateway.dal.domain.User;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName BaseController
 * @Description: TODO
 * @Author 毛军锐
 * @Date 2020/11/26 下午11:36
 **/
public class BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);

    @Resource
    private UserService userService;
    @Resource
    private OptLogService optLogService;
    @Resource
    private ApiConfigService apiConfigService;

    /**
     * 获取登录用户
     *
     * @param request
     * @return
     */
    public LoginUser getLoginUser(HttpServletRequest request) {
        return (LoginUser) request.getSession().getAttribute(AdminConstant.LOGIN_USER);
    }

    /**
     * 设置登录用户到session
     *
     * @param request
     * @param user
     */
    public void setLoginUser(HttpServletRequest request, User user) {
        LoginUser loginUser = LoginUser.builder().userId(user.getId()).userName(user.getAccount()).build();
        request.getSession().setAttribute(AdminConstant.LOGIN_USER, loginUser);
        request.getSession().removeAttribute(AdminConstant.LOGIN_IMG_CODE);
    }

    /**
     * 判断用户是否有API的操作权限
     *
     * @param loginUser
     * @param apiCfg
     * @return
     */
    public boolean hasApiOptAuth(LoginUser loginUser, ApiCfg apiCfg) {
        if (StringUtils.equals(loginUser.getUserName(), "admin")) {
            return true;
        }
        if (apiCfg.getId() == null) {
            return true;
        }
        String creator = apiCfg.getCreator();
        if (creator == null) {
            ApiCfg cfg = apiConfigService.findApiById(apiCfg.getId());
            if (cfg == null) {
                throw new GatewayException(ErrorCode.ISP_SYSTEM_ERROR, "API不存在");
            }
            creator = cfg.getCreator();
        }
        if (StringUtils.equals(loginUser.getUserName(), creator)) {
            return true;
        }
        Auth auth = userService.getAuth(loginUser.getUserId(), apiCfg.getApiGroup());
        return auth != null;
    }

    /**
     * 保存操作日志
     *
     * @param loginUser
     * @param action
     * @param content
     */
    public void saveOptLog(LoginUser loginUser, String action, String content, boolean success) {
        try {
            if (content != null && content.length() > 2000) {
                content = content.substring(0, 2000);
            }
            OptLog optLog = new OptLog();
            optLog.setOperatorId(loginUser.getUserId());
            optLog.setOperator(loginUser.getUserName());
            optLog.setAction(action);
            optLog.setContent(content);
            optLog.setResult(success ? 0 : 1);
            optLogService.saveOptLog(optLog);
        } catch (Exception e) {
            LOGGER.error("saveOptLog error:", e);
        }
    }
}
