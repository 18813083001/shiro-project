package com.thorough.library.shiro.filter;

import org.apache.shiro.web.filter.authc.UserFilter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;


public class SysUserFilter extends UserFilter {

    @Override
    protected void redirectToLogin(ServletRequest request, ServletResponse response) throws IOException {
        //不用Redirect，用Forward
        String loginUrl = getLoginUrl();
        RequestDispatcher rd = request.getRequestDispatcher(loginUrl);
        try {
            rd.forward(request, response);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
    }
}
