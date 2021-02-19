package com.zhiyong.gateway.admin.controller;

import com.alibaba.fastjson.JSON;
import com.zhiyong.gateway.admin.common.AdminConstant;
import com.zhiyong.gateway.admin.model.LoginUser;
import com.zhiyong.gateway.biz.service.StructConfigService;
import com.zhiyong.gateway.dal.domain.PojoTypeJson;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @ClassName PageController
 * @Description: 网关管理后台页面路由
 * @Author 毛军锐
 **/
@Controller
public class PageController {
    @Resource
    private StructConfigService structConfigService;

    @RequestMapping("/")
    public String home(HttpServletRequest request, Model model) {
        LoginUser loginUser = (LoginUser) request.getSession().getAttribute(AdminConstant.LOGIN_USER);
        model.addAttribute("account", loginUser.getUserName());
        return "index";
    }

    @RequestMapping("/index.html")
    public String index(HttpServletRequest request, Model model) {
        return home(request, model);
    }

    @RequestMapping("/login.html")
    public String login() {
        return "login";
    }

    @RequestMapping("/doc.html")
    public String doc() {
        return "doc";
    }

    @RequestMapping("/user/user-list.html")
    public String userList() {
        return "user/user-list";
    }

    @RequestMapping("/user/role-list.html")
    public String roleList() {
        return "user/role-list";
    }

    @RequestMapping("/user/auth-list.html")
    public String authList() {
        return "user/auth-list";
    }

    @RequestMapping("/app/app-list.html")
    public String appList() {
        return "app/app-list";
    }

    @RequestMapping("/group/group-list.html")
    public String groupList() {
        return "group/group-list";
    }

    @RequestMapping("/api/api-list.html")
    public String apiList() {
        return "api/api-list";
    }

    @RequestMapping("/api/api-test-tool.html")
    public String apiTestTool() {
        return "api/api-test-tool";
    }

    @RequestMapping("/api/add-api.html")
    public String addApi(Model model, Integer id, Integer groupId) {
        model.addAttribute("id", id);
        model.addAttribute("groupId", groupId);
        return "api/add-api";
    }

    @RequestMapping("/api/preview-api.html")
    public String previewApi(Model model, Integer id) {
        model.addAttribute("id", id);
        return "api/preview-api";
    }

    @RequestMapping("/struct/struct-list.html")
    public String dataTypeList() {
        return "struct/struct-list";
    }

    @RequestMapping("/struct/json-preview.html")
    public String jsonPreview(Model model, Integer id) {
        model.addAttribute("id", id);
        PojoTypeJson json = structConfigService.getPojoTypeJsonById(id);
        if (json != null && StringUtils.isNotBlank(json.getTypeJson())) {
            model.addAttribute("json", JSON.parseObject(json.getTypeJson()));
        }
        return "struct/json-preview";
    }

    @RequestMapping("/struct/add-struct.html")
    public String addDataType(Model model, Integer id) {
        model.addAttribute("id", id);
        return "struct/add-struct";
    }

    @RequestMapping("/monitor/api-count.html")
    public String apiCount() {
        return "monitor/api-count";
    }

    @RequestMapping("/monitor/opt-log.html")
    public String optLog() {
        return "monitor/opt-log";
    }
}
