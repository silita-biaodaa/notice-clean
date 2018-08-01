package com.silita.biaodaa.service;

/**
 * Create by IntelliJ Idea 2018.1
 * Company: silita
 * Author: gemingyi
 * Date: 2018-08-01:15:25
 */
public interface IElasticSearchService {

    /**
     * 添加招标公告数据
     * @param openDate
     */
    void insertZhaoBiaoData(String openDate);

    /**
     * 添加中标公告数据
     * @param openDate
     */
    void insertZhongBiaoData(String openDate);
}
