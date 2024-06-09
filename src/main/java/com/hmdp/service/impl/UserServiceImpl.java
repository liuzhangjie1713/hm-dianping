package com.hmdp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hmdp.dto.LoginFormDTO;
import com.hmdp.dto.Result;
import com.hmdp.dto.UserDTO;
import com.hmdp.entity.User;
import com.hmdp.mapper.UserMapper;
import com.hmdp.service.UserService;
import com.hmdp.utils.RedisConstants;
import com.hmdp.utils.RegexUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;


import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.hmdp.utils.SystemConstants.USER_NICK_NAME_PREFIX;

/**
 * 用户模块 服务实现类
 *
 * @author liuzhangjie
 * @date 2024-04-16
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Result sendCode(String phone, HttpSession session) {
        // 校验手机号
        if(RegexUtils.isPhoneInvalid(phone)){
            return Result.fail("手机号格式错误");
        }

        // 符合， 生成验证码
        String code = RandomUtil.randomNumbers(6);

        // 保存验证码到redis
        redisTemplate.opsForValue().set(RedisConstants.LOGIN_CODE_KEY + phone, code, RedisConstants.LOGIN_CODE_TTL, TimeUnit.MINUTES);

        // 发送验证码
        log.debug("发送短信验证码成功， 验证码： {}", code);

        // 返回ok
        return Result.ok();
    }

    @Override
    public Result login(LoginFormDTO loginForm, HttpSession session) {
        // 提交手机号和验证码
        String phone = loginForm.getPhone();
        log.debug("手机号： {}", phone);
        String code = loginForm.getCode();

        // 校验手机号
        if(RegexUtils.isPhoneInvalid(phone)){
            return Result.fail("手机号格式错误");
        }

        // 校验验证码
        Object cacheCode = redisTemplate.opsForValue().get(RedisConstants.LOGIN_CODE_KEY + phone);
        log.debug("缓存验证码：{}, 验证码： {}", cacheCode, code);
        if(cacheCode == null || !cacheCode.toString().equals(code)){
            return Result.fail("验证码错误");
        }

        // 查询用户信息
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, phone);
        User user = userMapper.selectOne(queryWrapper);

        // 不存在， 创建新用户并保存
        if(Objects.isNull(user)){
            user = new User();
            user.setPhone(phone);
            user.setNickName(RandomUtil.randomString(6));
            userMapper.insert(user);
        }

        log.debug("用户登录成功，用户信息：{}", user);

        // 保存用户信息到redis
        String token = UUID.randomUUID().toString(true);
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        redisTemplate.opsForValue().set(RedisConstants.LOGIN_USER_KEY + token, userDTO, RedisConstants.LOGIN_USER_TTL, TimeUnit.MINUTES);

        return Result.ok(token);
    }

    @Override
    public Result logout(HttpServletRequest request) {
        // 获取请求头中的token
        String token = request.getHeader("authorization");
        if(StrUtil.isBlank(token)){
            return Result.fail("用户未登录, 无法登出");
        }

        // 删除redis中的用户信息
        String key = RedisConstants.LOGIN_USER_KEY + token;
        redisTemplate.delete(key);

        return Result.ok("登出成功");
    }

}
