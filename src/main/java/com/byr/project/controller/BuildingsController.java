package com.byr.project.controller;


import cn.hutool.core.bean.BeanUtil;
import com.byr.project.domain.po.Building;
import com.byr.project.domain.po.SchoolBuildings;
import com.byr.project.domain.vo.BuildingVO;
import com.byr.project.domain.vo.LineVO;
import com.byr.project.service.IBuildingsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author lrp
 * @since 2024-05-04
 */
@RequestMapping("/buildings")
@RestController
@RequiredArgsConstructor
@Api(tags = "建筑物管理接口")
@Slf4j
public class BuildingsController {
    /**
     * category:校园/景点
     */

    private final IBuildingsService iBuildingsService;

    @ApiOperation("按照类别查询建筑物")
    @GetMapping("{buildingCategory}")
    public List<BuildingVO> queryBuildingsByCategory(@ApiParam("类别") @PathVariable("buildingCategory") String buildingCategory,@RequestParam String category) {
        return iBuildingsService.queryBuildingsByCategory(buildingCategory,category);
    }

    @ApiOperation("寻找两点最短路径")
    @GetMapping("/point")
    public List<LineVO> findTheShortestPathBetweenTwoPoints(@RequestParam int startId, @RequestParam int endId,@RequestParam String category) {
        return iBuildingsService.findTheShortestPathBetweenTwoPoints(startId, endId,category);
    }

    @ApiOperation("寻找多点最短路径")
    @GetMapping("/points")
    public List<LineVO> findTheShortestPathOfPoints(@RequestParam Integer start,@RequestParam List<Integer> ids,@RequestParam String category) {
        log.info(ids + "");
        return iBuildingsService.findTheShortestPathOfPoints(start,ids,category);
    }

    @ApiOperation("寻找多点最短时间")
    @GetMapping("/times")
    public List<LineVO> findTheShortestTimeOfPoints(@RequestParam Integer start,@RequestParam List<Integer> ids,@RequestParam String category) {
        return iBuildingsService.findTheShortestTimeOfPoints(start,ids,category);
    }

    @ApiOperation("寻找两点最短时间")
    @GetMapping("/time")
    public List<LineVO> findTheShortestTimeBetweenTwoPoints(@RequestParam int startId, @RequestParam int endId, @RequestParam String category) {
        return iBuildingsService.findTheShortestTimeBetweenTwoPoints(startId, endId,category);
    }


    @ApiOperation("选中场所根据类别查询")
    @GetMapping("/category")
    public List<BuildingVO> findTheNearestPlace(@RequestParam int startId, @ApiParam("建筑物类别")@RequestParam String buildingCategory,@ApiParam("类别")@RequestParam String category) {
        return iBuildingsService.findTheNearestPlaces(startId,buildingCategory, category);
    }

    @ApiOperation("根据名称查询场所")
    @GetMapping("/search")
    public List<BuildingVO> findThePlaceByName(@RequestParam String name, @RequestParam String category) {
        return iBuildingsService.findThePlaceByName(name,category);
    }

    @ApiOperation("查询所有场所")
    @GetMapping
    public List<BuildingVO> queryAllBuildings(@ApiParam("类别")@RequestParam String category) {
        return iBuildingsService.queryAllBuildings(category);
    }
}
