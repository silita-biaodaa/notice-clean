package com.silita.biaodaa.rules.ruleImpl.others;

import com.silita.biaodaa.rules.Interface.RepeatRule;
import com.silita.biaodaa.rules.ruleImpl.hunan.HunanBaseRule;
import com.silita.biaodaa.service.INoticeCleanService;
import com.snatch.model.EsNotice;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OthersRepeatRule extends HunanBaseRule implements RepeatRule {
    @Autowired
    INoticeCleanService noticeCleanService;

    private static Logger logger = Logger.getLogger(OthersRepeatRule.class);

    @Override
    public boolean executeRule(EsNotice esNotice) {
        logger.debug("全国公告[redisId:"+esNotice.getRedisId()+"][source:"+esNotice.getSource()+"][ur:"+esNotice.getUrl()+"][title:" + esNotice.getTitle() + "][openDate:"+esNotice.getOpenDate()+"]");
        try {
            //url已存在不入库
            int isExist = noticeCleanService.countSnastchUrlByUrl(esNotice);
            if (isExist == 1) {
                logger.info("#### 全国公告数据库中已存在相同url，[source:"+esNotice.getSource()+"][ur:"+esNotice.getUrl()+"]");
                return false;
            } else {
                handleNotRepeat(esNotice);
                return true;
            }
        }catch (Exception e){
            logger.error("全国公告异常，[redisId:"+esNotice.getRedisId()+"][source:"+esNotice.getSource()+"][ur:"+esNotice.getUrl()+"][title:" + esNotice.getTitle() + "][openDate:"+esNotice.getOpenDate()+"]",e);
            throw e;
        }
    }

}
