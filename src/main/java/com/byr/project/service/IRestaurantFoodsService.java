package com.byr.project.service;

import com.byr.project.domain.po.RestaurantFoods;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lrp
 * @since 2024-05-08
 */
public interface IRestaurantFoodsService extends IService<RestaurantFoods> {

    List<RestaurantFoods> queryFoodsByCuisine(String cuisine);

    List<RestaurantFoods> queryFoods(String foodsName, String foodsCuisine, String restaurantName);
}
