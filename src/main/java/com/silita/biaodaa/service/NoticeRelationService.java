package com.silita.biaodaa.service;

import com.silita.biaodaa.dao.NoticeRelationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dh on 2018/6/28.
 */
@Service
public class NoticeRelationService {
    @Autowired
    private NoticeRelationMapper mapper;

    public List<String> querysLikeUrl(Map argMap){
        return mapper.querysLikeUrl(argMap);
    }

    @Cacheable(value = "similarityNotice", key="#argMap.get('openDate')+#argMap.get('websiteUrl')+#argMap.get('tempTitle')")
    public List<Map<String,Object>> querySimilarityNotice (Map argMap) {
        argMap.put("dayRegion",30);
        argMap.put("limitCount",1000);
        return mapper.querySimilarityNotice(argMap);
    }

    public String queryThisId(Map argMap){
        return mapper.queryThisId(argMap);
    }

    public List<String> queryRelationNextIds (String nextId){
        return mapper.queryRelationNextIds(nextId);
    }

    public void batchInsertRelation (String thisId, List<String> nextIds){
        Map argMap = new HashMap();
        argMap.put("thisId",thisId);
        argMap.put("nextIds",nextIds);
        mapper.batchInsertRelation(argMap);
    }

    public void insertSnatchRelation(String mainId, String nextId){
        Map argMap = new HashMap();
        argMap.put("thisId",mainId);
        argMap.put("nextId",nextId);
        mapper.insertSnatchRelation(argMap);
    }
}
