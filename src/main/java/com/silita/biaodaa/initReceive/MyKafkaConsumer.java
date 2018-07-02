package com.silita.biaodaa.initReceive;

import com.silita.biaodaa.disruptor.DisruptorOperator;
import com.silita.biaodaa.utils.BeanUtils;
import com.silita.biaodaa.utils.LoggerUtils;
import com.snatch.model.EsNotice;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@Component
public class MyKafkaConsumer {
    private static Logger logger = Logger.getLogger(MyKafkaConsumer.class);

    @Autowired
    private DisruptorOperator disruptorOperator;

    private static Properties properties = null;

    private static String topic =  null;

    public MyKafkaConsumer(){
        super();
    }

    @PostConstruct
    public void init(){
        disruptorOperator.start();

        ConsumerConnector consumer  =null;
        Map<String, List<KafkaStream<byte[], byte[]>>>  messageStreams = null;
        KafkaStream<byte[], byte[]> stream =null;
        ConsumerIterator<byte[], byte[]> iterator =null;
//        stream.reversed();
        if(properties==null) {
            loadProperties();
        }
        int retry=0;
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(topic, 1);
        while(consumer==null || retry< 50) {
            try {
                consumer = createConsumer();//初始化kafka消费者
                messageStreams = consumer.createMessageStreams(topicCountMap);
                stream = messageStreams.get(topic).get(0);
                iterator =  stream.iterator();
                execute(consumer, iterator);
            } catch (Exception e) {
                logger.error(e, e);
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e1) {
                    logger.error(e1, e1);
                }
            }finally {
                retry++;
                iterator = null;
                if(stream!=null) {
                    stream.clear();
                    stream=null;
                }
                if(messageStreams!=null) {
                    messageStreams.clear();
                    messageStreams=null;
                }
                consumer.shutdown();
                consumer=null;
            }
        }
    }

    private static void loadProperties(){
        properties = new Properties();
        InputStream in = null;
        try {
            in =MyKafkaConsumer.class.getClassLoader().getResource("/config/kafka/kafka-consumer.properties").openStream();
            properties.load(in);
            topic = properties.getProperty("topic");
            logger.info("init consumer topic:" + topic);
        } catch (IOException e) {
            logger.error(e,e);
        }finally {
            try {
                if(in!= null) in.close();
            } catch (IOException e) {
                logger.error(e,e);
            }
            in =null;
        }
    }

    public void execute(ConsumerConnector consumer, ConsumerIterator<byte[], byte[]> iterator) {
//        iterator.reversed();
        while(iterator.hasNext()){
            Object msg =  BeanUtils.BytesToObject(iterator.next().message());
            if(msg instanceof Map) {
                Map vo = (Map) msg;
                EsNotice notice = (EsNotice) vo.get("model");
                LoggerUtils.showJVM("接收到kafka消息:[start:"+vo.get("start")+"][redisid:"+notice.getRedisId()+"][source:"+notice.getSource()+"][ur:"+notice.getUrl()+"]" + notice.getTitle() + notice.getOpenDate());
                try {
                    disruptorOperator.publishNoticeClean(notice);
                }catch (Exception e){
                    logger.error(e.getMessage(),e);
                }
            }else{
                logger.debug("接收到其他类型akka消息，跳过.msg className:"+msg.getClass());
            }
            consumer.commitOffsets();
        }
    }



    private ConsumerConnector createConsumer() {
        return Consumer.createJavaConsumerConnector(new ConsumerConfig(properties));
    }


    public static void main(String[] args) {
        new MyKafkaConsumer().init();// 使用kafka集群中创建好的主题
    }

}