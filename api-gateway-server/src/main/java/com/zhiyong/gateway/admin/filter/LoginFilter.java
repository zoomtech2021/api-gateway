package com.zhiyong.gateway.admin.filter;

import com.google.common.collect.Lists;
import com.zhiyong.gateway.admin.common.AdminConstant;
import com.zhiyong.gateway.admin.model.LoginUser;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.util.AntPathMatcher;

/**
 * @ClassName LoginInterceptor
 * @Description: TODO
 * @Author maojunrui
 * @Date 2019-08-28
 **/

@Configuration
@WebFilter(urlPatterns = "/**/*", filterName = "loginFilter")
@Order(1)
public class LoginFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(LoginFilter.class);
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    private static final String LOGIN_URL = "/login.html";

    /**
     * 登录检查排除URL
     */
    private List<String> exclusions = Lists.newArrayList(LOGIN_URL,
            "/login.do", "/custom/**", "/static/**", "/login/imageCode", "/dist/**",
            "/swagger-dubbo/api-docs", "/h/**", "/info","/health", "/router/**");

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String path = request.getRequestURI();
        if (!isExclusionPath(path)) {
            // 检查登录
            try {
                LoginUser loginUser = (LoginUser) request.getSession().getAttribute(AdminConstant.LOGIN_USER);
                if (loginUser == null) {
                    throw new RuntimeException("登录过期或未登录");
                }
            } catch (Exception e) {
                logger.error("LoginFilter error:" + e.getMessage());
                boolean isAjax = isAjax(request);
                if (isAjax) {
                    response.setStatus(403);
                } else {
                    response.setContentType("text/html;charset=UTF-8");
                    StringBuilder loginScript = new StringBuilder();
                    loginScript.append("<script language='javascript'>");
                    loginScript.append("window.top.location.href='").append(LOGIN_URL).append("';");
                    loginScript.append("</script>");
                    PrintWriter pw = servletResponse.getWriter();
                    pw.write(loginScript.toString());
                    pw.flush();
                    pw.close();
                }
                return;
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }

    /**
     * 是否是需要排除的路径
     *
     * @param path
     * @return
     */
    private boolean isExclusionPath(String path) {
        if (CollectionUtils.isNotEmpty(exclusions)) {
            for (String exclusion : exclusions) {
                if (PATH_MATCHER.match(exclusion, path)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 是否是Ajax请求
     *
     * @param request
     * @return
     */
    private boolean isAjax(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }
}
