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
    SnatchUrl getSnatchUrlCountByUrl(Map map);

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
     * @param snatchUrlId
     */
    void deleteSnatchUrlById(String snatchUrlId);

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
    void updateSnatchUrl(Map map);


}