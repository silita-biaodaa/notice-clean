package com.silita.biaodaa.dao;

import java.util.Map;

public interface ZhongbiaoDetailMapper {
    /**
     *
     * @param map
     */
    void updateZhongbiaoDetail(Map map);

    /**
     *
     * @param snatchUrlId
     * @return
     */
    Integer getOneNameLength(Integer snatchUrlId);

    /**
     *
     * @param snatchUrlId
     * @return
     */
    String getMegerOneName(Integer snatchUrlId);
}