package com.silita.biaodaa.service;

import com.silita.biaodaa.dao.NoticeRelationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dh on 2018/6/28.
 */
public class NoticeRelationService {
    @Autowired
    private NoticeRelationMapper mapper;

    public List<String> querysLikeUrl(Map argMap){
        return mapper.querysLikeUrl(argMap);
    }

    @Cacheable(value = "similarityNotice", key="#openDate+#websiteUrl+#tempTitle")
    public List<Map<String,Object>> querySimilarityNotice (String openDate, String websiteUrl, String tempTitle) {
        Map argMap = new HashMap();
        argMap.put("openDate",openDate);
        argMap.put("websiteUrl",websiteUrl);
        argMap.put("tempTitle",tempTitle);
        argMap.put("dayRegion",30);
        argMap.put("limitCount",1000);
        return mapper.querySimilarityNotice(argMap);
    }
}
