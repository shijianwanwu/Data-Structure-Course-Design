package com.byr.project.service.impl;

import com.byr.project.domain.po.User;
import com.byr.project.mapper.UserMapper;
import com.byr.project.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lrp
 * @since 2024-05-08
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Override
    public Map<String, Object> login(User user) {
        User account = lambdaQuery()
                .eq(User::getUsername, user.getUsername())
                .eq(User::getPassword, user.getPassword())
                .one();
        if (account!=null){
            log.info("nb");
            Map<String, Object> data = new HashMap<>();
            String key = "user::" + UUID.randomUUID();
            data.put("token", key);    // 待优化，最终方案jwt
            account.setPassword(null);
            return data;
        }
        return null;
    }

    @Override
    public String getUsername(Integer userId) {
        User user = lambdaQuery()
                .eq(User::getId, userId)
                .one();
        if (user != null){
            return user.getUsername();
        }
        return null;
    }

    @Override
    public Integer getUserId(String username) {
        User user = lambdaQuery()
                .eq(User::getUsername, username)
                .one();
        if (user != null){
            return user.getId();
        }
        return null;
    }
}
