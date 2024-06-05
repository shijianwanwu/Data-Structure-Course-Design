package com.byr.project.service;

import com.byr.project.domain.po.SchoolBuildings;
import com.baomidou.mybatisplus.extension.service.IService;
import com.byr.project.domain.vo.BuildingVO;
import com.byr.project.domain.vo.LineVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lrp
 * @since 2024-05-04
 */
public interface IBuildingsService extends IService<SchoolBuildings> {

    List<BuildingVO> queryBuildingsByCategory(String buildingCategory, String category);

    /**
     * 根据起点和终点寻找最短路径
     *
     * @param startId
     * @param endId
     * @param category
     * @return
     */
    List<LineVO> findTheShortestPathBetweenTwoPoints(int startId, int endId, String category);

    /***
     * 寻找经过多点的最短路径
     *
     * @param start
     * @param ids
     * @param category
     * @return
     */
    List<LineVO> findTheShortestPathOfPoints(Integer start, List<Integer> ids, String category);

    List<LineVO> findTheShortestTimeBetweenTwoPoints(@Param("startId") int startId, @Param("endId") int endId, String category);

    /**
     * 根据指定场所找到指定类别的建筑物,并按距离从小到大返回
     *
     * @param startId
     * @param category
     * @param buildingCategory
     * @return
     */
    List<BuildingVO> findTheNearestPlaces(int startId, String buildingCategory, String category);

    List<BuildingVO> findThePlaceByName(String name, String category);

    List<BuildingVO> queryAllBuildings(String category);

    List<LineVO> findTheShortestTimeOfPoints(Integer start, List<Integer> ids, String category);

    List<LineVO> getAllRoads(String category);
}
