package com.byr.project.controller;


import com.byr.project.domain.po.RestaurantFoods;
import com.byr.project.service.IRestaurantFoodsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lrp
 * @since 2024-05-08
 */
@RequestMapping("/foods")
@RestController
@RequiredArgsConstructor
@Api(tags = "美食管理接口")
@Slf4j
public class RestaurantFoodsController {
    private final IRestaurantFoodsService iRestaurantFoodsService;

    /**
     * 根据菜系等排序后返回前十个美食
     * 评价越高,分数越高,距离越近的越好
     * @param cuisine 菜系
     * @return 前十个美食
     */
    @ApiOperation("根据菜系等排序后返回前十个美食")
    @GetMapping("{cuisine}")
    public List<RestaurantFoods> queryFoodsByCategory(@ApiParam("菜系") @PathVariable("cuisine") String cuisine) {
        return iRestaurantFoodsService.queryFoodsByCuisine(cuisine);
    }

    @ApiOperation("输入美食名称,菜系,饭店等进行过滤查询")
    @GetMapping
    public List<RestaurantFoods> queryFoods(
            @RequestParam(required = false) String foodsName,
            @RequestParam(required = false) String foodsCuisine,
            @RequestParam(required = false) String restaurantName) {
        return iRestaurantFoodsService.queryFoods(foodsName, foodsCuisine, restaurantName);
    }

}
