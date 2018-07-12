package com.silita.biaodaa.dao;

import com.snatch.model.AnalyzeDetail;

public interface ZhaobiaoAnalyzeDetailMapper{
    /**
     *
     * @param zhaobiaoAnalyzeDetail
     */
    void insertZhaobiaoAnalyzeDetail(AnalyzeDetail zhaobiaoAnalyzeDetail);

    /**
     *
     * @param url
     * @return
     */
    Integer getZhaobiaoAnalyzeDetailByUrl(String url);
}