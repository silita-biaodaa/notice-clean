package com.silita.biaodaa.service;

import com.silita.biaodaa.dao.NoticeRuleMapper;
import com.silita.biaodaa.utils.RouteUtils;
import com.snatch.model.EsNotice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by dh on 2018/9/3.
 */
@Service
public class NoticeRuleService {

    @Autowired
    NoticeRuleMapper noticeRuleMapper;

    public List<Map> queryRulesByType(String type){
        return noticeRuleMapper.queryRulesByType(type);
    }

    public List<EsNotice> matchEsNoticeList(Map params){
        int type = (int)params.get("type");
        String source= (String)params.get("source");
        params.put("snatchurlTable", RouteUtils.routeTableName("mishu.snatchurl", source));
        params.put("snatchurlContentTable", RouteUtils.routeTableName("mishu.snatchurlcontent", source));

        String detailTable = null;
        if(type==2) {
            detailTable = "mishu.zhongbiao_detail";
        }else{
            detailTable ="mishu.zhaobiao_detail";
        }
        params.put("detailTable", detailTable);
        return noticeRuleMapper.matchEsNoticeList(params);
    }
}
