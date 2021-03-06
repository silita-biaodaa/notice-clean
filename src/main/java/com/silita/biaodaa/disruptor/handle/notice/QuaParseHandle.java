package com.silita.biaodaa.disruptor.handle.notice;

import com.lmax.disruptor.EventHandler;
import com.silita.biaodaa.disruptor.event.AnalyzeEvent;
import com.silita.biaodaa.service.QuaParseService;
import com.snatch.model.EsNotice;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.ref.SoftReference;

/**
 * Created by dh on 2018/6/26.
 */
@Component
public class QuaParseHandle implements EventHandler<AnalyzeEvent> {
    private Logger logger = Logger.getLogger(this.getClass());

    @Autowired
    QuaParseService quaParseService;

    @Override
    public void onEvent(AnalyzeEvent event, long sequence, boolean endOfBatch) throws Exception {
        SoftReference<EsNotice> noticeRef = new SoftReference(event.getEsNotice());
        EsNotice esNotice = noticeRef.get();
        try {
            if (esNotice != null) {
                long start = System.nanoTime();
                logger.info("全国资质处理开始 #### [noticeId:"+esNotice.getUuid()+"][redisId:" + esNotice.getRedisId() + "][rank:" + esNotice.getRank() + "][source:" + esNotice.getSource() + "][ur:" + esNotice.getUrl() + "]" + esNotice.getTitle() + esNotice.getOpenDate());
                quaParseService.insertUrlCertOpt(esNotice);
                long cost = (System.nanoTime()-start);
                logger.info("全国资质处理成功结束,耗时："+cost/1000000+"ms"+cost/1000+"微秒 ####  [noticeId:"+esNotice.getUuid()+"][redisId:" + esNotice.getRedisId() + "][rank:" + esNotice.getRank() + "][source:" + esNotice.getSource() + "][ur:" + esNotice.getUrl() + "]" + esNotice.getTitle() + esNotice.getOpenDate());
                quaParseService.updateEsInfo(esNotice);
                logger.info("招标公告es更新结束。");
            }
        }catch (Exception e){
            logger.error("全国公告资质处理出错，"+e,e);
        }finally {
            esNotice.setContent(null);
            esNotice.setPressContent(null);
            esNotice=null;
        }
    }
}
