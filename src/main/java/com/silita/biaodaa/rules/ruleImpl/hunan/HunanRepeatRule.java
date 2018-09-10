package com.silita.biaodaa.rules.ruleImpl.hunan;

import com.silita.biaodaa.common.Constant;
import com.silita.biaodaa.common.elastic.indexes.IdxZhaobiaoSnatch;
import com.silita.biaodaa.common.elastic.indexes.IdxZhongbiaoSnatch;
import com.silita.biaodaa.rules.Interface.RepeatRule;
import com.silita.biaodaa.service.INoticeCleanService;
import com.silita.biaodaa.service.NoticeRuleService;
import com.silita.biaodaa.utils.ComputeResemble;
import com.silita.biaodaa.utils.MyStringUtils;
import com.snatch.model.EsNotice;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.silita.biaodaa.utils.ComputeResemble.similarDegreeWrapper;
import static com.silita.biaodaa.utils.RuleUtils.getNumStr;

/**
 * Created by dh on 2018/3/14.
 */
@Component
public class HunanRepeatRule extends HunanBaseRule implements RepeatRule {
    private static Logger logger = Logger.getLogger(HunanRepeatRule.class);

    @Autowired
    INoticeCleanService noticeCleanService;

    @Autowired
    NoticeRuleService noticeRuleService;

    /**
     * 替换普通字符（非标点符号）
     * @param title
     * @param regex
     * @param targetStr
     * @return
     */
    private String replaceString(String title,String regex,String targetStr){
        Pattern ptn = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
        Matcher matcher = ptn.matcher(title);
        String tmp=null;
        while (matcher.find()) {
            tmp=  matcher.group();
            if(tmp!=null && !tmp.equals(title)) {
                title = title.replaceAll(tmp, targetStr);
            }
        }
        return title;
    }


    private List<String> matchStringByTag(String str,String regex){
        List<String> resList= new LinkedList<String>();
        Pattern ptn = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
        Matcher matcher = ptn.matcher(str);
        while (matcher.find()) {
            //排除匹配内容的标点符号与空格等
            String mStr = replacePunctuation(matcher.group(),"([.。，,.;；：:\\(\\)（）\\[\\]【】]-—？?！!~@#$&)","");
            resList.add(mStr);
        }
        return resList;
    }

    /**
     * 替换标点符号
     * @param str
     * @param regex
     * @param targetStr
     * @return
     */
    private String replacePunctuation(String str,String regex,String targetStr){
        if(str !=null && str.length()>0) {
            Pattern ptn = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            Matcher matcher = ptn.matcher(str);
            String tmp = null;
            while (matcher.find()) {
                tmp = "\\" + matcher.group();
                if (tmp != null && !tmp.equals(str)) {
                    str = str.replaceAll(tmp, targetStr);
                }
            }
        }
        return str;
    }

    private String extractUrlHost(String url)throws Exception {
        return new URI(url).getHost().toString();
    }

    /**
     * 历史公告删除
     * @param historyNotice
     * @return
     */
    private boolean delHistoryNotice( EsNotice historyNotice){
//        // 插入新进公告，历史公告isshow = 1
//        notice.setEdit(historyNotice.getEdit());
//        handleNotRepeat(notice);
//        noticeCleanService.updateIsShowById(historyNotice.getUuid(), 1, notice.getSource());
        noticeCleanService.insertSnatchurlRepetition(historyNotice);
        noticeCleanService.deleteSnatchUrl(historyNotice.getUuid(),historyNotice.getSource());

        if (historyNotice.getSource().equals(Constant.HUNAN_SOURCE)) {
            // 删除es上的历史公告索引
            if (historyNotice.getType() == 2) {
                // 删除中标公告索引
                snatchNoticeHuNanDao.deleteIndexById(IdxZhongbiaoSnatch.class, historyNotice.getUuid());
            } else {
                // 删除招标公告索引
                snatchNoticeHuNanDao.deleteIndexById(IdxZhaobiaoSnatch.class, historyNotice.getUuid());
            }
        }

        // 历史公告关联信息删除、编辑信息更改
//        delRelationInfoAndEditDetail(notice, historyNotice);
        logger.info("###  历史公告被去重 .. title：" + historyNotice.getTitle() + "  ###");
        return true;
    }

    /**
     * 新进公告替换历史公告
     * @param notice
     * @param historyNotice
     * @return
     */
    private boolean replaceHistoryNotice(EsNotice notice, EsNotice historyNotice){
        notice.setUuid(historyNotice.getUuid());
        notice.setEdit(historyNotice.getEdit());
        //清理页面缓存
        redisClear.clearRepeatNotice(historyNotice.getUuid());
        //更新基本表
        noticeCleanService.updateSnatchUrl(notice, historyNotice.getUuid());
        //更新内容
        noticeCleanService.updateSnatchurlContent(notice);
        noticeCleanService.updateSnatchpress(notice);
        //保留历史公告进入去重表
        noticeCleanService.insertSnatchurlRepetition(historyNotice);
        //仅湖南数据处理es
        if (notice.getSource().equals(Constant.HUNAN_SOURCE)) {
            if (notice.getType() == 2) { //中标
                try {
                    snatchNoticeHuNanDao.insertZhongbiaoEsNotice(notice);
                } catch (Exception e) {
                    logger.error("@@@@ES中标去重更新失败" + e);
                }
            } else {
                try {
                    snatchNoticeHuNanDao.updateZhaobiaoEsNotice(notice);
                } catch (Exception e) {
                    logger.error("@@@@ES招标去重更新失败" + e);
                }
            }
            // 历史公告关联信息删除、编辑信息更改
            delRelationInfoAndEditDetail(notice, historyNotice);
        }
        logger.info("###新公告替换历史公告 .. title: " + notice.getTitle() + "  历史公告 : " + historyNotice.getTitle() + "  ###");
        return true;
    }

    /**
         * 根据filterList关键字列表，获取关键字之前的括号内容(列表),
     * 返回列表中排除excludeList集合中的内容。
     * @param filterList
     * @param excludeList
     * @param title
     * @return
     */
    private List<String> filterSegment(List<Map> filterList,String title,List<Map> excludeList){
        List<String> resList = null;
        for(Map fMap :filterList){
            String fName = (String)fMap.get("name");
            int idxPre = title.indexOf(fName);
            if(idxPre>2){
                resList= matchStringByTag(title.substring(0,idxPre),"\\(.{0,8}\\)|\\（.{0,8}\\）");
                for(String paren:resList) {
                    for (Map exMap : excludeList) {
                        String exName = (String) fMap.get("name");
                        if (paren.indexOf(exName)!= -1){//命中排除字符，从结果集中去掉
                            resList.remove(paren);
                            break;
                        }
                    }
                }
            }
        }
        return resList;
    }

    /**
     * 构造匹配公告的（标题模糊匹配）查询条件
     * @param esNotice
     * @return
     * @throws Exception
     */
    private Map buildTitleMatchParam(EsNotice esNotice)throws Exception{
        String title = esNotice.getTitle();

        //根据关键字截取标题
        String titleKey = null;
        List<Map> keyList = noticeRuleService.queryRulesByType("repeat_keys");
        for (Map map : keyList) {
            String keyName = (String) map.get("name");
            int keyIdx = title.indexOf(keyName);
            if (keyIdx != -1) {
                if (keyIdx < 6) {
                    //获取标题的50%
                    if (title.length() / 2 > keyIdx) {
                        keyIdx = title.length() / 2;
                    }
                }
                titleKey = title.substring(0, keyIdx);
                break;
            }
        }

        //关键字无匹配时，标题直接截取一半
        if(titleKey==null || titleKey.trim().equals("")) {
            titleKey = title.substring(0,title.length()/2);
        }

        //构造标题查询条件：标题祛除标点以及关键字
        titleKey = replacePunctuation(titleKey, "([.。、`，,.;；：:\\(\\)（）\\[\\]【】]-—？?！!~@#$&)", "%");
        titleKey = replaceString(titleKey, "(^关于)|([ ])|(招标)|(中标)|(项目)|(施工)|(工程)", "%");
        while (titleKey.indexOf("%%") != -1) {
            titleKey = titleKey.replaceAll("%%", "%");
        }

        //公告关键字匹配公告集合：相同地区，相同类型，公示时间前后3天
        String source = esNotice.getSource();
        String city = esNotice.getCity();
        String province = esNotice.getProvince();
        Integer type = esNotice.getType();
        String openDate = esNotice.getOpenDate();
        String url = esNotice.getUrl();
        Map argMap = new HashMap();
        argMap.put("titleKey", "%" + titleKey + "%");
        argMap.put("source", source);
        argMap.put("province", province);
        argMap.put("city", city);
        argMap.put("type", type);
        argMap.put("openDate", openDate);
        argMap.put("url", "%" + extractUrlHost(url) + "%");
        if (title.indexOf("标段") == -1) {
            argMap.put("notLike", "%标段%");
        }

        return argMap;
    }

    /**
     * 公告内容过滤逻辑
     * @param esNotice
     * @param matchSets
     * @return
     */
    private String matchSetExecutor(EsNotice esNotice,Set<EsNotice> matchSets) {
        String filterState = "";
        String repeatExecute = "";
        int type = esNotice.getType();
        if (matchSets.size() > 0) {
            boolean isRepeat = false;
            Iterator notSameIter2 = matchSets.iterator();
            while (notSameIter2.hasNext()) {//按顺序
                EsNotice esnt = (EsNotice) notSameIter2.next();
                Integer detailId = null;
                if (esnt.getDetail() != null) {
                    detailId = esnt.getDetail().getId();
                }

                //过滤之后的匹配集合，进行相似度判断
                String esntPress = chineseCompressUtil.getPlainText(esnt.getContent());
                double computeNum = ComputeResemble.similarDegreeWrapper(esNotice.getPressContent(), esntPress);
                if (type == 2) {//中标公告20%
                    if (computeNum > 0.2) {
                        isRepeat = true;
                    }
                } else {//非中标公告85%
                    if (computeNum > 0.85) {
                        isRepeat = true;
                    }
                }
                if (isRepeat) {
                    if (esNotice.getRank() != 0 && esnt.getRank() == 0) {//去重（历史）省公告
                        //只替换有编辑内容的第一条编辑过的公告（已按ID排序）
                        if (detailId != null && detailId > 0 && !repeatExecute.equals("replace")) {
                            repeatExecute = "replace";
                            replaceHistoryNotice(esNotice, esnt);//新公告替换历史公告
                        } else {
                            delHistoryNotice(esNotice);//历史公告删除
                        }
                    }
                }
            }

            if (repeatExecute.equals("replace")) {//公告已做替换处理，流程结束
                filterState = "isUpdated";
            } else {
                filterState = "isNewNotice";
            }


        }else{
            filterState = "isNewNotice";
        }
        return filterState;
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
                        filterState="isUpdated";
                        replaceHistoryNotice(esnt, esNotice);
                    } else {
                        //公告入库
                        filterState="isNewNotice";
                    }
                } else {
                    //网站不一致,公告入库
                    filterState="isNewNotice";
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
                filterState = "isNewNotice";
            }
        }else{
            filterState = "isNewNotice";
        }
        return filterState;
    }

    @Override
    public boolean executeRule(EsNotice esNotice) {
        logger.info("湖南去重开始[redisId:"+esNotice.getRedisId()+"][source:"+esNotice.getSource()+"][ur:"+esNotice.getUrl()+"][title:" + esNotice.getTitle() + "][openDate:"+esNotice.getOpenDate()+"]");
        boolean isNewNotice = false;
        String filterState="";
        List<EsNotice> matchTitleList=null;
        List<EsNotice> matchNoticeList =null;
        Set<EsNotice> matchSet = null;//根据标题等维度匹配，疑似公告总集合
        try {
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
                filterState="isNewNotice";//进公告表
            }else {
                //2.1：从db中匹配疑似公告标题
                //匹配公告集合：标题片段，地区，类型，公示时间前后3天
                Map argMap = buildTitleMatchParam(esNotice);
                logger.info("2.1.标题匹配队列条件[argMap:"+argMap+"]");
                matchTitleList = noticeRuleService.matchEsNoticeList(argMap);
                if (matchTitleList.size()>= 0) {
                    matchSet = new HashSet<EsNotice>(matchTitleList);
                }else{
                    matchSet = new HashSet<EsNotice>();
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
                    filterState="isNewNotice";//异常情况，不做去重
                }else {
                    filterState = filterV15(esNotice,matchSet);
                }
            }

            if(filterState.equals("isNewNotice")){//不重复，公告直接入库，需关联
                logger.info("公告不重复，直接入库。[title:"+title+"][type:"+esNotice.getType()+"][ur:"+esNotice.getUrl()+"]");
                handleNotRepeat(esNotice);
                isNewNotice = true;
            }else if(filterState.equals("isUpdated")) {//已更新公告内容，无需关联
                logger.info("公告已更新。[title:"+title+"][type:"+esNotice.getType()+"][ur:"+esNotice.getUrl()+"]");
                isNewNotice = false;
            }else{//新公告被去重（进去重表），无需关联
                logger.info("公告被去重，进入去重表。[title:"+title+"][type:"+esNotice.getType()+"][ur:"+esNotice.getUrl()+"]");
                noticeCleanService.insertSnatchurlRepetition(esNotice);
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

}
