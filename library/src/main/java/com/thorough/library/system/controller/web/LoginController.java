
package com.thorough.library.system.controller.web;

import com.thorough.library.shiro.filter.SysFormAuthenticationFilter;
import com.thorough.library.shiro.utils.ValidateCode;
import com.thorough.library.specification.system.Principal;
import com.thorough.library.system.model.entity.User;
import com.thorough.library.utils.*;
import com.thorough.library.specification.controller.BaseController;
import com.thorough.library.system.utils.UserUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录Controller
 */
@Controller
public class LoginController extends BaseController {

    private final static  String backgroundIndex = "/modules/sys/index";

    private final static  String login = "/login";

    /**
     * 管理登录（后端接口）
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(HttpServletRequest request, HttpServletResponse response, Model model) {
        String url = checkLoginStatus(request,response,model);
        return url;
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String indexLogin(HttpServletRequest request, HttpServletResponse response, Model model) {
        String url = checkLoginStatus(request,response,model);
        return url;
    }

    /**
     * 微服务
     * */
    @RequestMapping(value = "${adminPath}", method = RequestMethod.GET)
    public String adminIndex(HttpServletRequest request, HttpServletResponse response, Model model) {
        String url = checkLoginStatus(request,response,model);
        return url;
    }

    private String checkLoginStatus(HttpServletRequest request, HttpServletResponse response, Model model){
        Principal principal = UserUtils.getPrincipal();
        // 如果已经登录，则跳转到管理首页
        if (principal != null) {
            return backgroundIndex;
        }
        return login;
    }

    /**
     * 管理登录（前端接口）
     */
    @RequestMapping(value = "${adminPath}/login", method = RequestMethod.GET)
    public String login(HttpServletRequest request, HttpServletResponse response, Model model) {
        Principal principal = UserUtils.getPrincipal();
        ResponseWrapper wrapper = new ResponseWrapper();
        if(principal != null){
            Map map = principalToMap(principal, UserUtils.getUser());
            wrapper.setData(map);
        }else {
            wrapper.setSuccess(false);
            wrapper.setMessage("未登录或者登录超时，请重新登录！");
            wrapper.add("","");
            wrapper.setCode("530");
        }
        return renderString(response, wrapper);
    }

    /**
     *  首次登录失败或者已经登录成功后再次调用登录接口，通过POST请求发起的登录失败会跳转到这里，成功会被Filter重定向或者跳转到登录首页，真正登录的POST请求由Filter完成
     */
    @RequestMapping(value = "${adminPath}/login", method = RequestMethod.POST)
    public String loginFail(HttpServletRequest request, HttpServletResponse response, Model model) {
        Principal principal = UserUtils.getPrincipal();
        boolean mobile = WebUtils.isTrue(request, SysFormAuthenticationFilter.DEFAULT_MOBILE_PARAM);
        //没有登录的情况
        if (principal == null){
            String message = (String) request.getAttribute(SysFormAuthenticationFilter.DEFAULT_MESSAGE_PARAM);
            String username = WebUtils.getCleanParam(request, SysFormAuthenticationFilter.DEFAULT_USERNAME_PARAM);
            boolean rememberMe = WebUtils.isTrue(request, SysFormAuthenticationFilter.DEFAULT_REMEMBER_ME_PARAM);
            String exception = (String) request.getAttribute(SysFormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME);

            if (StringUtils.isBlank(message) || StringUtils.equals(message, "null")) {
                message = "用户名或密码错误, 请重试.";
            }
            model.addAttribute(SysFormAuthenticationFilter.DEFAULT_MOBILE_PARAM, mobile);
            model.addAttribute(SysFormAuthenticationFilter.DEFAULT_MESSAGE_PARAM, message);
            model.addAttribute(SysFormAuthenticationFilter.DEFAULT_USERNAME_PARAM, username);
            model.addAttribute(SysFormAuthenticationFilter.DEFAULT_REMEMBER_ME_PARAM, rememberMe);
            model.addAttribute(SysFormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME, exception);

            // 非授权异常，登录失败，验证码加1。
            if (!UnauthorizedException.class.getName().equals(exception)) {
                model.addAttribute("isValidateCodeLogin", ValidateCode.isValidateCodeLogin(username, true, false));
            }

            // 如果是移动端，则返回JSON字符串
            if (mobile){
                ResponseWrapper wrapper = new ResponseWrapper();
                wrapper.setSuccess(false);
                wrapper.setData(model.asMap());
                renderString(response, wrapper);
                return null;
            }else {
                return login;//登录失败，返回登录页面
            }
        } else {
            //如果是移动端
            if (mobile){
                ResponseWrapper wrapper = new ResponseWrapper();
                Map map = principalToMap(principal, UserUtils.getUser());
                wrapper.setData(map);
                renderString(response, wrapper);
                return null;
            }else {
                return backgroundIndex;//如果是后端已经登录成功，跳转到首页
            }
        }
    }

    /**
     * 登录成功，进入管理首页
     */
    @RequestMapping(value = "${adminPath}/success")
    public String index(HttpServletRequest request, HttpServletResponse response) {
        Principal principal = UserUtils.getPrincipal();

        // 登录成功后，验证码计算器清零
        ValidateCode.isValidateCodeLogin(principal.getLoginName(), false, true);

        // 如果已登录，再次访问主页，则退出原账号。
        if (PropertyUtil.TRUE.equals(PropertyUtil.getProperty("notAllowRefreshIndex"))) {
            String logined = CookieUtils.getCookie(request, "LOGINED");
            if (StringUtils.isBlank(logined) || "false".equals(logined)) {
                CookieUtils.setCookie(response, "LOGINED", "true");
            } else if (StringUtils.equals(logined, "true")) {
                UserUtils.getSubject().logout();
                return "redirect:" + adminPath + "/login";
            }
        }

        // 如果是手机登录，则返回JSON字符串
        if (principal.isMobileLogin()) {
                Map map = principalToMap(principal, UserUtils.getUser());
                ResponseWrapper wrapper = new ResponseWrapper();
                wrapper.setData(map);
                return renderString(response,wrapper);
        }
        return backgroundIndex;
    }

    private Map principalToMap(Principal principal, User user){
        Map map = new HashMap();
        map.put("id",principal.getId());
        map.put("loginName",principal.getLoginName());
        map.put("name",principal.getName());
        map.put("mobileLogin",principal.isMobileLogin());
        map.put("sessionid",principal.getSessionid());
        map.put("userType",user.getUserType());
        map.put("userTypeName",user.getUserTypeName());
        return map;
    }

    @RequestMapping(value = "${adminPath}/logoff")
    public ResponseEntity<?> logout(){
        ResponseBuilder builder = ResponseBuilder.newInstance();
        SecurityUtils.getSubject().logout();
        builder.message("成功退出！");
        return builder.build();
    }

}
