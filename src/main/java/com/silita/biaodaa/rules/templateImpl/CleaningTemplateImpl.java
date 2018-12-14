package com.silita.biaodaa.rules.templateImpl;

import com.silita.biaodaa.rules.Interface.CleaningTemplate;
import com.silita.biaodaa.rules.Interface.RelationRule;
import com.silita.biaodaa.rules.Interface.RepeatRule;
import com.silita.biaodaa.service.CleanService;
import com.silita.biaodaa.service.MessagePushService;
import com.snatch.model.EsNotice;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

/**
 * Created by dh on 2018/3/14.
 */
public class CleaningTemplateImpl implements CleaningTemplate {
    private static Logger logger = Logger.getLogger(CleaningTemplateImpl.class);

    private RepeatRule repeatRule;

    private RelationRule relationRule;

    @Autowired
    private MessagePushService messagePushService;


    @Value( "${relation_flag}" )
    private boolean relationFlag;

    @Autowired
    private CleanService cleanService;


    public CleaningTemplateImpl(RepeatRule repeatRule, RelationRule relationRule){
        this.repeatRule = repeatRule;
        this.relationRule = relationRule;
    }

    //执行公告清洗操作
    public void executeClean(EsNotice esNotice){
        esNotice.setTableName("mishu.snatchurl");
        long repStartTime = System.currentTimeMillis();
        //1.公告去重
        logger.info("#####去重规则开始[redisId:"+esNotice.getRedisId()+"][source:"+esNotice.getSource()+"][url:"+esNotice.getUrl()+"][title:" + esNotice.getTitle() + "][openDate:"+esNotice.getOpenDate()+"]");
        boolean repeatStatus = repeatRule.executeRule(esNotice);
        logger.info("#####去重执行完毕，[title:"+esNotice.getTitle()+"][redisId:"+esNotice.getRedisId()+"][repeatStatus:"+repeatStatus+"][url:"+esNotice.getUrl()+"][relationFlag:"+relationFlag+"]" +
                "\n去重消耗时间：" + (System.currentTimeMillis() - repStartTime) +" ms #####");
        //2.全国公告，解析内容直接插入到编辑表
        if(repeatStatus){
            logger.info("准备插入编辑信息storeAnalysisDetail:[id:"+esNotice.getUuid()+"][title:"+esNotice.getTitle()+"][TableName:"+esNotice.getTableName()+"]");
            cleanService.storeAnalysisDetail(esNotice);
        }

        // 无重复数据调用关联方法
        if(repeatStatus && relationFlag){
            String title = esNotice.getTitle();
            double startTotal = (Runtime.getRuntime().totalMemory()) / (1024.0 * 1024);
            logger.info(esNotice.getRedisId()+"####title:"+title+",开始执行关联。。。当前jvm内存用量："+startTotal+"MB");
            long startTime = System.currentTimeMillis();
            //3.公告关联
            Map<String,Object> relationMap = relationRule.executeRule(esNotice);
            double endTotal = (Runtime.getRuntime().totalMemory()) / (1024.0 * 1024);
            logger.info(esNotice.getRedisId()+"##### title:"+title+",关联结束，消耗时间："+( System.currentTimeMillis()-startTime)+"ms #####" +
                    "当前jvm内存用量："+endTotal+"MB,关联增加内存"+(endTotal-startTotal)+"MB");

//            //3.变更关联的公告发送信息
//            //TODO:后续收藏数据需要增加source路由字段
//            //仅支持湖南数据
//            if(esNotice.getSource().equals(Constant.HUNAN_SOURCE)) {
//                if (relationMap != null && relationMap.size() > 0) {
//                    messagePushService.queryCollectNotice(relationMap, esNotice.getUrl());
//                }
//            }
        }


        esNotice =null;
    }

}
