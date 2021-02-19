package com.zhiyong.gateway.admin.controller;

import com.github.pagehelper.PageInfo;
import com.zhiyong.gateway.admin.common.AdminConstant;
import com.zhiyong.gateway.admin.model.LoginUser;
import com.zhiyong.gateway.biz.service.UserService;
import com.zhiyong.gateway.common.constant.CommonConstant;
import com.zhiyong.gateway.common.model.ApiResponse;
import com.zhiyong.gateway.common.model.PageRequest;
import com.zhiyong.gateway.common.model.PageResult;
import com.zhiyong.gateway.common.util.AssertUtil;
import com.zhiyong.gateway.dal.domain.Auth;
import com.zhiyong.gateway.dal.domain.AuthExt;
import com.zhiyong.gateway.dal.domain.User;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName UserController
 * @Description: TODO
 * @Author 毛军锐
 * @Date 2020/11/30 下午8:24
 **/
@RestController
@RequestMapping("/admin")
public class UserController extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Resource
    private UserService userService;

    /**
     * 保存用户
     *
     * @param user
     * @return
     */
    @RequestMapping("/saveUser.do")
    public ApiResponse<Integer> saveUser(User user) {
        try {
            AssertUtil.notEmpty("账号", user.getAccount());
            if (user.getId() == null) {
                AssertUtil.notEmpty("密码", user.getPassword());
            } else {
                user.setPassword(null);
            }
            AssertUtil.notEmpty("角色", user.getRole());
            AssertUtil.notEmpty("部门", user.getDepartment());
            int id = userService.saveUser(user);
            return ApiResponse.buildSuccessResponse(id);
        } catch (Exception e) {
            LOGGER.error("saveUser error:", e);
            return ApiResponse.buildCommonErrorResponse(e.getMessage());
        }
    }

    /**
     * 删除用户
     *
     * @param id
     * @return
     */
    @RequestMapping("/delUser.do")
    public ApiResponse<Integer> delUser(Integer id) {
        try {
            User user = new User();
            user.setId(id);
            user.setDeleted(CommonConstant.DELETED);
            userService.saveUser(user);
            return ApiResponse.buildSuccessResponse(id);
        } catch (Exception e) {
            LOGGER.error("delUser error:", e);
            return ApiResponse.buildCommonErrorResponse(e.getMessage());
        }
    }

    /**
     * 查询用户
     *
     * @param account
     * @return
     */
    @RequestMapping("/getUserList.do")
    public PageResult getUserList(PageRequest pageRequest, String account) {
        PageInfo<User> pageInfo = userService.pagerUser(account, pageRequest);
        return PageResult.builder().total(pageInfo.getTotal())
                .rows(pageInfo.getList())
                .pageNum(pageInfo.getPageNum())
                .pageSize(pageInfo.getPageSize())
                .build();
    }


    /**
     * 修改密码
     *
     * @param newPwd
     * @return
     */
    @RequestMapping("/modifyPwd.do")
    public ApiResponse<Boolean> modifyPwd(HttpServletRequest request, String newPwd) {
        try {
            if (StringUtils.isBlank(newPwd)) {
                return ApiResponse.buildCommonErrorResponse("新密码不能为空");
            }
            LoginUser loginUser = (LoginUser) request.getSession().getAttribute(AdminConstant.LOGIN_USER);
            User user = new User();
            user.setId(loginUser.getUserId());
            user.setPassword(newPwd);
            int res = userService.saveUser(user);
            request.getSession().removeAttribute(AdminConstant.LOGIN_USER);
            return ApiResponse.buildSuccessResponse(res > 0);
        } catch (Exception e) {
            LOGGER.error("modifyPwd error:", e);
            return ApiResponse.buildCommonErrorResponse(e.getMessage());
        }
    }

    /**
     * 保存权限
     *
     * @param auth
     * @return
     */
    @RequestMapping("/saveAuth.do")
    public ApiResponse<Integer> saveAuth(Auth auth) {
        try {
            AssertUtil.notNull("用户ID", auth.getUserId());
            AssertUtil.notNull("类目ID", auth.getApiGroup());
            int id = userService.saveAuth(auth);
            return ApiResponse.buildSuccessResponse(id);
        } catch (Exception e) {
            LOGGER.error("saveAuth error:", e);
            return ApiResponse.buildCommonErrorResponse(e.getMessage());
        }
    }

    /**
     * 查询权限
     *
     * @param account
     * @return
     */
    @RequestMapping("/getAuthList.do")
    public PageResult getAuthList(PageRequest pageRequest, String account) {
        PageInfo<AuthExt> pageInfo = userService.pagerAuth(account, pageRequest);
        return PageResult.builder().total(pageInfo.getTotal())
                .rows(pageInfo.getList())
                .pageNum(pageInfo.getPageNum())
                .pageSize(pageInfo.getPageSize())
                .build();
    }

    /**
     * 删除权限
     *
     * @param id
     * @return
     */
    @RequestMapping("/delAuth.do")
    public ApiResponse<Integer> delAuth(Integer id) {
        try {
            userService.delAuth(id);
            return ApiResponse.buildSuccessResponse(id);
        } catch (Exception e) {
            LOGGER.error("delAuth error:", e);
            return ApiResponse.buildCommonErrorResponse(e.getMessage());
        }
    }
}
