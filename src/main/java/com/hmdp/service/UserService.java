package com.hmdp.service;

import com.hmdp.dto.LoginFormDTO;
import com.hmdp.dto.Result;
import com.hmdp.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;


/**
 * 用户模块 服务类
 *
 * @author liuzhangjie
 * @date 2024-04-16
 */
public interface UserService {

    Result sendCode(String phone, HttpSession session);

    Result login(LoginFormDTO loginForm, HttpSession session);

    Result logout(HttpServletRequest request);
}
