package com.silita.biaodaa.disruptor.handle.notice;

import com.lmax.disruptor.EventHandler;
import com.silita.biaodaa.disruptor.event.AnalyzeEvent;
import com.silita.biaodaa.rules.Interface.CleaningTemplate;
import com.silita.biaodaa.rules.factory.CleaningFactory;
import com.silita.biaodaa.utils.MyStringUtils;
import com.snatch.model.EsNotice;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by dh on 2018/6/26.
 */
@Component
public class CleaningHandle implements EventHandler<AnalyzeEvent> {
    private Logger logger = org.slf4j.LoggerFactory.getLogger(getClass());

    @Autowired
    private CleaningFactory cleaningFactory;

    @Override
    public void onEvent(AnalyzeEvent event, long sequence, boolean endOfBatch) throws Exception {
        EsNotice esNotice = event.getEsNotice();
        long startTimeCount = System.currentTimeMillis();
        logger.info("SnatchScoreActor start #### " + esNotice.getRedisId() + "公告规则start, [rank:" + esNotice.getRank() + "][source:" + esNotice.getSource() + "][ur:" + esNotice.getUrl() + "]" + esNotice.getTitle() + esNotice.getOpenDate());
        if (esNotice.getRank() == null) {
            esNotice.setRank(0);
        }
        String source = esNotice.getSource();
        if (MyStringUtils.isNull(source)) {
            throw new Exception("接收的新公告source不能为空！请检查。");
        }
        CleaningTemplate cleaningTemplate = cleaningFactory.getClearnTemplate(source);
        cleaningTemplate.executeClean(esNotice);
        logger.info("SnatchScoreActor finished #### " + esNotice.getRedisId() + "公告规则finished, [rank:" + esNotice.getRank() + "][source:" + esNotice.getSource() + "][ur:" + esNotice.getUrl() + "]" + esNotice.getTitle() + esNotice.getOpenDate() + " 清洗规则总耗时：" + (System.currentTimeMillis() - startTimeCount) + "ms ####");
    }
}
