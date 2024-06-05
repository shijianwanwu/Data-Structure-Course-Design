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
    public List<LineVO> findTheShortestPathBetweenTwoPoints(int startId, int endId, String category) {
        return findTheShortestPathBetweenTwoPoints(startId, endId, false, category);
    }
    /**
     * 建图函数
     * @param timeBased 为真时按照时间建图，为假时按照距离建图
     * @param category
     * @return
     */
    public Graph makeGraph(boolean timeBased, String category){
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
        return graph;
    }
    public List<? extends Road> getRoadsListByCategory(String category) {
        if (category.equals("校园")) {
            return Db.lambdaQuery(Schoolroads.class)
                    .select(Schoolroads::getStartPoint, Schoolroads::getEndPoint, Schoolroads::getId, Schoolroads::getDistance, Schoolroads::getTime, Schoolroads::getVehicle)
                    .list();
        } else {
            return Db.lambdaQuery(Sceneroads.class)
                    .select(Sceneroads::getStartPoint, Sceneroads::getEndPoint, Sceneroads::getId, Sceneroads::getDistance, Sceneroads::getTime, Sceneroads::getVehicle)
                    .list();
        }
    }

    public List<? extends Building> getBuildingsListByCategory(String category) {
        if (category.equals("校园")) {
            return Db.lambdaQuery(SchoolBuildings.class)
                    .select(SchoolBuildings::getX, SchoolBuildings::getY, SchoolBuildings::getId)
                    .list();
        } else {
            return Db.lambdaQuery(Scenebuildings.class)
                    .select(Scenebuildings::getX, Scenebuildings::getY, Scenebuildings::getId)
                    .list();
        }
    }
    public List<LineVO> generateLineVOs(List<Integer> Path, List<? extends Road> roadsList, List<? extends Building> BuildingsList) {
        List<Road> shortestPathRoads = Path.stream()
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

        return lineVOS;
    }
    public List<LineVO> findTheShortestPathBetweenTwoPoints(int startId, int endId, boolean timeBased, String category) {
        Graph graph=makeGraph(timeBased, category);
        List<? extends Road> roadsList = getRoadsListByCategory(category);
        List<? extends Building> BuildingsList = getBuildingsListByCategory(category);
        // 返回最短路径上各路径的id
        List<Integer> shortestPath = graph.shortestPath(startId, endId);
        /*
            通过最短路径的id找到对应的道路并生成对应的LineVO
         */
        return generateLineVOs(shortestPath, roadsList, BuildingsList);
    }

    public List<Integer> findTheShortestPathBetweenTwoPointsReturnInteger(int startId, int endId, boolean timeBased, String category) {
        Graph graph=makeGraph(timeBased, category);
        // 返回最短路径上各路径的id
        List<Integer> shortestPath = graph.shortestPath(startId, endId);
        return shortestPath;
    }

    public List<LineVO> findTheShortestPathOfPoints(Integer start, List<Integer> ids,  boolean timeBased,String category) {
        Graph graph=makeGraph(timeBased, category);
        List<? extends Road> list = getRoadsListByCategory(category);
        List<? extends Building> buildingsList = getBuildingsListByCategory(category);
        List<Integer> points = graph.shortestPathMultiplePoints(ids);

        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < points.size() - 1; i++) {
            result.addAll(findTheShortestPathBetweenTwoPointsReturnInteger(points.get(i), points.get(i + 1), timeBased, category));
        }
        //将第一个节点作为终点，最后一个节点作为起点
        result.addAll(findTheShortestPathBetweenTwoPointsReturnInteger(points.get(points.size() - 1), points.get(0), timeBased, category));

        return generateLineVOs(result, list, buildingsList);
    }

    @Override
    public List<LineVO> findTheShortestPathOfPoints(Integer start, List<Integer> ids, String category) {
        return findTheShortestPathOfPoints(start, ids, false, category);
    }

    @Override
    public List<LineVO> findTheShortestTimeBetweenTwoPoints(int startId, int endId, String category) {
        return findTheShortestPathBetweenTwoPoints(startId, endId, true,category);
    }

    @Override
    public List<LineVO> findTheShortestTimeOfPoints(Integer start, List<Integer> ids, String category) {
        return findTheShortestPathOfPoints(start, ids, true,category);
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
            List<Integer> path = findTheShortestPathBetweenTwoPointsReturnInteger(startId, building.getId(), false,category);
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
                    .like(name != null, SchoolBuildings::getName, name)
                    .list();
            BuildingsList = BeanUtil.copyToList(schoolBuildingsList, BuildingVO.class);
        } else {
            List<Scenebuildings> sceneBuildingsList = Db.lambdaQuery(Scenebuildings.class)
                    .like(name != null, Scenebuildings::getName, name)
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
