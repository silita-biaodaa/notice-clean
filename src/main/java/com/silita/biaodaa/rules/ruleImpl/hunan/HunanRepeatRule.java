package com.silita.biaodaa.rules.ruleImpl.hunan;

import com.silita.biaodaa.rules.Interface.RepeatRule;
import com.silita.biaodaa.service.INoticeCleanService;
import com.silita.biaodaa.utils.MyStringUtils;
import com.snatch.model.EsNotice;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.silita.biaodaa.utils.ComputeResemble.similarDegreeWrapper;
import static com.silita.biaodaa.utils.RuleUtils.getNumStr;

/**
 * Created by dh on 2018/3/14.
 */
@Component
public class HunanRepeatRule extends HunanBaseRule implements RepeatRule {
    private static Logger logger = Logger.getLogger(HunanRepeatRule.class);

    @Autowired
    FilterCompareKeys repeatFilter;

    @Autowired
    INoticeCleanService noticeCleanService;

    private String getKString(EsNotice n){
        return n.getTitle()+n.getUrl()+n.getType()+n.getPressContent()+n.getOpenDate()+n.getSource();
    }

    private void removeRepetitionSet(Set<EsNotice> matchSet){
        List<String> kList = new ArrayList<String>();
        Iterator iter = matchSet.iterator();
        while(iter.hasNext()){
            EsNotice no = (EsNotice) iter.next();
            if(kList.contains(getKString(no))){
                iter.remove();
            }else{
                kList.add(getKString(no));
            }
        }
    }

    private void formatContent(EsNotice n){
        String c = n.getContent();
        c = c.replaceAll("'","\'");
        c = c.replaceAll("‘","\'");
        c = c.replaceAll("’","\'");
        c = c.replaceAll("\"","\\\"");
        c = c.replaceAll("“","\\\"");
        c = c.replaceAll("”","\\\"");
        n.setContent(c);
    }

    @Override
    public boolean executeRule(EsNotice esNotice) {
        logger.info("湖南去重开始[redisId:"+esNotice.getRedisId()+"][source:"+esNotice.getSource()+"][url:"+esNotice.getUrl()+"][title:" + esNotice.getTitle() + "][openDate:"+esNotice.getOpenDate()+"]");
        boolean isNewNotice = false;
        String filterState="";
        List<EsNotice> matchTitleList=null;
        List<EsNotice> matchNoticeList =null;
        Set<EsNotice> matchSet = null;//根据标题等维度匹配，疑似公告总集合
        try {
            formatContent(esNotice);
            String title = esNotice.getTitle();
            String press = chineseCompressUtil.getPlainText(esNotice.getContent());
            esNotice.setPressContent(press);

            int isExist = noticeCleanService.countSnastchUrlByUrl(esNotice);
            //一、url重复判断（已存在url直接丢弃）
            if (isExist != 0) {
                logger.info("#### 数据库中已存在相同url:" + esNotice.getUrl() + "[title:"+title+"][type:"+esNotice.getType()+"] ####");
                return false;
            }

            //二、去重逻辑判断
            if(MyStringUtils.isNotNull(title) && title.length()<6){//不进行去重的,直接入库
                filterState=IS_NEW;//进公告表
            }else {
                //2.1：从db中匹配疑似公告标题
                //匹配公告集合：标题片段，地区，类型，公示时间前后3天
                Map argMap = buildTitleMatchParam(esNotice);
                logger.info("2.1.标题匹配队列条件[argMap:"+argMap+"]");
                matchTitleList = noticeRuleService.matchEsNoticeList(argMap);
                if (matchTitleList.size()>= 0) {
                    matchSet = new TreeSet<EsNotice>(matchTitleList);
                }else{
                    matchSet = new TreeSet<EsNotice>();
                }
                //匹配公告集合：地区，类型，公示时间前后3天,相似度大于80%
                argMap.remove("titleKey");
                logger.info("2.2.祛除标题，匹配队列条件[argMap:"+argMap+"]");
                matchNoticeList = noticeRuleService.matchEsNoticeList(argMap);
                for (EsNotice notice : matchNoticeList) {
                    if (similarDegreeWrapper(title, notice.getTitle()) > 0.8) {
                        matchSet.add(notice);
                    }
                }

                if(matchSet.size()<=0){
                    logger.info("2.3标题匹配队列为空。。条件[argMap:"+argMap+"]");
                    filterState=IS_NEW;//异常情况，不做去重
                }else {
                    removeRepetitionSet(matchSet);
                //3.执行去重逻辑，过滤内容等
//                    filterState = filterV15(esNotice,matchSet);//V1.5版本规则

                    filterState = repeatFilter.filterRule(esNotice,matchSet);//V1.6版本过滤规则
                }
            }

            if(filterState.equals(IS_NEW)){//不重复，公告直接入库，需关联
                logger.info("公告不重复，直接入库。[title:"+title+"][type:"+esNotice.getType()+"][ur:"+esNotice.getUrl()+"]");
                handleNotRepeat(esNotice);
                isNewNotice = true;
            }else if(filterState.equals(IS_UPDATED)) {//已更新公告内容，无需关联
                logger.info("公告已更新。[title:"+title+"][type:"+esNotice.getType()+"][ur:"+esNotice.getUrl()+"]");
                isNewNotice = false;
            }else if(filterState.equals(IS_REPEATED)){//已经被去重，无需关联
                isNewNotice = false;
            }else {
                logger.error("异常情况[filterState:"+filterState+"]，新公告入库。[title:"+title+"][type:"+esNotice.getType()+"][ur:"+esNotice.getUrl()+"][type:"+esNotice.getType()+"]");
                handleNotRepeat(esNotice);
//                noticeCleanService.insertSnatchurlRepetition(esNotice);
                isNewNotice = false;
            }
        }catch (Exception e){
            logger.error("###湖南去重异常[redisId:"+esNotice.getRedisId()+"][source:"+esNotice.getSource()+"][ur:"+esNotice.getUrl()+"][title:" + esNotice.getTitle() + "][openDate:"+esNotice.getOpenDate()+"]："+e,e);
        }finally {
            matchNoticeList = null;
            matchTitleList = null;
            matchSet=null;
            System.gc();
            logger.info("####湖南去重结束:[redisId:"+esNotice.getRedisId()+"][source:"+esNotice.getSource()+"][ur:"+esNotice.getUrl()+"][title:" + esNotice.getTitle() + "][openDate:"+esNotice.getOpenDate()+"]");
        }
        return isNewNotice;
    }

    /**
     * v15去重逻辑：过滤规则。
     * @param esNotice
     * @param matchSet
     * @return
     * @throws Exception
     */
    private String filterV15(EsNotice esNotice,Set<EsNotice> matchSet)throws Exception{
        String filterState = "";
        String title = esNotice.getTitle();

        Set<EsNotice> sameTitleList = new TreeSet<EsNotice>();//标题一致的列表
        Set<EsNotice> notSameTitleList = new TreeSet<EsNotice>();//标题不一样的列表
        for (EsNotice notice : matchSet) {
            if (title.equals(notice.getTitle())) {
                sameTitleList.add(notice);
            } else {
                notSameTitleList.add(notice);
            }
        }
        matchSet = null;

        String url = esNotice.getUrl();

        //标题相同判断
        if (sameTitleList.size() > 0) {
            Iterator iter = sameTitleList.iterator();
            while (iter.hasNext()) {
                EsNotice esnt = (EsNotice)iter.next();
                if (extractUrlHost(url).equals(extractUrlHost(esnt.getUrl()))) {//网站相同
                    //判断公告内容是否一致
                    String esntPress = chineseCompressUtil.getPlainText(esnt.getContent());
                    if (esntPress != null && esNotice.getPressContent() != null
                            && esntPress.equals(esNotice.getPressContent())) {
                        //保留新进来的公告,替换历史公告
                        filterState=IS_UPDATED;
                        replaceHistoryNotice(esnt, esNotice);
                    } else {
                        //公告入库
                        filterState=IS_NEW;
                    }
                } else {
                    //网站不一致,公告入库
                    filterState=IS_NEW;
                }
            }
        }

        //标题不相同，业务逻辑
        if (notSameTitleList.size() > 0) {
            //标段匹配判断
            if (title.indexOf("标段") != -1) {
                Iterator iter = notSameTitleList.iterator();
                while (iter.hasNext()) {
                    EsNotice esnt = (EsNotice) iter.next();
                    if (esnt.getTitle().indexOf("标段") != -1) {//标题存在标段，对比标段数
                        int blockIdx = title.lastIndexOf("标段");
                        int hisblockIdx = esnt.getTitle().lastIndexOf("标段");
                        if (blockIdx != -1 && hisblockIdx != -1) {
                            String blockStr = getNumStr(title.substring(0, blockIdx));
                            String hisblockStr = getNumStr(esnt.getTitle().substring(0, hisblockIdx));
                            if (!blockStr.equals(hisblockStr)) {
//                                            notSameTitleList.remove(esnt);
                                iter.remove();
                            }
                        }
                    }
                }
            }

            List<Map> filterList1 = noticeRuleService.queryRulesByType("repeat_filter1");
            List<Map> filterList2 = noticeRuleService.queryRulesByType("repeat_filter2");
            //根据关键字匹配新进公告标题：获取前序括号内容，
            List<String> titleSegments = filterSegment(filterList1, title, filterList2);
            if (titleSegments != null && titleSegments.size() > 0) {
                //新进公告标题的括号内容在历史公告中必须都存在才进行去重
                Iterator notSameIter = notSameTitleList.iterator();
                while (notSameIter.hasNext()) {
                    EsNotice esnt = (EsNotice) notSameIter.next();
                    List<String> matchTitleSegments = filterSegment(filterList1, esnt.getTitle(), filterList2);
                    if (matchTitleSegments != null && matchTitleSegments.size() > 0) {
                        for (String titleSeg : titleSegments) {
                            boolean tempMatch = false;
                            for (String mTitleSeg : matchTitleSegments) {
                                if (mTitleSeg.equals(titleSeg)) {
                                    tempMatch = true;
                                }
                            }
                            if (!tempMatch) {
                                notSameIter.remove();
                                break;
                            }
                        }
                    }
                }

                filterState =matchSetExecutor(esNotice,notSameTitleList);

            } else {//无有效括号内容的
                filterState = IS_NEW;
            }
        }else{
            filterState = IS_NEW;
        }
        return filterState;
    }



}
