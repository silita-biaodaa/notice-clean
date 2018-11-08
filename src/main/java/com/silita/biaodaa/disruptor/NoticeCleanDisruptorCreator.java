package com.silita.biaodaa.disruptor;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.silita.biaodaa.disruptor.event.AnalyzeEvent;
import com.silita.biaodaa.disruptor.exception.AnalyzeException;
import com.silita.biaodaa.disruptor.handle.notice.CleaningHandle;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class NoticeCleanDisruptorCreator {

    private static Logger logger = org.slf4j.LoggerFactory.getLogger(NoticeCleanDisruptorCreator.class);

    private static Disruptor<AnalyzeEvent> processDisruptor;

    private static final EventFactory<AnalyzeEvent> EVENT_FACTORY = new EventFactory<AnalyzeEvent>() {
        @Override
        public AnalyzeEvent newInstance() {
            return new AnalyzeEvent();
        }
    };

    private static final int BUFFER_SIZE = 1024*4;

    //TODO 需要根据其他因素调整线程数量
    private static final int THREAD_NUM = 5;
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(THREAD_NUM);

    /**
     * 利用spring完成初始化，singleton
     */
    public static synchronized void initDisruptor(
            CleaningHandle cleaningHandle) {
        if (processDisruptor == null) {
            logger.info("..........NoticeCleanDisruptorCreator init..........\nDISRUPTOR BUFFER_SIZE:" + BUFFER_SIZE + " THREAD_NUM:" + THREAD_NUM);
            processDisruptor = new Disruptor<AnalyzeEvent>(EVENT_FACTORY, BUFFER_SIZE, EXECUTOR, ProducerType.SINGLE, new SleepingWaitStrategy());
            processDisruptor.handleExceptionsWith(new AnalyzeException());
            processDisruptor.handleEventsWith(cleaningHandle);
            logger.info("..........NoticeCleanDisruptorCreator init success..........");
        }
    }

    public static Disruptor<AnalyzeEvent> getProcessDisruptor() {
        return processDisruptor;
    }

    public static void shutdownDisruptor() {
        processDisruptor.shutdown();
        EXECUTOR.shutdown();
    }
}
