package com.hmdp.service.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hmdp.dto.Result;
import com.hmdp.entity.Shop;
import com.hmdp.mapper.ShopMapper;
import com.hmdp.service.ShopService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.utils.SystemConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.hmdp.utils.RedisConstants.*;

/**
 * 商铺模块服务实现类
 *
 * @author liuzhangjie
 * @date 2024-06-02
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ShopServiceImpl implements ShopService {

    private final RedisTemplate<String, Object> redisTemplate;

    private final ShopMapper shopMapper;
    @Override
    public Shop queryById(Long id) {
        return queryWithMutex(id);
    }

    @Override
    public Long save(Shop shop) {
        shopMapper.insert(shop);
        return shop.getId();
    }

    @Override
    public void update(Shop shop) {
        // 打印shop
        log.debug("shop: {}", shop);
        shopMapper.updateById(shop);
        redisTemplate.delete(CACHE_SHOP_KEY + shop.getId());
    }

    @Override
    public List<Shop> queryByType(Integer typeId, Integer current) {
        LambdaQueryWrapper<Shop> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(Shop::getTypeId, typeId);
        Page<Shop> shopPage = shopMapper.selectPage(new Page<>(current, SystemConstants.DEFAULT_PAGE_SIZE), queryWrapper);
        return shopPage.getRecords();
    }

    @Override
    public List<Shop> queryByName(String name, Integer current) {
        LambdaQueryWrapper<Shop> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.like(Shop::getName, name);
        Page<Shop> shopPage = shopMapper.selectPage(new Page<>(current, SystemConstants.DEFAULT_PAGE_SIZE), queryWrapper);
        return shopPage.getRecords();
    }


    // 互斥锁解决缓存击穿
    private Shop queryWithMutex(Long id) {
        //先查Redis
        Object res = redisTemplate.opsForValue().get(CACHE_SHOP_KEY + id);

        //如果查到了，直接返回
        if(!Objects.isNull(res)) {
            Shop shop = JSONUtil.toBean(res.toString(), Shop.class);
            return shop;
        }

        //实现在高并发的情况下缓存重建
        Shop shop = null;
        try {
            //1.获取互斥锁
            boolean flag = tryLock(LOCK_SHOP_KEY + id);
            // 2.失败，则休眠并重试
            while (!flag) {
                Thread.sleep(50);
                return queryWithMutex(id);
            }
            //3.获取成功->读取数据库，重建缓存
            //查不到，则将空值写入Redis
            shop = shopMapper.selectById(id);
            if (shop == null) {
                redisTemplate.opsForValue().set(CACHE_SHOP_KEY + id, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
                return null;
            }
            // 4.查询到了，将查询到的商户信息写入Redis
            redisTemplate.opsForValue().set(CACHE_SHOP_KEY + id, shop, CACHE_SHOP_TTL, TimeUnit.MINUTES);
            //最终把查询到的商户信息返回给前端
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            releaseLock(LOCK_SHOP_KEY + id);
        }
        return shop;
    }

    private boolean tryLock(String key) {
        Boolean flag = redisTemplate.opsForValue().setIfAbsent(key, "1", 10, TimeUnit.SECONDS);
        //避免返回值为null，我们这里使用了BooleanUtil工具类
        return BooleanUtil.isTrue(flag);
    }

    private void releaseLock(String key) {
        redisTemplate.delete(key);
    }
}
