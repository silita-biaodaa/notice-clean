package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.Snatchpress;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;
import java.util.Map;

public interface SnatchpressMapper extends MyMapper<Snatchpress> {

    Snatchpress getSnatchpress(Integer id);

    List<Map<String, Object>> queryzh();

    List<Map<String, Object>> queryAnalyzeRangeByField(String field);

    void insertUnanalysis_aptitude(Map<String,Object> param);

    Map<String,Object> getAptitudeDictionary(String uuid);

    void insertSnatchUrlCert(Map<String,Object> param);

    List<Map<String,Object>> getBuildZhList();

    List<String> getAptitudeDictionaryList(String uuid);

    List<Map<String,Object>> getSnatchUrlCert(String contId);

    void insertSnatchUrlBuild(Map<String,Object> param);

    List<String> getBuildZh(Map<String,Object> param);




}