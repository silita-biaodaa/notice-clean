package com.silita.biaodaa.rules.ruleImpl.hunan;

import com.silita.biaodaa.rules.Interface.RepeatFilter;
import com.silita.biaodaa.rules.exception.MyRetryException;
import com.silita.biaodaa.utils.MyStringUtils;
import com.snatch.model.EsNotice;
import org.apache.commons.collections.list.TreeList;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.silita.biaodaa.rules.Interface.RepeatRule.IS_NEW;

/**
 * Created by dh on 2018/9/10.
 */
@Component
public class FilterCompareKeys extends HunanBaseRule implements RepeatFilter {

    private static Logger logger = Logger.getLogger(FilterCompareKeys.class);

    @Override
    public String filterRule(EsNotice esNotice, Set<EsNotice> matchSet) throws Exception {
        String filterState = "";
        try {
            //1.获取新进公告的关键信息：时间数据序列，数据序列
            List<String> keysList = extractKeysList(esNotice.getPressContent());
            if(keysList==null || keysList.isEmpty()){
                filterState=IS_NEW;
            }
            //2.对比疑似队列,获取匹配队列
            Set<EsNotice> matchNotices = new TreeSet<EsNotice>();
            for(EsNotice compareNotice: matchSet) {
                List<String> tmpKeysList = extractKeysList(compareNotice.getPressContent());
                if(keysList.isEmpty()){
                    filterState=IS_NEW;
                }else{
                    if(tmpKeysList==null || tmpKeysList.isEmpty()){
                        logger.warn("extractKeysList is null.[esNotice.title:"+esNotice.getTitle()+"][esNotice.url:"+esNotice.getUrl()+"][title:"+compareNotice.getTitle()+"][url:"+compareNotice.getUrl()+"][pressContent:"+compareNotice+"]");
                        continue;
                    }else{
                        if(compareKeysList(keysList,tmpKeysList)){
                            logger.info("[new title:"+esNotice.getTitle()+"][keysList:"+keysList.toArray()+"][esNotice.getPressContent():"+esNotice.getPressContent()+"]\n" +
                                    "[match title:"+compareNotice.getTitle()+"][tmpKeysList:"+tmpKeysList.toArray()+"][compareNotice.getPressContent():"+compareNotice.getPressContent()+"]");
                            matchNotices.add(compareNotice);
                        }
                    }
                }
            }

            //3.对匹配队列，执行去重落地处理
            filterState = matchSetExecutor(esNotice,matchNotices);
            return filterState;
        }catch(MyRetryException rte){
            throw rte;
        }catch (Exception e) {
            filterState= "error";
            logger.error(e,e);
            return filterState;
        }
    }

    /**
     * 比较关键字序列.
     * @param c1
     * @param c2
     * @return true:完全相同；false:序列不完全相同
     */
    private boolean compareKeysList(List<String> c1,List<String> c2){
        int c1_len = c1.size();
        int c2_len = c2.size();
        logger.debug("compareKeysList:[c1_len:"+c1_len+"][c2_len:"+c2_len+"]");
        if(c1_len!=c2_len){
            return false;
        }
        int sameCount = 0;
        for(int i=0;i<c1.size();i++){
            if(c1.get(i).equals(c2.get(i))){
                sameCount++;
                continue;
            }
        }
        logger.debug("compareKeysList:[sameCount:"+sameCount+"][c1_len:"+c1_len+"]");
        return sameCount==c1_len;
    }

    /**
     * 提取公告内容中的关键信息（供对比）
     * @param content
     * @return
     * @throws Exception
     */
    private static List<String> extractKeysList(String content)throws Exception{
        List<String> keysList = null;
        String moneyReg = "([1-9][\\d]{0,10}|0)(\\.[\\d]{1,6})?([元]|[万元 \\n])";
        String dateTimeReg = "([1-9]\\d{1,3}[-年]+(0[1-9]|1[0-2]|[1-9])[-月]+(0[1-9]|[1-2][0-9]|3[0-1])[日]?)|((20|21|22|23|[0]?[0-1]\\d)[:：]+[0-5]\\d([:：]?[0-5]\\d)?)";
        if(MyStringUtils.isNotNull(content)){
            List<String> mList = matchSegmentList(content,moneyReg);
            List<String> timeList = matchSegmentList(content,dateTimeReg);
            if(!mList.isEmpty()){
                keysList = mList;
                keysList.addAll(timeList);
            }else{
                keysList = timeList;
            }
        }
        return keysList;
    }


    private static List<String> matchSegmentList(String str,String regex){
        List<String> resList= new TreeList();
        Pattern ptn = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
        Matcher matcher = ptn.matcher(str);
        String tmp=null;
        while (matcher.find()) {
            tmp=  matcher.group();
            resList.add(tmp);
        }
        return resList;
    }


}
