package com.byr.project.domain.vo;

import com.byr.project.domain.po.Building;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BuildingVO implements Building {
    private Integer id;

    private String category;

    private String name;

    /**
     * 坐标
     */
    private Integer x;

    /**
     * 坐标
     */
    private Integer y;
}
