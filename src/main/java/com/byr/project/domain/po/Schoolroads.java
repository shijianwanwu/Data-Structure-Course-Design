package com.byr.project.domain.po;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 学校道路表
 * </p>
 *
 * @author lrp
 * @since 2024-05-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("schoolroads")
public class Schoolroads implements Serializable, Road {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
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
