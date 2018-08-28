package com.taotao.sso.controller;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.ExceptionUtil;
import com.taotao.sso.service.LoginService;
import javafx.beans.binding.ObjectExpression;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class LoginController {
    @Autowired
    private LoginService loginService;

    @RequestMapping(value = "/user/login", method = RequestMethod.POST)
    @ResponseBody
    public TaotaoResult login(String username, String password, HttpServletRequest request, HttpServletResponse response) {
        try {
            return loginService.login(username, password, request, response);
        } catch (Exception e) {
            e.printStackTrace();
            return TaotaoResult.build(500, ExceptionUtil.getStackTrace(e));
        }
    }

    @RequestMapping("/user/token/{token}")
    @ResponseBody
    public Object getUserByToken(@PathVariable String token, String callback) {
        try {
             TaotaoResult result = loginService.getUserByToken(token);
             if (StringUtils.isNotBlank(callback)) {
                 MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(result);
                 mappingJacksonValue.setJsonpFunction(callback);
                 return mappingJacksonValue;
             }
             return result;
        } catch (Exception e) {
            e.printStackTrace();
            return TaotaoResult.build(500, ExceptionUtil.getStackTrace(e));
        }
    }


    @RequestMapping(value = "/user/logout/{token}",method = RequestMethod.GET)
    @ResponseBody
    public TaotaoResult logout(@PathVariable String token){
        TaotaoResult result = loginService.logout(token);
        return result;
    }
}
