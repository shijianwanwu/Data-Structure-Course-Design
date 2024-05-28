package com.byr.project.controller;



import com.byr.project.domain.po.Sightseeing;
import com.byr.project.service.ISightseeingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author author
 * @since 2024-05-04
 */
@RestController
@RequestMapping("/sightseeing")
@RequiredArgsConstructor
@Api(tags = "景区管理接口")
public class SightseeingController {
    private final ISightseeingService sightseeingService;

    @ApiOperation("根据名称查询景区接口")
    @GetMapping("/{name}")
    public Sightseeing getSightseeingByName(@ApiParam("景区名称") @PathVariable String name) {
        return sightseeingService.getSightseeingByName(name);
    }

    @ApiOperation("获取所有景区接口")
    @GetMapping("/all")
    public List<Sightseeing> getAllSightseeing() {
        return sightseeingService.list();
    }
}
