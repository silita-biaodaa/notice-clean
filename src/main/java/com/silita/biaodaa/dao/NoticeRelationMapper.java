package com.silita.biaodaa.dao;

import java.util.List;
import java.util.Map;

/**
 * Created by dh on 2018/6/28.
 */
public interface NoticeRelationMapper {

    List<String> querysLikeUrl (Map argMap);

    List<Map<String,Object>> querySimilarityNotice (Map argMap);
}
