package com.silita.biaodaa.disruptor;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventTranslatorOneArg;
import com.silita.biaodaa.disruptor.event.AnalyzeEvent;
import com.silita.biaodaa.disruptor.handle.notice.CleaningHandle;
import com.silita.biaodaa.disruptor.handle.notice.EndingHandle;
import com.silita.biaodaa.disruptor.handle.notice.QuaParseHandle;
import com.snatch.model.EsNotice;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
public class DisruptorOperator {

    Logger logger = org.slf4j.LoggerFactory.getLogger(getClass());

    @Autowired
    private NoticeCleanDisruptorCreator noticeCleanDisruptorCreator;

    @Autowired
    private OtherDisruptorCreator otherDisruptorCreator;

    @Autowired
    private CleaningHandle cleaningHandle;

    @Autowired
    private QuaParseHandle quaParseHandle;

    @Autowired
    private EndingHandle endingHandle;

    private static EventTranslatorOneArg<AnalyzeEvent,EsNotice> eventTranslator = new EventTranslatorOneArg<AnalyzeEvent,EsNotice>() {
        @Override
        public void translateTo(AnalyzeEvent event, long sequence, EsNotice esNotice) {
            event.setEsNotice(esNotice);
        }
    };

    /**
     * 初始化disruptor
     */
    @PostConstruct
    public void init() {
        noticeCleanDisruptorCreator.initDisruptor(cleaningHandle ,endingHandle);
        List<EventHandler> parallelHandleList = new ArrayList<EventHandler>();
        parallelHandleList.add(quaParseHandle);
        otherDisruptorCreator.initDisruptor(parallelHandleList,endingHandle);
    }

    public void publishNoticeClean(EsNotice esNotice) {
        NoticeCleanDisruptorCreator.getProcessDisruptor().publishEvent(eventTranslator,esNotice);
    }

    public void publishQuaParse(EsNotice esNotice) {
        OtherDisruptorCreator.getProcessDisruptor().publishEvent(eventTranslator,esNotice);
    }

    /**
     * 启动disruptor
     */
    public void start() {
        noticeCleanDisruptorCreator.getProcessDisruptor().start();
        logger.info("noticeCleanDisruptorCreator start success...");
        otherDisruptorCreator.getProcessDisruptor().start();
        logger.info("otherDisruptorCreator start success###");
    }

    /**
     * 关闭disruptor
     */
    public void shutdown() {
        noticeCleanDisruptorCreator.shutdownDisruptor();
        logger.info("noticeCleanDisruptorCreator shutdown");
        otherDisruptorCreator.shutdownDisruptor();
        logger.info("otherDisruptorCreator shutdown");
    }

}
