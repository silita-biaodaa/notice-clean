package com.silita.biaodaa.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dh on 2018/7/2.
 */
public class HunanRelationRuleTest extends ConfigTest {
    @Autowired
    NoticeRelationService service;

    @Autowired
    QuaParseService quaParseService;

    @Test
    public void testCache(){
        for(int i=1;i<=3;i++){
            logger.debug("testqueryzh "+i);
            quaParseService.queryzh();
        }
    }

    @Test
    public void testBatchInsertRelation(){
        String thisId="1";
        List<String > nextIdList = new ArrayList<>();
        nextIdList.add("222");
        nextIdList.add("333");
        nextIdList.add("444");
        nextIdList.add("555");
        service.batchInsertRelation(thisId,nextIdList);
    }
}
