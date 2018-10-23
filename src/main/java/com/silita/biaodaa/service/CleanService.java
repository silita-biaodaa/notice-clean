package com.silita.biaodaa.service;

import com.silita.biaodaa.common.Constant;
import com.silita.biaodaa.dao.CleanMapper;
import com.silita.biaodaa.utils.RouteUtils;
import com.snatch.model.EsNotice;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by dh on 2018/10/11.
 */
@Service
public class CleanService {

    private Logger logger = Logger.getLogger(CleanService.class);

    @Autowired
    private CleanMapper cleanMapper;

    /**
     * 解析数据直接入表
     * @param esNotice
     */
    public void storeAnalysisDetail(EsNotice esNotice){
        //插入解析信息到编辑表
        String tabName = RouteUtils.routerDetailTabName(esNotice.getType(),esNotice.getSource());
        esNotice.setDetailTabName(tabName);
        if(esNotice.getSource()==null) {
            esNotice.setSource(Constant.HUNAN_SOURCE);
        }
        cleanMapper.insertAnalysisDetail(esNotice);

        //更新公告状态为可见
        esNotice.setTableName(RouteUtils.routeTableName("mishu.snatchurl",esNotice));
        logger.info(esNotice.getTableName()+"#########"+esNotice.getUuid());
        cleanMapper.updateNoticeShowStatus(esNotice);
    }

}
