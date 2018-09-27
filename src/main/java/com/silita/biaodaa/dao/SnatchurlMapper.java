package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.SnatchUrl;
import com.snatch.model.EsNotice;

import java.util.List;
import java.util.Map;

public interface SnatchurlMapper {
    /**
     *
     * @param map
     * @return
     */
    List<SnatchUrl> getSnatchUrlCountByUrl(Map map);

    /**
     *
     * @param params
     * @return
     */
    List<EsNotice> listSnatchUrl(Map params);

    /**
     *
     * @param params
     */
    void updateSnatchUrlById(Map params);

    /**
     *
     * @param map
     */
    int deleteSnatchUrlById(Map map);

    /**
     *
     * @param
     */
    void insertSnatchUrl(Map map);

    /**
     *
     * @param map
     * @return
     */
    Integer getMaxIdByUrl(Map map);

    /**
     *
     * @return
     */
    Integer getSnatchurlIdByUrl(Map map);

    /**
     *
     * @param map
     */
    int updateSnatchUrl(Map map);


    /**
     * 根据公示时间获取ES搜索引擎所需招标数据
     * @Param openDate
     * @return
     */
    List<Map<String, Object>> listESZhaoBiaoDateByOpenDate(String openDate);


    /**
     * 据公示时间获取ES搜索引擎所需中标数据
     * @Param openDate
     * @return
     */
    List<Map<String, Object>> listESZhongBiaoDateByOpenDate(String openDate);
}