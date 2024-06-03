package com.byr.project.service.impl;

import com.byr.project.mapper.SightseeingMapper;
import com.byr.project.service.ISightseeingService;
import com.byr.project.domain.po.Sightseeing;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author author
 * @since 2024-05-04
 */
@Slf4j
@Service
public class SightseeingServiceImpl extends ServiceImpl<SightseeingMapper, Sightseeing> implements ISightseeingService {

    public Sightseeing getSightseeingByName(String name) {
        return lambdaQuery()
                .eq(name != null, Sightseeing::getName, name).one();

    }

    @Override
    public List<Sightseeing> searchSightseeing(String name, String category) {
        List<Sightseeing> sightseeingList = lambdaQuery()
                .like(name != null, Sightseeing::getName, name)
                .eq(category != null, Sightseeing::getCategory, category)
                .list();

        log.info(sightseeingList.toString());
        // 创建一个小顶堆
        //评价越高,热度越高的越好
        PriorityQueue<Sightseeing> heap = new PriorityQueue<>(10, Comparator
                .comparing(Sightseeing::getRating)
                .thenComparing(Sightseeing::getPopularity)
                );

        // 遍历列表，将元素添加到堆中
        for (Sightseeing sightseeing : sightseeingList) {
            if (heap.size() < 10) {
                heap.add(sightseeing);
            } else if (heap.comparator().compare(sightseeing, heap.peek()) > 0) {
                heap.poll();
                heap.add(sightseeing);
            }
        }

        // 将堆中的元素添加到结果列表中
        List<Sightseeing> result = new ArrayList<>(heap);
        //为堆内元素排序
        Collections.sort(result, heap.comparator().reversed());

        return result;
    }

}
