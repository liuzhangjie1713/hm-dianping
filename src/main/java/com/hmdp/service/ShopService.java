package com.hmdp.service;

import com.hmdp.dto.Result;
import com.hmdp.entity.Shop;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 商铺模块服务接口
 *
 * @author liuzhangjie
 * @date 2024-06-02
 */
public interface ShopService {

    /**
     * 根据id查询商铺信息
     * @param id 商铺id
     * @return 商铺详情数据
     */
    Shop queryById(Long id);

    /**
     * 新增商铺信息
     * @param shop 商铺数据
     * @return 商铺id
     */
    Long save(Shop shop);

    void update(Shop shop);

    List<Shop> queryByType(Integer typeId, Integer current);

    List<Shop> queryByName(String name, Integer current);
}
