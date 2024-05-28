package com.byr.project.service;

import com.byr.project.domain.po.Sightseeing;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author author
 * @since 2024-05-04
 */
public interface ISightseeingService extends IService<Sightseeing> {

    public Sightseeing getSightseeingByName(String name);


}
