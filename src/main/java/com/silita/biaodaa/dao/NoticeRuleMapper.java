package com.silita.biaodaa.dao;

import com.snatch.model.EsNotice;

import java.util.List;
import java.util.Map;

/**
 * Created by dh on 2018/9/3.
 */
public interface NoticeRuleMapper {

    List<Map> queryRulesByType(String type);

    List<EsNotice> matchEsNoticeList(Map argMap);
}
