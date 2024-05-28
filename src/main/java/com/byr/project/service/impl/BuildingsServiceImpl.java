package com.byr.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.byr.project.domain.po.*;
import com.byr.project.domain.vo.BuildingVO;
import com.byr.project.domain.vo.LineVO;
import com.byr.project.mapper.BuildingsMapper;
import com.byr.project.service.IBuildingsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lrp
 * @since 2024-05-04
 */
@Service
@Slf4j
public class BuildingsServiceImpl extends ServiceImpl<BuildingsMapper, SchoolBuildings> implements IBuildingsService {


    @Override
    public List<BuildingVO> queryBuildingsByCategory(String buildingCategory, String category) {
        List<BuildingVO> BuildingsList;
        if (category.equals("校园")) {
            List<SchoolBuildings> schoolBuildingsList = Db.lambdaQuery(SchoolBuildings.class)
                    .eq(buildingCategory != null, SchoolBuildings::getCategory, buildingCategory)
                    .list();
            BuildingsList = BeanUtil.copyToList(schoolBuildingsList, BuildingVO.class);
        } else {
            List<Scenebuildings> sceneBuildingsList = Db.lambdaQuery(Scenebuildings.class)
                    .eq(buildingCategory != null, Scenebuildings::getCategory, buildingCategory)
                    .list();
            BuildingsList = BeanUtil.copyToList(sceneBuildingsList, BuildingVO.class);
        }
        return BuildingsList;
    }

    /**
     * 根据起点和终点寻找最短路径
     *
     * @param startId
     * @param endId
     * @param category
     * @return
     */
    public List<Integer> findTheShortestPathBetweenTwoPoints(int startId, int endId, String category) {
        return findTheShortestPathBetweenTwoPoints(startId, endId, false, category);
    }

    public List<Integer> findTheShortestPathBetweenTwoPoints(int startId, int endId, boolean timeBased, String category) {
        List<? extends Road> roadsList;
        List<? extends Building> BuildingsList;
        if (category.equals("校园")) {
            roadsList = Db.lambdaQuery(Schoolroads.class)
                    .select(Schoolroads::getStartPoint, Schoolroads::getEndPoint, Schoolroads::getId, Schoolroads::getDistance, Schoolroads::getTime, Schoolroads::getVehicle)
                    .list();
            //得到所有建筑物信息
            BuildingsList = Db.lambdaQuery(SchoolBuildings.class)
                    .select(SchoolBuildings::getX, SchoolBuildings::getY, SchoolBuildings::getId)
                    .list();
        } else {
            roadsList = Db.lambdaQuery(Sceneroads.class)
                    .select(Sceneroads::getStartPoint, Sceneroads::getEndPoint, Sceneroads::getId, Sceneroads::getDistance, Sceneroads::getTime, Sceneroads::getVehicle)
                    .list();
            BuildingsList = Db.lambdaQuery(Scenebuildings.class)
                    .select(Scenebuildings::getX, Scenebuildings::getY, Scenebuildings::getId)
                    .list();
        }

        Graph graph;
        if (timeBased) {
            graph = Graph.fromListByTime(roadsList, BuildingsList);
        } else {
            graph = Graph.fromListByDistance(roadsList, BuildingsList);
        }
        // 返回最短路径上各路径的id
        List<Integer> shortestPath = graph.shortestPath(startId, endId);

        /*
            通过最短路径的id找到对应的道路并生成对应的LineVO
         */
        List<Road> shortestPathRoads = shortestPath.stream()
                .map(id -> roadsList.stream()
                        .filter(road -> road.getId().equals(id))
                        .findFirst()
                        .orElse(null))
                .collect(Collectors.toList());
        log.info("最短路径为：{}", shortestPathRoads);
        List<LineVO> lineVOS = new ArrayList<>();
        for (Road road : shortestPathRoads) {
            Building startBuilding = BuildingsList.stream()
                    .filter(building -> building.getId().equals(road.getStartPoint()))
                    .findFirst()
                    .orElse(null);

            Building endBuilding = BuildingsList.stream()
                    .filter(building -> building.getId().equals(road.getEndPoint()))
                    .findFirst()
                    .orElse(null);


            if (startBuilding != null && endBuilding != null) {
                lineVOS.add(new LineVO(startBuilding.getX(), startBuilding.getY(), endBuilding.getX(), endBuilding.getY(),road.getVehicle()));
            }
        }
        log.info("最短路径为：{}", lineVOS);

        return shortestPath;
    }

    @Override
    public List<Integer> findTheShortestPathOfPoints(List<Integer> ids) {
        //得到所有道路数据
        List<Schoolroads> list = Db.lambdaQuery(Schoolroads.class).list();
        //得到所有建筑物信息
        List<SchoolBuildings> schoolBuildingsList = lambdaQuery().list();
        //测试部分,可以返回所有道路的id
        List<Integer> test = new ArrayList<>();
        for (Schoolroads schoolroads :
                list) {
            test.add(schoolroads.getId());
        }

        return test;
    }

    @Override
    public List<Integer> findTheShortestTimeBetweenTwoPoints(int startId, int endId, String category) {
        return findTheShortestPathBetweenTwoPoints(startId, endId, true,category);
    }

    @Override
    public List<BuildingVO> findTheNearestPlaces(int startId, String buildingCategory, String category) {
        List<? extends Building> BuildingsList;
        if (category.equals("校园")) {
            BuildingsList = Db.lambdaQuery(SchoolBuildings.class)
                    .select()
                    .eq(buildingCategory != null, SchoolBuildings::getCategory, buildingCategory)
                    .list();
            System.out.println(BuildingsList.size());
        } else {
            BuildingsList = Db.lambdaQuery(Scenebuildings.class)
                    .select()
                    .eq(buildingCategory != null, Scenebuildings::getCategory, buildingCategory)
                    .list();
        }
        //预先计算到该类所有建筑物的最短路径长度
        Map<Integer, Double> shortestPathLengths = new HashMap<>();
        for (Building building : BuildingsList) {
            //如果是起点则距离为0
            if (building.getId().equals(startId)) {
                shortestPathLengths.put(building.getId(), 0.0);
                continue;
            }
            List<Integer> path = findTheShortestPathBetweenTwoPoints(startId, building.getId(), category);
            shortestPathLengths.put(building.getId(), findTheShortestLength(path));
        }

        //根据预先计算的最短路径长度进行排序
        BuildingsList = BuildingsList.stream()
                .sorted(Comparator.comparing(building -> shortestPathLengths.get(building.getId())))
                .collect(Collectors.toList());

        List<BuildingVO> buildingVOS = BeanUtil.copyToList(BuildingsList, BuildingVO.class);
        return buildingVOS;
    }

    @Override
    public List<BuildingVO> findThePlaceByName(String name, String category) {
        List<BuildingVO> BuildingsList;
        if (category.equals("校园")) {
            List<SchoolBuildings> schoolBuildingsList = Db.lambdaQuery(SchoolBuildings.class)
                    .eq(name != null, SchoolBuildings::getName, name)
                    .list();
            BuildingsList = BeanUtil.copyToList(schoolBuildingsList, BuildingVO.class);
        } else {
            List<Scenebuildings> sceneBuildingsList = Db.lambdaQuery(Scenebuildings.class)
                    .eq(name != null, Scenebuildings::getName, name)
                    .list();
            BuildingsList = BeanUtil.copyToList(sceneBuildingsList, BuildingVO.class);
        }
        return BuildingsList;
    }

    @Override
    public List<BuildingVO> queryAllBuildings(String category) {
        List<BuildingVO> BuildingsList;
        if (category.equals("校园")) {
            List<SchoolBuildings> schoolBuildingsList = Db.lambdaQuery(SchoolBuildings.class)
                    .list();
            BuildingsList = BeanUtil.copyToList(schoolBuildingsList, BuildingVO.class);
        } else {
            List<Scenebuildings> sceneBuildingsList = Db.lambdaQuery(Scenebuildings.class)
                    .list();
            BuildingsList = BeanUtil.copyToList(sceneBuildingsList, BuildingVO.class);
        }
        return BuildingsList;
    }

    public double findTheShortestLength(List<Integer> ids) {
        List<Schoolroads> schoolroadsList = Db.lambdaQuery(Schoolroads.class).in(Schoolroads::getId, ids).list();
        Double sum = 0.0;
        for (Schoolroads road :
                schoolroadsList) {
            sum += road.getDistance().doubleValue();
        }
        return sum;
    }


}
