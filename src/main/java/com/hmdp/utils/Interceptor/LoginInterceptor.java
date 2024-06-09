package com.hmdp.utils.Interceptor;

import com.hmdp.dto.UserDTO;
import com.hmdp.utils.RedisConstants;
import com.hmdp.utils.UserHolder;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 登录 拦截器
 * @author liuzhangjie
 * @date 2024/04/18
 */


@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取UserHolder中的用户信息
        UserDTO user = UserHolder.getUser();

        // 判断用户是否存在
        if(Objects.isNull(user)){
            // 不存在， 则拦截
            response.setStatus(401);
            return false;
        }

        return true;
    }
}
