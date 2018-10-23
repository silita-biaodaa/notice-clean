package com.silita.biaodaa.dao;

import com.snatch.model.EsNotice;

/**
 * Created by dh on 2018/10/11.
 */
public interface CleanMapper {
    void updateNoticeShowStatus(EsNotice esNotice);

    void insertAnalysisDetail(EsNotice esNotice);
}
