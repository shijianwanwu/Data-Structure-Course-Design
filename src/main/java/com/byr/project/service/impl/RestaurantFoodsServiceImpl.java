package com.byr.project.service.impl;

import com.byr.project.domain.po.RestaurantFoods;
import com.byr.project.mapper.RestaurantFoodsMapper;
import com.byr.project.service.IRestaurantFoodsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lrp
 * @since 2024-05-08
 */
@Service
public class RestaurantFoodsServiceImpl extends ServiceImpl<RestaurantFoodsMapper, RestaurantFoods> implements IRestaurantFoodsService {

    /**
     * 根据评价.热度和距离不完全排序返回前十个美食
     *
     * @param cuisine
     * @return
     */
    @Override
    public List<RestaurantFoods> queryFoodsByCuisine(String cuisine) {
        List<RestaurantFoods> restaurantFoodsList = lambdaQuery()
                .eq(cuisine != null, RestaurantFoods::getFoodsCuisine, cuisine)
                .list();

        // 创建一个小顶堆
        //评价越高,分数越高,距离越近的越好
        PriorityQueue<RestaurantFoods> heap = new PriorityQueue<>(10, Comparator
                .comparing(RestaurantFoods::getFoodsRating)
                .thenComparing(RestaurantFoods::getFoodsHeat)
                .thenComparing(RestaurantFoods::getRestaurantDistance, Comparator.reverseOrder()));

        // 遍历列表，将元素添加到堆中
        for (RestaurantFoods restaurantFoods : restaurantFoodsList) {
            if (heap.size() < 10) {
                heap.add(restaurantFoods);
            } else if (heap.comparator().compare(restaurantFoods, heap.peek()) > 0) {
                heap.poll();
                heap.add(restaurantFoods);
            }
        }

        // 将堆中的元素添加到结果列表中
        List<RestaurantFoods> result = new ArrayList<>(heap);
        //为堆内元素排序
        Collections.sort(result, heap.comparator().reversed());
        return result;
    }

    @Override
    public List<RestaurantFoods> queryFoods(String foodsName, String foodsCuisine, String restaurantName) {
        List<RestaurantFoods> restaurantFoodsList = lambdaQuery()
                .like(foodsName != null, RestaurantFoods::getFoodsName, foodsName)
                .eq(foodsCuisine != null, RestaurantFoods::getFoodsCuisine, foodsCuisine)
                .like(restaurantName != null, RestaurantFoods::getRestaurantName, restaurantName)
                .orderByDesc(RestaurantFoods::getFoodsRating,RestaurantFoods::getFoodsHeat)
                .orderByAsc(RestaurantFoods::getRestaurantDistance)
                .list();
        return restaurantFoodsList;
    }
}
