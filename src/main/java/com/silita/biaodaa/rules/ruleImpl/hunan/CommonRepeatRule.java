package com.silita.biaodaa.rules.ruleImpl.hunan;

import com.silita.biaodaa.rules.Interface.RepeatRule;
import com.silita.biaodaa.rules.exception.MyRetryException;
import com.silita.biaodaa.service.impl.NoticeCleanService;
import com.silita.biaodaa.utils.MyStringUtils;
import com.snatch.model.EsNotice;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.silita.biaodaa.utils.ComputeResemble.similarDegreeWrapper;
import static com.silita.biaodaa.utils.RuleUtils.getNumStr;

/**
 * 通用去重规则
 * Created by dh on 2018/3/14.
 */
@Component
public class CommonRepeatRule extends HunanBaseRule implements RepeatRule {
    private static Logger logger = Logger.getLogger(CommonRepeatRule.class);

    @Autowired
    FilterCompareKeys repeatFilter;

    @Autowired
    NoticeCleanService noticeCleanService;

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
        c=null;
        System.gc();
    }

    /**
     * 根据匹配条件从db中获取匹配队列
     * @param esNotice
     * @param argMap
     * @return
     */
    public  Set<EsNotice> matchNoticeSet(EsNotice esNotice,Map argMap) {
        Set<EsNotice> matchSet = null;
        List<EsNotice> matchTitleList=null;
        List<EsNotice> matchNoticeList =null;
        matchTitleList = noticeRuleService.matchEsNoticeList(argMap);
        logger.debug("matchTitleList:"+matchTitleList.size());
        if (matchTitleList != null && matchTitleList.size() >0) {
            matchSet = new TreeSet<EsNotice>(matchTitleList);
        } else {
            matchSet = new TreeSet<EsNotice>();
        }
        //匹配公告集合：地区，类型，公示时间前后3天,相似度大于80%
        Map nonTitleKey = new HashMap(argMap);
        nonTitleKey.remove("titleKey");
        logger.info("$$祛除标题，匹配队列条件[argMap:" + nonTitleKey + "]");
        matchNoticeList = noticeRuleService.matchEsNoticeList(nonTitleKey);
        logger.debug("matchNoticeList:"+matchNoticeList.size());
        for (EsNotice notice : matchNoticeList) {
            if (similarDegreeWrapper(esNotice.getTitle(), notice.getTitle()) > 0.8) {
                matchSet.add(notice);
            }
        }
        nonTitleKey = null;
        matchNoticeList = null;
        matchTitleList = null;
        if (matchSet.size() > 0) {
            removeRepetitionSet(matchSet);
        }
        return matchSet;
    }

    @Override
    public boolean executeRule(EsNotice esNotice) {
        boolean isNewNotice = false;
        String filterState="";
        Set<EsNotice> matchSet = null;//根据标题等维度匹配，疑似公告总集合
        try {
            formatContent(esNotice);
            String title = esNotice.getTitle();
            String press = chineseCompressUtil.getPlainText(esNotice.getContent());
            esNotice.setPressContent(press);

            //url重复判断（已存在url直接丢弃）
            int isExist = noticeCleanService.countSnastchUrlByUrl(esNotice);
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

                //多节点并发操作同一条historyNotice，使用乐观锁处理
                int retryCount =3;//重试次数
                boolean isRetry = false;
                do{
                    //重复匹配时，url重复判断（已存在url直接丢弃）
                    if(isRetry) {
                        int isExist2 = noticeCleanService.countSnastchUrlByUrl(esNotice);
                        if (isExist2 != 0) {
                            logger.info("#### 数据库中已存在相同url:" + esNotice.getUrl() + "[title:" + title + "][type:" + esNotice.getType() + "] ####");
                            return false;
                        }
                    }

                    try {
                        matchSet = matchNoticeSet(esNotice, argMap);//匹配队列
                        if (matchSet.size() <= 0) {
                            logger.info("2.3[redis:"+esNotice.getRedisId()+"][title:"+esNotice.getTitle()+"]标题匹配队列为空。。条件[argMap:" + argMap + "]");
                            filterState = IS_NEW;//异常情况，不做去重
                        } else {
                            removeRepetitionSet(matchSet);
                            logger.debug("匹配队列去重完毕。[matchSet:"+matchSet.size()+"]");
                            //3.执行去重逻辑，过滤内容等
    //                    filterState = filterV15(esNotice,matchSet);//V1.5版本规则
                            long t = System.currentTimeMillis();
                            logger.info("@@@开始执行过滤规则[redis:"+esNotice.getRedisId()+"][title:"+esNotice.getTitle()+"]。。。");
                            filterState = repeatFilter.filterRule(esNotice, matchSet);//V1.7版本过滤规则
                            logger.info("@@@过滤规则执行完毕[filterState:"+filterState+"][redis:"+esNotice.getRedisId()+"][title:"+esNotice.getTitle()+"]。。。"+(System.currentTimeMillis()-t)+"ms");
                        }
                        isRetry = false;
                    } catch (MyRetryException rt) {
                        retryCount--;
                        isRetry = true;
                        logger.warn("去重匹配数据脏读，准备重试...剩余重试次数："+retryCount+"||"+rt,rt);
                    }
                }while (isRetry && retryCount>0);
            }

            logger.info("去重判定结果[title:"+esNotice.getTitle()+"][filterState:"+filterState+"]");
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
                logger.error("异常情况[title:"+esNotice.getTitle()+"][filterState:"+filterState+"]，新公告入库。[type:"+esNotice.getType()+"][ur:"+esNotice.getUrl()+"][type:"+esNotice.getType()+"]");
                handleNotRepeat(esNotice);
                isNewNotice = true;
            }
        }catch (Exception e){
            logger.error("###去重规则异常[redisId:"+esNotice.getRedisId()+"][source:"+esNotice.getSource()+"][ur:"+esNotice.getUrl()+"][title:" + esNotice.getTitle() + "][openDate:"+esNotice.getOpenDate()+"]："+e,e);
        }finally {
            matchSet=null;
            System.gc();
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
                        noticeCleanService.replaceHistoryNotice(esnt, esNotice);
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
