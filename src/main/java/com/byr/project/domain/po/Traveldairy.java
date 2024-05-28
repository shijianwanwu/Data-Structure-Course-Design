package com.byr.project.domain.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author lrp
 * @since 2024-05-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("traveldairy")
public class Traveldairy implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "DairyID", type = IdType.AUTO)
    private Integer DairyID;

    @TableField("Dairy")
    private String Dairy;

    @TableField("rating")
    private Double rating;

    @TableField("Popularity")
    private Integer Popularity;

    @TableField("UserID")
    private Integer UserID;

    @TableField("ScenSpotID")
    private Integer ScenSpotID;

    @TableField("DairyName")
    private String DairyName;


}
