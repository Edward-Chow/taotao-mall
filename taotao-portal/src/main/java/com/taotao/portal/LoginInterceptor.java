package com.taotao.portal;

import com.sun.org.apache.regexp.internal.RE;
import com.taotao.pojo.TbUser;
import com.taotao.portal.service.UserService;
import org.omg.CORBA.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    private UserService userService;
    @Value("${SSO_LOGIN_URL}")
    private String SSO_LOGIN_URL;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        TbUser user = userService.getUserByToken(httpServletRequest, httpServletResponse);
        if (user == null) {
            httpServletResponse.sendRedirect(SSO_LOGIN_URL + "?redirectURL=" + httpServletRequest.getRequestURL());
            return false;
        }
        httpServletRequest.setAttribute("user", user);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
