package com.silita.biaodaa.rules.ruleImpl.others;

import com.silita.biaodaa.rules.Interface.RelationRule;
import com.silita.biaodaa.rules.ruleImpl.hunan.HunanBaseRule;
import com.snatch.model.EsNotice;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OthersRelationRule extends HunanBaseRule implements RelationRule {
    Logger logger = Logger.getLogger(this.getClass());

    @Override
    public Map<String, Object> executeRule(EsNotice esNotice) {
        logger.info("##### 全国公告暂不进行关联！  #####");
        return null;
    }
}
