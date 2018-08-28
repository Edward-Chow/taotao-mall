package com.taotao.sso.service;

import com.taotao.common.pojo.TaotaoResult;
import org.springframework.http.HttpRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface LoginService {
    TaotaoResult login(String username, String password, HttpServletRequest request, HttpServletResponse response);
    TaotaoResult getUserByToken(String token);
    TaotaoResult logout(String token);
}
