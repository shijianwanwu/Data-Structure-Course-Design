package com.byr.project.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LineVO {
    private Integer startX;
    private Integer startY;
    private Integer endX;
    private Integer endY;
    private String vehicle;
}

