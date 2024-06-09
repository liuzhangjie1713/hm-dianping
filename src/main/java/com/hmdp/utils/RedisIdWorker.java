package com.hmdp.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * @author liuzhangjie
 * @date 2024/06/08
 */

@Component
@RequiredArgsConstructor
public class RedisIdWorker {

    private final StringRedisTemplate redisTemplate;
    private static final long BEGIN_TIMESTAMP = 1704067200L;
    private static final int SEQUENCE_BITS = 32;

    public long nextId(String keyPrefix) {
        // 1. 生成时间戳
        LocalDateTime now = LocalDateTime.now();
        long timestamp = now.toEpochSecond(ZoneOffset.UTC) - BEGIN_TIMESTAMP;

        // 2. 生成递增序列号
        String date = now.format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
        long sequence = redisTemplate.opsForValue().increment("id:" + keyPrefix + ":" + date, 1L);

        // 3. 拼接成最终的ID
        return (timestamp << SEQUENCE_BITS) | sequence;
    }

}
