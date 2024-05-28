package com.byr.project.service.impl;

import com.byr.project.domain.po.Traveldairy;
import com.byr.project.mapper.TraveldairyMapper;
import com.byr.project.service.ITraveldairyService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.byr.project.utils.Compress;
import com.byr.project.utils.LRUCache;
import com.byr.project.utils.Search;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lrp
 * @since 2024-05-28
 */
@Slf4j
@Service
public class TraveldairyServiceImpl extends ServiceImpl<TraveldairyMapper, Traveldairy> implements ITraveldairyService {
    String DefaultPath = "src/main/java/com/byr/project/diaries";
    @Override
    public Traveldairy getDiaryById(Long id) {
        Traveldairy traveldairy = baseMapper.selectById(id);
        traveldairy.setDairy(Compress.decompress(DefaultPath, traveldairy.getDairyID() + "", traveldairy.getDairy()));
        return traveldairy;
    }

    @Override
    public boolean addDairy(Traveldairy traveldairy) throws IOException {
        String dairy = traveldairy.getDairy();
        traveldairy.setDairy("");
        int insert = baseMapper.insert(traveldairy);
        if (insert > 0) {
            String compressedContent = Compress.compress(dairy, DefaultPath, traveldairy.getDairyID()+"");
            traveldairy.setDairy(compressedContent);
            baseMapper.updateById(traveldairy);
            return true;
        }
        return false;
    }

    @Override
    public List<Traveldairy> getAllDiaries(Long scenSpotID) {
        List<Traveldairy> list = lambdaQuery()
                .eq(scenSpotID != null, Traveldairy::getScenSpotID, scenSpotID)
                .list();
        list.stream().forEach(traveldairy -> {
            traveldairy.setDairy(Compress.decompress(DefaultPath, traveldairy.getDairyID() + "", traveldairy.getDairy()));
        });

        // 对列表进行排序
        list.sort((d1, d2) -> {
            int popularityCompare = d2.getPopularity().compareTo(d1.getPopularity());
            if (popularityCompare != 0) {
                return popularityCompare;
            } else {
                return d2.getRating().compareTo(d1.getRating());
            }
        });

        return list;
    }

    @Override
    public boolean scoreDiary(Long id, Integer score) {
        Traveldairy traveldairy = baseMapper.selectById(id);
        Integer popularity = traveldairy.getPopularity();
        traveldairy.setRating((traveldairy.getRating()*popularity+score)/(double)(popularity+1));
        traveldairy.setPopularity(popularity+1);
        int i = baseMapper.updateById(traveldairy);
        return i > 0;
    }

    /**考虑游学日记数量较大，变化非常快的情况下进行高效查找
     * 优化方法：
     * 1.数据库给名称字段添加索引
     * 2.使用缓存：考虑使用缓存来存储最近查询的日记。这可以减少对数据库的查询，特别是对于频繁查询的日记。
     * 使用LRU（最近最少使用）策略：使用一个基于LRU策略的缓存，当缓存满时，LRU策略会移除最近最少使用的缓存项。
     */

    private Map<String, List<Traveldairy>> cache = new LRUCache<>(1000);
    @Override
    public List<Traveldairy> findDiaryByName(String name) {
        if (cache.containsKey(name)) {
            return cache.get(name);
        } else {
            List<Traveldairy> diaries = lambdaQuery().eq(Traveldairy::getDairyName, name).list();
            diaries.stream().forEach(traveldairy -> {
                traveldairy.setDairy(Compress.decompress(DefaultPath, traveldairy.getDairyID() + "", traveldairy.getDairy()));
            });
            cache.put(name, diaries);
            return diaries;
        }
    }

    @Override
    public Traveldairy findDiaryByContent(Long id, String content) {
        Traveldairy traveldairy = lambdaQuery().eq(Traveldairy::getDairyID, id).one();
        if (traveldairy != null) {
            traveldairy.setDairy(Compress.decompress(DefaultPath, traveldairy.getDairyID() + "", traveldairy.getDairy()));
            log.info(traveldairy.getDairy());
            if (Search.search(content,traveldairy.getDairy())) {
                return traveldairy;
            }
        }
        return null;
    }

}
