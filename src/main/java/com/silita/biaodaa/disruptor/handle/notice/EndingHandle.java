package com.silita.biaodaa.disruptor.handle.notice;

import com.lmax.disruptor.EventHandler;
import com.silita.biaodaa.disruptor.event.AnalyzeEvent;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * Created by dh on 2018/11/15.
 */
@Component
public class EndingHandle implements EventHandler<AnalyzeEvent> {
    private static Logger logger = Logger.getLogger(EndingHandle.class);

    @Override
    public void onEvent(AnalyzeEvent event, long sequence, boolean endOfBatch) throws Exception {
        event.setEsNotice(null);
        event=null;
        logger.debug("###EndingHandle###"+sequence);
    }
}
