package com.hmdp.utils.Interceptor;

import cn.hutool.core.util.StrUtil;
import com.hmdp.dto.UserDTO;
import com.hmdp.utils.RedisConstants;
import com.hmdp.utils.UserHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * token刷新 拦截器
 * @author liuzhangjie
 * @date 2024/04/18
 */

@Component
@RequiredArgsConstructor
public class RefreshTokenInterceptor implements HandlerInterceptor {
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求头中的token
        String token = request.getHeader("authorization");
        if(StrUtil.isBlank(token)){
            return true;
        }

        // 获取redis中的用户信息
        String key = RedisConstants.LOGIN_USER_KEY + token;
        UserDTO user = (UserDTO) redisTemplate.opsForValue().get(key);

        // 判断用户是否存在
        if(Objects.isNull(user)){
            return true;
        }

        // 保存用户信息
        UserHolder.saveUser(user);

        // 刷新token有效期
        redisTemplate.expire(key, RedisConstants.LOGIN_USER_TTL, TimeUnit.MINUTES);
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 清理当前线程保存的用户数据
        UserHolder.removeUser();

        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }
}
