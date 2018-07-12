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
        logger.debug("全国新公告[title：" + esNotice.getTitle()+"][source:"+esNotice.getSource()+"]");
        //url已存在不入库
        int isExist = noticeCleanService.countSnastchUrlByUrl(esNotice);
        if (isExist == 1) {
            logger.info("#### 数据库中已存在相同url:" + esNotice.getUrl() + " ####");
            return false;
        }else{
            handleNotRepeat(esNotice);
            return true;
        }
    }

}
