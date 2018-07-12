package com.silita.biaodaa.dao;

import com.snatch.model.AnalyzeDetailZhongBiao;

public interface ZhongbiaoAnalyzeDetailMapper {
    /**
     *
     */
    void insertZhongBiaoAnalyzeDetail(AnalyzeDetailZhongBiao zhongbiaoAnalyzeDetail);

    /**
     *
     * @param url
     */
    Integer getZhongBiaoAnalyzeDetailByUrl(String url);
}