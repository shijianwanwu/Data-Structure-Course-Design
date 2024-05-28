package com.byr.project.service;

import com.byr.project.domain.po.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lrp
 * @since 2024-05-08
 */
public interface IUserService extends IService<User> {

    Map<String, Object> login(User user);
}
