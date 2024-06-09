package com.hmdp.config;

import com.hmdp.utils.Interceptor.LoginInterceptor;
import com.hmdp.utils.Interceptor.RefreshTokenInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author liuzhangjie
 * @date 2024/04/18
 */

@Configuration
@RequiredArgsConstructor
public class MVCConfig implements WebMvcConfigurer {

    private final RefreshTokenInterceptor refreshTokenInterceptor;

    private final LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // token刷新拦截器
        registry.addInterceptor(refreshTokenInterceptor)
                        .order(0);

        // 登录拦截器
        registry.addInterceptor(loginInterceptor)
                .excludePathPatterns(
                     "/shop/**",
                     "/voucher/**",
                     "/shop-type/**",
                     "/upload/**",
                     "/blog/hot",
                     "/user/code",
                     "/user/login"
                ).order(1);
    }
}
