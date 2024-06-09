package com.hmdp.controller;

import com.hmdp.dto.Result;
import com.hmdp.entity.Blog;
import com.hmdp.service.BlogService;
import com.hmdp.service.UserService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@RestController
@RequestMapping("/blog")
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;

    private final UserService userService;

    @PostMapping
    public Result saveBlog(@RequestBody Blog blog) {
        // 获取登录用户

        // 保存探店博文

        // 返回id

        return null;
    }

    @PutMapping("/like/{id}")
    public Result likeBlog(@PathVariable("id") Long id) {
        // 修改点赞数量
        return null;
    }

    @GetMapping("/of/me")
    public Result queryMyBlog(@RequestParam(value = "current", defaultValue = "1") Integer current) {
        // 获取登录用户

        // 根据用户查询

        // 获取当前页数据
        return null;
    }

    @GetMapping("/hot")
    public Result queryHotBlog(@RequestParam(value = "current", defaultValue = "1") Integer current) {
        // 根据用户查询

        // 获取当前页数据

        // 查询用户
        return null;
    }
}
