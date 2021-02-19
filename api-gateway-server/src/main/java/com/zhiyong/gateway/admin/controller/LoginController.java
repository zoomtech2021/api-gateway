package com.zhiyong.gateway.admin.controller;

import com.zhiyong.gateway.admin.common.AdminConstant;
import com.zhiyong.gateway.biz.service.UserService;
import com.zhiyong.gateway.common.model.ApiResponse;
import com.zhiyong.gateway.dal.domain.User;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName LoginController
 * @Description: TODO
 * @Author 毛军锐
 * @Date 2020/5/20 下午7:19
 **/
@RestController
public class LoginController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    @Resource
    private UserService userService;

    /**
     * 登录
     *
     * @return
     */
    @RequestMapping("/login.do")
    public ApiResponse<Boolean> login(HttpServletRequest request,
                                      String account, String password, String vercode) {
        if (StringUtils.isBlank(account)) {
            return ApiResponse.buildCommonErrorResponse("账号不能为空");
        }
        if (StringUtils.isBlank(password)) {
            return ApiResponse.buildCommonErrorResponse("密码不能为空");
        }
        if (StringUtils.isBlank(vercode)) {
            return ApiResponse.buildCommonErrorResponse("验证码不能为空");
        }

        String imgCode = (String) request.getSession().getAttribute(AdminConstant.LOGIN_IMG_CODE);
        vercode = vercode.trim().toUpperCase();
        if (!StringUtils.equals(vercode, imgCode)) {
            return ApiResponse.buildCommonErrorResponse("图形验证码输入错误");
        }
        // 登录
        User user = userService.getUserByAccount(account);
        if (user == null || !user.getPassword().equals(password)) {
            return ApiResponse.buildCommonErrorResponse("账号或密码错误");
        }
        setLoginUser(request, user);
        LOGGER.info("======网关后台用户：{}，登录成功！", user.getAccount());
        return ApiResponse.buildSuccessResponse(true);
    }

    /**
     * 登出
     *
     * @return
     */
    @RequestMapping("/admin/logout.do")
    public ApiResponse<Boolean> logout(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().removeAttribute(AdminConstant.LOGIN_USER);
        return ApiResponse.buildSuccessResponse(true);
    }
}
