package com.byr.project.service;

import com.byr.project.domain.po.Traveldairy;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lrp
 * @since 2024-05-28
 */
public interface ITraveldairyService extends IService<Traveldairy> {

    Traveldairy getDiaryById(Long id);

    boolean addDairy(Traveldairy traveldairy) throws IOException;

    List<Traveldairy> getAllDiaries(Long scenSpotID);

    boolean scoreDiary(Long id, Integer score);

    List<Traveldairy> findDiaryByName(String name);

    Traveldairy findDiaryByContent(Long id, String content);

    List<Traveldairy> getDiariesByUserId(Long userId);

    boolean removeById(Long id);

    boolean updateById(Traveldairy traveldairy);
}
