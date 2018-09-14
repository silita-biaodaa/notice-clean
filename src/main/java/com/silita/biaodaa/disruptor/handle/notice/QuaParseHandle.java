package com.silita.biaodaa.disruptor.handle.notice;

import com.lmax.disruptor.EventHandler;
import com.silita.biaodaa.disruptor.event.AnalyzeEvent;
import com.silita.biaodaa.service.QuaParseService;
import com.snatch.model.EsNotice;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.ref.SoftReference;

/**
 * Created by dh on 2018/6/26.
 */
@Component
public class QuaParseHandle implements EventHandler<AnalyzeEvent> {
    private Logger logger = org.slf4j.LoggerFactory.getLogger(getClass());

    @Autowired
    QuaParseService quaParseService;

    @Override
    public void onEvent(AnalyzeEvent event, long sequence, boolean endOfBatch) throws Exception {
        SoftReference<EsNotice> noticeRef = new SoftReference(event.getEsNotice());
        EsNotice esNotice = noticeRef.get();
        try {
            if (esNotice != null) {
                logger.info("资质处理开始 #### [noticeId:"+esNotice.getUuid()+"][redisId:" + esNotice.getRedisId() + "][rank:" + esNotice.getRank() + "][source:" + esNotice.getSource() + "][ur:" + esNotice.getUrl() + "]" + esNotice.getTitle() + esNotice.getOpenDate());
                quaParseService.insertUrlCert(Integer.parseInt(esNotice.getUuid()), esNotice);
                logger.info("资质处理成功结束 ####  [noticeId:"+esNotice.getUuid()+"][redisId:" + esNotice.getRedisId() + "][rank:" + esNotice.getRank() + "][source:" + esNotice.getSource() + "][ur:" + esNotice.getUrl() + "]" + esNotice.getTitle() + esNotice.getOpenDate());
            }
        }catch (Exception e){
            logger.error("资质处理出错，"+e,e);
        }finally {
            esNotice=null;
        }
    }
}
