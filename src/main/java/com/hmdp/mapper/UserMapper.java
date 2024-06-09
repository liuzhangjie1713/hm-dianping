package com.hmdp.mapper;

import com.hmdp.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * Mapper 接口
 *
 * @author liuzhangjie
 * @date 2024-04-16
 */
@Mapper
public interface UserMapper extends BaseMapper<User>{

}
