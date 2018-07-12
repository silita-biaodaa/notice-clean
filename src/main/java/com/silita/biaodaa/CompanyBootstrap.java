package com.silita.biaodaa;

import com.silita.biaodaa.initReceive.MyKafkaConsumer;
import com.silita.biaodaa.service.QuaParseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * Created by zhangxiahui on 18/3/13.
 */
@Component
@Scope("singleton")
public class CompanyBootstrap implements ApplicationListener<ApplicationEvent> {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    MyKafkaConsumer myKafkaConsumer;

    @Autowired
    QuaParseService quaParseService;

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ContextRefreshedEvent) {
            boolean isRoot = ((ContextRefreshedEvent) event).getApplicationContext().getParent() == null;
            if (isRoot) {
                try {
                    myKafkaConsumer.init();
//                    final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(9);
                    //scheduler.scheduleAtFixedRate(testTask, 0, 1, TimeUnit.SECONDS);
                    logger.info("===========任务启动完成=========");
                } catch (Exception e) {
                    logger.info("任务启动异常", e);
                }
            }
        }
    }
}
