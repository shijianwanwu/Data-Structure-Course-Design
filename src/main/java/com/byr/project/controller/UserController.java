package com.byr.project.controller;


import com.byr.project.common.vo.Result;
import com.byr.project.domain.po.User;
import com.byr.project.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 根据用户id返回用户名，id为Integer类型
     */
    @ApiOperation("根据用户id返回用户名")
    @GetMapping("/getUsername")
    public Result<String> getUsername(@ApiParam("用户id") @RequestParam Integer userId){
        String username = userService.getUsername(userId);
        if(username != null){
            return Result.success(username);
        }
        return Result.fail(20002,"用户名或密码错误");
    }

    /**
     * 根据用户名返回用户id
     */
    @ApiOperation("根据用户名返回用户id")
    @GetMapping("/getUserId")
    public Result<Integer> getUserId(@ApiParam("用户名") @RequestParam String username){
        Integer userId = userService.getUserId(username);
        if(userId != null){
            return Result.success(userId);
        }
        return Result.fail(20002,"用户名或密码错误");
    }
}
