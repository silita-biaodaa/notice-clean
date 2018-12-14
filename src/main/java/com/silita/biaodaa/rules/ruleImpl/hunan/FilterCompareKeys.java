package com.silita.biaodaa.rules.ruleImpl.hunan;

import com.silita.biaodaa.rules.Interface.RepeatFilter;
import com.silita.biaodaa.rules.exception.MyRetryException;
import com.silita.biaodaa.utils.BeanUtils;
import com.silita.biaodaa.utils.ComputeResemble;
import com.silita.biaodaa.utils.MyStringUtils;
import com.snatch.model.EsNotice;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
            //1.获取新进公告的关键信息：时间数据序列，数字序列
            logger.debug("@@@开始：提取公告内容中的关键信息"+(esNotice.getPressContent()!=null ? esNotice.getPressContent().length() : null));
            String pressTxt = extractPressContent(esNotice.getPressContent());//压缩文本部分提取
            String noticeKeys = extractKeysMD5(esNotice);//维度信息合并提取

            logger.debug("@@@结束：提取公告内容中的关键信息"+pressTxt+"||"+noticeKeys);
            if(pressTxt==null || noticeKeys==null){
                filterState=IS_NEW;
                logger.info("获取新进公告的关键信息为空[pressTxt:"+pressTxt+"][noticeKeys:"+noticeKeys+"]");
            }else {
                //2.对比疑似队列,获取匹配队列
                Set<EsNotice> matchNotices = new TreeSet<EsNotice>();
                for (EsNotice compareNotice : matchSet) {
                    String pressTxtSub = extractPressContent(compareNotice.getPressContent());//压缩文本部分提取
                    String noticeKeysSub = extractKeysMD5(compareNotice);//维度信息提取

                    if(pressTxtSub==null || noticeKeysSub==null){
                        continue;
                    }else{
                        double sRate = ComputeResemble.similarDegreeWrapper(pressTxt,pressTxtSub);
                        if(noticeKeys.equals(noticeKeysSub) && sRate>0.9 ){//相似特征匹配
                            matchNotices.add(compareNotice);
                            logger.debug("noticeKeysSub and sRate 关键信息匹配成功。");
                        }else{
                            logger.debug("noticeKeysSub and sRate 关键信息比对不匹配。");
                        }
                    }

                }
                //3.对匹配队列，执行去重落地处理
                logger.info("开始：对匹配队列，执行去重落地处理");
                filterState = matchSetExecutor(esNotice, matchNotices);
                logger.info("结束：对匹配队列，执行去重落地处理[filterState:"+filterState+"]");
            }
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
    public synchronized List<String>  extractKeysList(String content)throws Exception{
        List<String> keysList = new ArrayList<>();
        StringBuilder reg = new StringBuilder("(([1-9][\\d]{0,10}|0)(\\.[\\d]{1,6})?([元]|[万元 \\n]))");//moneyReg
        reg.append("|(([1-9]\\d{1,3}[-年]+(0[1-9]|1[0-2]|[1-9])[-月]+(0[1-9]|[1-2][0-9]|3[0-1])[日]?)|((20|21|22|23|[0]?[0-1]\\d)[:：]+[0-5]\\d([:：]?[0-5]\\d)?))");//dateTimeReg
//        reg.append("|(([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,})");//email
        reg.append("|(((13\\d|14[57]|15[^4,\\D]|17[678]|18\\d)\\d{8}|170[059]\\d{7})|(0\\d{2,3}-\\d{7,8})|(\\d{7,8}))");//phone
        if(MyStringUtils.isNotNull(content)){
            List<String> mList = matchSegmentList(content, reg.toString());
            if (!mList.isEmpty()) {
                keysList.addAll(mList);
            }
        }
        return keysList;
    }

    private String extractKeysMD5(EsNotice esNotice){
        StringBuilder keysCompress=new StringBuilder(esNotice.getOpenDate());
        keysCompress.append(esNotice.getSource());
        keysCompress.append(esNotice.getProvince());
        keysCompress.append(esNotice.getCity());
        if(esNotice.getDetailZhongBiao()!=null) {
            keysCompress.append(esNotice.getDetailZhongBiao().getOneName());
            keysCompress.append(esNotice.getDetailZhongBiao().getOneOffer());
        }else if(esNotice.getDetail() !=null){
            keysCompress.append(esNotice.getDetail().getPbMode());
            keysCompress.append(esNotice.getDetail().getBmSite());
            keysCompress.append(esNotice.getDetail().getTbEndDate());
            keysCompress.append(esNotice.getDetail().getTbAssureSum());
            keysCompress.append(esNotice.getDetail().getBmEndDate());
            keysCompress.append(esNotice.getDetail().getBmStartDate());
        }else{
            logger.warn("解析异常公告预警，无detail对象！["+esNotice.getRedisId()+esNotice.getTitle()+esNotice.getUrl()+"]");
        }
        return BeanUtils.getMD5(keysCompress.toString());
    }

    private String extractPressContent(String content){
        StringBuilder sb = new StringBuilder();
        int len = content.length();
        if(content!= null || len>100){
            sb.append(content.substring(0,10));
            sb.append(content.substring(len/2,len/2+20));
            sb.append(content.substring(len-15,len));
            return sb.toString();
        }else{
            return content;
        }
    }


    private static List<String> matchSegmentList(String str,String regex){
        List<String> resList= new ArrayList();
        Pattern ptn = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = null;
        try {
                matcher = ptn.matcher(str);
                String tmp = null;
                while (matcher.find()) {
                    tmp = matcher.group();
                    resList.add(tmp);
                }
        }catch (Exception e){
            logger.error(e,e);
        }finally {
            ptn=null;
            if(matcher!=null) {
                matcher.reset();
                matcher = null;
            }
            str =null;
        }
        return resList;
    }


}
