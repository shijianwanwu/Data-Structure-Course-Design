package com.byr.project.controller;


import com.byr.project.common.vo.Result;
import com.byr.project.domain.po.Traveldairy;
import com.byr.project.service.ITraveldairyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lrp
 * @since 2024-05-28
 */
@RestController
@RequiredArgsConstructor
@Api(tags = "日记管理接口")
@Slf4j
@RequestMapping("/traveldairy")
public class TraveldairyController {

    /**
     * 根据传入的Traveldairy对象添加日记
     */
    @ApiOperation("添加日记")
    @PostMapping("/add")
    public Result addDairy(@RequestBody Traveldairy traveldairy) throws IOException {
        boolean flag = iTraveldairyService.addDairy(traveldairy);
        if (flag) {
            return Result.success("添加成功");
        }
        return Result.fail("添加失败");
    }

    /**
     * 获取所有游学日记,返回按照日记热度和评分排序后的结果
     * @return
     */
    @ApiOperation("获取所有游学日记，可根据目的地筛选")
    @GetMapping
    public List<Traveldairy> getAllDiaries(@RequestParam(required = false) Long ScenSpotID) {
        return iTraveldairyService.getAllDiaries(ScenSpotID);
    }

    private final ITraveldairyService iTraveldairyService;
    @ApiOperation("根据ID获取游学日记")
    @GetMapping("/{id}")
    public Traveldairy getDiaryById(@ApiParam("游学日记ID") @PathVariable Long id) {
        return iTraveldairyService.getDiaryById(id);
    }

    @ApiOperation("给日记评分")
    @PatchMapping("/score")
    public Result scoreDiary(@RequestParam Long id, @RequestParam Integer score) {
        boolean flag = iTraveldairyService.scoreDiary(id, score);
        if (flag) {
            return Result.success("评分成功");
        }
        return Result.fail("评分失败");
    }

    @ApiOperation("根据名称查询日记")
    @GetMapping("/diary")
    public List<Traveldairy> findDiaryByName(@RequestParam String name) {
        return iTraveldairyService.findDiaryByName(name);
    }

    @ApiOperation("查询指定日记是否有指定内容")
    @GetMapping("/content")
    public Result<Traveldairy> findDiaryByContent(@RequestParam Long id, @RequestParam String content) {
        Traveldairy diaryByContent = iTraveldairyService.findDiaryByContent(id, content);
        if ( diaryByContent != null) {
            return Result.success(diaryByContent);
        }
        else {
            return Result.fail("未找到指定内容");
        }
    }

    @ApiOperation("根据用户ID返回游学日记")
    @GetMapping("/user")
    public List<Traveldairy> getDiariesByUserId(@RequestParam Long userId) {
        return iTraveldairyService.getDiariesByUserId(userId);
    }

    @ApiOperation("根据id删除日记")
    @DeleteMapping("/{id}")
    public Result deleteDiaryById(@PathVariable Long id) {
        boolean flag = iTraveldairyService.removeById(id);

        if (flag) {
            return Result.success("删除成功");
        }
        return Result.fail("删除失败");
    }

    @ApiOperation("更新日记接口传ID加更新后的内容")
    @PutMapping("/{id}")
    public Result updateDiary(@PathVariable Integer id, @RequestBody String content) {
        Traveldairy traveldairy = iTraveldairyService.getById(id);
        // 去除字符串的双引号
        content = content.replace("\"", "");
        //log.info(content);
        traveldairy.setDairy(content);
        boolean flag = iTraveldairyService.updateById(traveldairy);
        if (flag) {
            return Result.success("更新成功");
        }
        return Result.fail("更新失败");
    }


}
