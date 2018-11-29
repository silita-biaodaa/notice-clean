package com.silita.biaodaa.disruptor.handle.notice;

import com.lmax.disruptor.EventHandler;
import com.silita.biaodaa.disruptor.event.AnalyzeEvent;
import com.silita.biaodaa.rules.Interface.CleaningTemplate;
import com.silita.biaodaa.rules.factory.CleaningFactory;
import com.silita.biaodaa.utils.MyStringUtils;
import com.silita.biaodaa.utils.RegexUtils;
import com.snatch.model.EsNotice;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;

/**
 * Created by dh on 2018/6/26.
 */
@Component
public class CleaningHandle implements EventHandler<AnalyzeEvent> {
    private Logger logger = Logger.getLogger(getClass());

    @Autowired
    private CleaningFactory cleaningFactory;

    @Override
    public void onEvent(AnalyzeEvent event, long sequence, boolean endOfBatch) throws Exception {
        EsNotice esNotice = event.getEsNotice();
        long startTimeCount = System.currentTimeMillis();
        try {
            //适配等级字段
            rankAdapter(esNotice);
            logger.info("cleanHandle start #### " + esNotice.getRedisId() + " [rank:" + esNotice.getRank() + "][source:" + esNotice.getSource() + "][ur:" + esNotice.getUrl() + "]" + esNotice.getTitle() + esNotice.getOpenDate());
            String source = esNotice.getSource();
            if (MyStringUtils.isNull(source)) {
                throw new Exception("接收的新公告source不能为空！请检查。");
            }
            //第一中标金额字段进行适配
            oneOfferAdapter(esNotice);
            CleaningTemplate cleaningTemplate = cleaningFactory.getClearnTemplate(source);
            cleaningTemplate.executeClean(esNotice);
        }catch (Exception e){
            logger.error("清洗逻辑异常，"+e.getMessage(),e);
        }finally {
            logger.info("cleanHandle finished #### " + esNotice.getRedisId() + "[rank:" + esNotice.getRank() + "][source:" + esNotice.getSource() + "][ur:" + esNotice.getUrl() + "]" + esNotice.getTitle() + esNotice.getOpenDate() + " 清洗规则总耗时：" + (System.currentTimeMillis() - startTimeCount) + "ms ####");
        }

    }


    /**
     * 等级适配
     * 没有等级的，设置到rank=1 市级
     * @param notice
     */
    private void rankAdapter(EsNotice notice){
        String areaRank = notice.getAreaRank();
        if(areaRank==null){
            areaRank ="1";
        }
        notice.setRank(Integer.parseInt(areaRank));
    }

    /**
     * 第一中标金额：转换单位为万位纯数字形式
     * @param n
     */
    private void oneOfferAdapter(EsNotice n){
        if(n.getDetailZhongBiao()!=null
                && MyStringUtils.isNotNull(n.getDetailZhongBiao().getOneOffer())){
            String oneOffer = n.getDetailZhongBiao().getOneOffer();
            if(StringUtils.isNotBlank(oneOffer)) {
                oneOffer = numberUnitChange(oneOffer, "(\\d{1,50}[,.]{0,5}\\d{1,50}[,.]{0,5}\\d{0,50}[,.]{0,5}\\d{0,50})", "(万|w|W)", 0.0001);
                if (StringUtils.isNotBlank(oneOffer) && Double.parseDouble(oneOffer)>1) {
                    n.getDetailZhongBiao().setOneOffer(oneOffer);
                }else{
                    n.getDetailZhongBiao().setOneOffer(null);
                }
            }
        }
    }

    /**
     * 转换数字型字符的单位
     * @param s 数字字符
     * @param sRegex 数字字符匹配表达式
     * @param unitRegex 需要转换的单位后缀
     * @param unitRt 转换系数
     * @return
     */
    private String numberUnitChange(String s,String sRegex,String unitRegex,double unitRt){
        String num = RegexUtils.matchValue(s,sRegex);

        int sIdx = s.indexOf(num);
        boolean isUnit=false;
        //取数字后的字符进行判断
        if(sIdx+num.length()+1 <= s.length()){
            if(RegexUtils.matchExists(s.substring(sIdx+num.length(),sIdx+num.length()+1),unitRegex)){
                isUnit=true;
            }
        }

        DecimalFormat df = new DecimalFormat("#,###.######");
        try {
            if(!isUnit) {
                Number n = df.parse(num);
                Double d = n.doubleValue();
                s = String.valueOf(d * unitRt);
            }else{
                s = num;
            }
            s = df.format(Double.parseDouble(s));
        } catch (Exception e) {
            s=null;
            logger.error(e, e);
        }finally {
            return s;
        }
    }

}
