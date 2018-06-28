package com.silita.biaodaa.rules.ruleImpl.hunan;

import com.silita.biaodaa.rules.Interface.RelationRule;
import com.silita.biaodaa.utils.ComputeResemble;
import com.silita.biaodaa.utils.MyStringUtils;
import com.snatch.model.EsNotice;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by dh on 2018/3/14.
 */
@Component
public class HunanRelationRule extends HunanBaseRule implements RelationRule {
    Logger logger = Logger.getLogger(this.getClass());

    @Override
    public Map<String, String> executeRule(EsNotice esNotice) {
        logger.info("湖南关联开始：[redisId:"+esNotice.getRedisId()+"][source:"+esNotice.getSource()+"][ur:"+esNotice.getUrl()+"]" + esNotice.getTitle() + esNotice.getOpenDate());
        Map<String,String> map = new HashMap<String,String>();
        List<String> relationList = new ArrayList<String>(); // 关联列表
        int urlIndex = urlIndexOf(esNotice.getUrl());
        if (urlIndex != -1) {
            logger.info("#####  启用张家界、邵阳、湘西、湘潭、长沙 公告关联规则！  #####");
            // 张家界，邵阳，湘西，湘潭，长沙 公告关联规则
            String noticeUrl = esNotice.getUrl();
            String source = esNotice.getSource();
            String urlKey = "";
            if (urlIndex < 3) {
                // 张家界、邵阳、湘西
                urlKey = noticeUrl.substring(noticeUrl.indexOf("&tpid=") + 6);
                relationList = snatchNoticeHuNanDao.querysLikeUrl(urlKey,source);
            } else if (urlIndex == 3) {
                // 湘潭
                urlKey = noticeUrl.substring(0,noticeUrl.lastIndexOf("?") + 1);
                relationList = snatchNoticeHuNanDao.querysLikeUrl(urlKey,source);
            } else if (urlIndex == 4) {
                // 长沙
                String relationUlrs = "";
                if (esNotice.getDetail() != null && MyStringUtils.isNotNull(esNotice.getDetail().getRelationUrl())) {
                    relationUlrs = esNotice.getDetail().getRelationUrl();
                } else if (esNotice.getDetailZhongBiao() != null && MyStringUtils.isNotNull(esNotice.getDetailZhongBiao().getRelationUrl())) {
                    relationUlrs = esNotice.getDetailZhongBiao().getRelationUrl();
                }
                if (relationUlrs.split(",").length > 1) {
                    String[] relation_urls = relationUlrs.split(",");
                    relationList = snatchNoticeHuNanDao.querysLikeUrl(Arrays.asList(relation_urls),source);
                }
            }
        } else {
            // 通用关联规则
            logger.info("#####  启用通用关联规则！  #####");
            String title = esNotice.getTitle();
            logger.info("####  新进公告Title：" + title +"  ####");

            // 公告标题处理后进行模糊搜索
            String tempTitle = title.length() < 10 ? title : subSearchTitle(title);
            tempTitle = replaceStrSymbol(tempTitle); // 替换符号空格为%，标题前后添加%
//            logger.info("####  处理后的模糊匹配词：" + tempTitle + "  ####");
            String websiteUrl = MyStringUtils.parseWebSiteUrl(esNotice.getUrl());
            List<Map<String,Object>> searchResult = snatchNoticeHuNanDao.querySimilarityNotice(esNotice,websiteUrl,tempTitle);
            // 相似度搜索公告
            List<Map<String,Object>> result2 = snatchNoticeHuNanDao.querySimilarityNotice(esNotice,websiteUrl,null);

            // set去重
            Set<Map<String,Object>> set = new HashSet<Map<String,Object>>(searchResult);

            String title2 = clearKeyWord(title);
            for (Map<String,Object> mp : result2) { // 只保留与新进公告标题95%相似度以上的
                String hstyTitle = clearKeyWord(String.valueOf(mp.get("title")));
                if (ComputeResemble.similarDegreeWrapper(title2, hstyTitle) > 0.95) {
                    set.add(mp);
                }
            }

            searchResult = new ArrayList<Map<String,Object>>(set);

//            logger.info("#### 模糊匹配  .. resultSize：" + searchResult.size() + "  ####");

            // 数据过滤
            if (!searchResult.isEmpty() && searchResult.size() > 1) { // size为 1 ，无相关公告
                searchResult = noticeFilter(searchResult,esNotice);
            }
            for (Map<String,Object> m : searchResult) {
                relationList.add(String.valueOf(m.get("id")));
            }
        }
        logger.info("#####  关联条数：" + relationList.size() + "  #####");

        // 进行关联
        if (!relationList.isEmpty() && relationList.size() < 20 && relationList.size() > 1) {
            String thisId = snatchNoticeHuNanDao.queryThisId(esNotice.getUrl(),esNotice.getSource());
            List<String> nextIdList = new ArrayList<String>();
            if (MyStringUtils.isNotNull(thisId)) {
                for (String otherId : relationList) {
                    if (!otherId.equals(thisId)) {
                        nextIdList.addAll(snatchNoticeHuNanDao.queryRelationNextIds(otherId));
                        nextIdList.add(otherId);
                    }
                }
                // list去重
                Set<String> nextIdSet = new HashSet<String>(nextIdList);
                nextIdList = new ArrayList<>(nextIdSet);
                // 插入关联表
                if (nextIdList.size() > 5) {
                    map = snatchNoticeHuNanDao.batchInsertRelation(thisId,nextIdList);
                } else {
                    for (String nextId:nextIdList) {
                        snatchNoticeHuNanDao.insertSnatchRelation(thisId,nextId);
                        map.put("mainId",thisId);
                        map.put("nextId",nextId);
                    }
                }
            }
        }
        logger.info("#####  湖南关联结束！  #####[redisId:"+esNotice.getRedisId()+"][source:"+esNotice.getSource()+"][ur:"+esNotice.getUrl()+"]" + esNotice.getTitle() + esNotice.getOpenDate());
        return map;
    }
}
