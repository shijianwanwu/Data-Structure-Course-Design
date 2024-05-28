package com.byr.project.service.impl;

import com.byr.project.mapper.SightseeingMapper;
import com.byr.project.service.ISightseeingService;
import com.byr.project.domain.po.Sightseeing;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author author
 * @since 2024-05-04
 */
@Service
public class SightseeingServiceImpl extends ServiceImpl<SightseeingMapper, Sightseeing> implements ISightseeingService {

    public Sightseeing getSightseeingByName(String name) {
        return lambdaQuery()
                .eq(name != null, Sightseeing::getName, name).one();

    }

}
