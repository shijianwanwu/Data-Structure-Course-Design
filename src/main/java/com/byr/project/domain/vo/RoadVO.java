package com.byr.project.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoadVO {
    private Integer id;

    /**
     * 起点
     */
    private Integer startPoint;

    /**
     * 终点
     */
    private Integer endPoint;

    /**
     * 距离
     */
    private BigDecimal distance;

    /**
     * 拥挤度
     */
    private BigDecimal congestion;

    /**
     * 时间
     */
    private BigDecimal time;

    /**
     * 使用的交通工具
     */
    private String Vehicle;
}
