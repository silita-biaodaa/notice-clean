package com.silita.biaodaa.rules.ruleImpl.others;

import com.silita.biaodaa.rules.Interface.RelationRule;
import com.silita.biaodaa.rules.ruleImpl.hunan.HunanBaseRule;
import com.snatch.model.EsNotice;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OthersRelationRule extends HunanBaseRule implements RelationRule {
    Logger logger = Logger.getLogger(this.getClass());

    @Override
    public Map<String, String> executeRule(EsNotice esNotice) {
        logger.info("##### 全国公告暂不进行关联！  #####");
        return null;
//        // 通用关联规则
//        List<String> relationList = new ArrayList<String>(); // 关联列表
//        Map<String,String> map = new HashMap<String,String>();
//        String title = esNotice.getTitle();
//        logger.info("####  新进公告Title：" + title +"  ####");
//
//        // 公告标题处理后进行模糊搜索
//        String tempTitle = title.length() < 10 ? title : subSearchTitle(title);
//        tempTitle =clearKeyWord(tempTitle);
//        tempTitle = replaceStrSymbol(tempTitle); // 替换符号空格为%，标题前后添加%
//        List<Map<String,Object>> searchResult = snatchNoticeHuNanDao.querySimilarityNotice(esNotice,tempTitle);
//
//        // set去重
//        Set<Map<String,Object>> set = new HashSet<Map<String,Object>>(searchResult);
//        searchResult = new ArrayList<Map<String,Object>>(set);
//
//        // 数据过滤
//        if (!searchResult.isEmpty() && searchResult.size() > 1) { // size为 1 ，无相关公告
//            searchResult = noticeFilter(searchResult,esNotice);
//        }
//        for (Map<String,Object> m : searchResult) {
//            relationList.add(String.valueOf(m.get("id")));
//        }
//        logger.debug("#####  全国公告关联条数：" + relationList.size() + "  #####");
//
//        // 进行关联
//        if (!relationList.isEmpty() && relationList.size() < 20 && relationList.size() > 1) {
//            String thisId = snatchNoticeHuNanDao.queryThisId(esNotice.getUrl(),esNotice.getSource());
//            List<String> nextIdList = new ArrayList<String>();
//            if (MyStringUtils.isNotNull(thisId)) {
//                for (String otherId : relationList) {
//                    if (!otherId.equals(thisId)) {
//                        nextIdList.addAll(snatchNoticeHuNanDao.queryRelationNextIds(otherId));
//                        nextIdList.add(otherId);
//                    }
//                }
//                // list去重
//                Set<String> nextIdSet = new HashSet<String>(nextIdList);
//                nextIdList = new ArrayList<>(nextIdSet);
//                // 插入关联表
//                if (nextIdList.size() > 5) {
//                    map = snatchNoticeHuNanDao.batchInsertRelation(thisId,nextIdList);
//                } else {
//                    for (String nextId:nextIdList) {
//                        snatchNoticeHuNanDao.insertSnatchRelation(thisId,nextId);
//                        map.put("mainId",thisId);
//                        map.put("nextId",nextId);
//                    }
//                }
//            }
//        }
//        logger.debug("#####  自动关联结束！  #####");
//        return map;
    }
}
