package com.byr.project.controller;


import com.byr.project.common.vo.Result;
import com.byr.project.domain.po.User;
import com.byr.project.service.IUserService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lrp
 * @since 2024-05-08
 */

@RequestMapping("/user")
@RestController
@RequiredArgsConstructor
@Api(tags = "用户管理接口")
@Slf4j
public class UserController {

    private final IUserService userService;

    @PostMapping("/login")
    public Result<Map<String,Object>> login(@RequestBody User user){
        Map<String,Object> data = userService.login(user);
        log.info(user.getPassword()+user.getUsername());
        if(data != null){
            return Result.success(data);
        }
        return Result.fail(20002,"用户名或密码错误");
    }
}
