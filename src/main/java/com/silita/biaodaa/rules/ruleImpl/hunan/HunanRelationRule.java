package com.silita.biaodaa.rules.ruleImpl.hunan;

import com.silita.biaodaa.rules.Interface.RelationRule;
import com.silita.biaodaa.service.NoticeRelationService;
import com.silita.biaodaa.utils.ComputeResemble;
import com.silita.biaodaa.utils.MyStringUtils;
import com.silita.biaodaa.utils.RouteUtils;
import com.silita.biaodaa.utils.RuleUtils;
import com.snatch.model.EsNotice;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.silita.biaodaa.utils.RuleUtils.*;

@Component
public class HunanRelationRule extends HunanBaseRule implements RelationRule {
    private Logger logger = Logger.getLogger(this.getClass());

    @Autowired
    private NoticeRelationService service;

    /**
     * 判断公告是否为张家界、长沙、邵阳、湘西、湘潭
     * @param noticeUrl
     * @return
     */
    public int especWebSite (String noticeUrl) {
        for (int i = 0; i < normalUrl.length; i++) {
            if (noticeUrl.contains(normalUrl[i])) {
                return i;
            }
        }
        return -1;
    }

    private List<String>  specialSiteRel(int especIdx,EsNotice esNotice,Map argMap){
        List<String> relationList  = new ArrayList<String>();
        String urlKey = "";
        String noticeUrl = esNotice.getUrl();
        if (especIdx < 3) {
            // 张家界、邵阳、湘西
            urlKey = noticeUrl.substring(noticeUrl.indexOf("&tpid=") + 6);
            argMap.put("url","%"+urlKey+"%");
            relationList = service.querysLikeUrl(argMap);
        } else if (especIdx == 3) {
            // 湘潭
            urlKey = noticeUrl.substring(0,noticeUrl.lastIndexOf("?") + 1);
            argMap.put("url","%"+urlKey+"%");
            relationList = service.querysLikeUrl(argMap);
        } else if (especIdx == 4) {
            // 长沙
            String relationUlrs = "";
            if (esNotice.getDetail() != null && MyStringUtils.isNotNull(esNotice.getDetail().getRelationUrl())) {
                relationUlrs = esNotice.getDetail().getRelationUrl();
            } else if (esNotice.getDetailZhongBiao() != null && MyStringUtils.isNotNull(esNotice.getDetailZhongBiao().getRelationUrl())) {
                relationUlrs = esNotice.getDetailZhongBiao().getRelationUrl();
            }
            if (relationUlrs.split(",").length > 1) {
                String[] relation_urls = relationUlrs.split(",");
                argMap.put("urls",Arrays.asList(relation_urls));
                relationList = service.querysLikeUrl(argMap);
            }
        }
        return relationList;
    }

    /**
     * 通用关联规则
     * @param esNotice
     * @param argMap
     * @return
     */
    private List<String>  normalRule(EsNotice esNotice,Map argMap){
        List<String> relationList  = new ArrayList<String>();
        logger.info("#####  启用通用关联规则！  #####");
        String title = esNotice.getTitle();
        logger.info("####  新进公告Title：" + title +"  ####");

        // 公告标题处理后进行模糊搜索
        String tempTitle = title.length() < 10 ? title : subSearchTitle(title);
        tempTitle = replaceStrSymbol(tempTitle); // 替换符号空格为%，标题前后添加%
//            logger.info("####  处理后的模糊匹配词：" + tempTitle + "  ####");
        String websiteUrl = MyStringUtils.parseWebSiteUrl(esNotice.getUrl());
        argMap.put("openDate",esNotice.getOpenDate());
        argMap.put("websiteUrl",websiteUrl+"%");
        argMap.put("tempTitle",tempTitle);
        List<Map<String,Object>> searchResult = service.querySimilarityNotice(argMap);

        Set<Map<String,Object>> set = new HashSet<Map<String,Object>>(searchResult);

        String title2 = clearKeyWord(title);
        // 增加相似度匹配的公告
        argMap.put("tempTitle",null);
        List<Map<String,Object>> result2 = service.querySimilarityNotice(argMap);
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
        return relationList;
    }

    @Override
    public Map<String, Object> executeRule(EsNotice esNotice) {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            logger.info("湖南关联开始：[redisId:" + esNotice.getRedisId() + "][source:" + esNotice.getSource() + "][ur:" + esNotice.getUrl() + "]" + esNotice.getTitle() + esNotice.getOpenDate());
            String source = esNotice.getSource();
            String snatchTabName = RouteUtils.routeTableName("mishu.snatchurl", source);
            String noticeUrl = esNotice.getUrl();
            Map argMap = new HashMap<>();
            argMap.put("snatchTabName", snatchTabName);
            argMap.put("source", source);
            argMap.put("noticeUrl", noticeUrl);

            List<String> relationList = null; // 关联列表
            int especIdx = especWebSite(noticeUrl);
            if (especIdx != -1) {
                logger.info("#####  启用张家界、邵阳、湘西、湘潭、长沙 公告关联规则！  #####");
                relationList = specialSiteRel(especIdx, esNotice, argMap);
            } else {
                // 通用关联规则
                relationList = normalRule(esNotice, argMap);
            }
            logger.info("#####  关联条数：" + relationList.size() + "  #####");

            // 进行关联
            if (!relationList.isEmpty() && relationList.size() > 1 && relationList.size() < 20) {
                String thisId = service.queryThisId(argMap);
                List<String> nextIdList = new ArrayList<String>();
                if (MyStringUtils.isNotNull(thisId)) {
                    for (String otherId : relationList) {
                        if (!otherId.equals(thisId)) {
                            nextIdList.addAll(service.queryRelationNextIds(otherId));
                            nextIdList.add(otherId);
                        }
                    }
                    // list去重
                    Set<String> nextIdSet = new HashSet<String>(nextIdList);
                    nextIdList = new ArrayList<>(nextIdSet);
                    // 插入关联表
                    if (nextIdList.size() > 0) {
                        service.batchInsertRelation(thisId, nextIdList);
                        map.put("mainId", thisId);
                        map.put("nextId", nextIdList.get(nextIdList.size() - 1));//TODO:此处原逻辑不合理，只取了最后一个id,消息推送时会漏消息
                    }
                }
            }
        }catch (Exception e){
            logger.error(e,e);
        }finally {
            logger.info("#####  湖南关联结束！  #####[redisId:"+esNotice.getRedisId()+"][source:"+esNotice.getSource()+"][ur:"+esNotice.getUrl()+"]" + esNotice.getTitle() + esNotice.getOpenDate());
            return map;
        }

    }

    /**
     * 公告过滤
     * @param searchResult
     * @param notice
     * @return
     */
    public List<Map<String,Object>> noticeFilter (List<Map<String,Object>> searchResult,EsNotice notice) {
        // 数据过滤
        logger.info("####  数据过滤 .. resultSize：" + searchResult.size() + "  ####");
        String title = notice.getTitle();

        // 标段过滤
        if (searchResult.size() > 1) {
            Iterator<Map<String,Object>> it = searchResult.iterator();
            while (it.hasNext()){
                String resultTitle = String.valueOf(it.next().get("title"));
                if (title.contains("标段")) {
                    if (!resultTitle.contains("标段")) {
                        it.remove();
                    }else {
                        // 取出两个标题中的数字或英文
                        String titleNumStr = null;
                        if (title.lastIndexOf("标段") != -1) {
                            titleNumStr = getNumStr(title.substring(0, title.lastIndexOf("标段")));
                        } else {
                            titleNumStr = getNumStr(title);
                        }
                        String resultTitleNumStr = null;
                        if (resultTitle.lastIndexOf("标段") != -1) {
                            resultTitleNumStr = getNumStr(resultTitle.substring(0, resultTitle.lastIndexOf("标段")));
                        } else {
                            resultTitleNumStr = getNumStr(resultTitle);
                        }
                        if (!titleNumStr.equals(resultTitleNumStr)) {
                            // 俩个标题的数字或英文不一致
                            it.remove();
                        }
                    }
                } else if (resultTitle.contains("标段")) {
                    // 新进公告没有标段，相关公告有标段
                    it.remove();
                }
                logger.info("####  标段过滤 ..  resultSize：" + searchResult.size() + "  ####");
            }
        }

        // 公告类型过滤
        if (searchResult.size() > 1) {
            Iterator<Map<String,Object>> it = searchResult.iterator();
            while (it.hasNext()) {
                String resultTitle = String.valueOf(it.next().get("title"));
                if (RuleUtils.keyWords3IndexOf(resultTitle,keyWords3) != RuleUtils.keyWords3IndexOf(title,keyWords3)) {
                    it.remove();
                }
            }
            logger.info("####  公告类型过滤 ..  resultSize：" + searchResult.size() + "  ####");
        }

        // 项目次数过滤
        if (searchResult.size() > 1) {
            Iterator<Map<String,Object>> it = searchResult.iterator();
            String regex = "(第).{1}?(次|批|包)";
            Pattern pa = Pattern.compile(regex);
            while (it.hasNext()) {
                String resultTitle = String.valueOf(it.next().get("title"));
                Matcher ma = pa.matcher(title);
                if (ma.find()) {
                    String sabi = ma.group();
                    ma = pa.matcher(resultTitle);
                    if (ma.find()) {
                        // 新公告与历史公告都存在第几次字段
                        int titleRegIndex = title.indexOf(sabi);
                        int titleKeyIndex = keyWordsIndex(title, keyWords4);
                        int historyTitleRegIndex = resultTitle.indexOf(sabi);
                        int histotyTitleKeyIndex = keyWordsIndex(resultTitle, keyWords4);
                        if (titleKeyIndex == -1 || titleKeyIndex > titleRegIndex) {
                            // 新进公告没有关键字或关键字在相关字段前
                            if (histotyTitleKeyIndex == -1 || histotyTitleKeyIndex > historyTitleRegIndex) {
                            } else {it.remove();}
                        } else {
                            if (histotyTitleKeyIndex == -1 || histotyTitleKeyIndex > historyTitleRegIndex) {
                                it.remove();
                            }
                        }
                    } else {
                        // 新公告存在第几次字段，历史公告无第几次字段,过滤掉
                        it.remove();
                    }
                } else {
                    // 新进公告无第几次字段，历史公告存在第几次字段，过滤掉
                    ma = pa.matcher(resultTitle);
                    if (ma.find()) {
                        it.remove();
                    }
                }
            }
            logger.info("####  项目次数过滤 ..  historyNotices：" + searchResult.size() + "  ####");
        }

        // 括号内容过滤
        if (searchResult.size() > 1) {
            if (contaninsBracket(title)) {
                int keyIndex = keyWordsIndex(title,keyWords5); // 获取第一个关键字的位置
                if (keyIndex != -1) {
                    String tempTitle = title.substring(0,keyIndex);
                    if (contaninsBracket(tempTitle)) {
                        Iterator<Map<String,Object>> it = searchResult.iterator();
                        while (it.hasNext()) {
                            String resultTitle = String.valueOf(it.next().get("title"));
                            if (!compareBracketStr(tempTitle,resultTitle,keyWords6)) {
                                it.remove();
                            }
                        }
                    }
                }
            }
            logger.info("####  括号内容过滤 .. resultSize: " + searchResult.size() + "  ####");
        }
        return searchResult;
    }
}
