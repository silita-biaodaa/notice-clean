package com.silita.biaodaa.rules.ruleImpl.hunan;

import com.silita.biaodaa.common.Constant;
import com.silita.biaodaa.common.config.CustomizedPropertyConfigurer;
import com.silita.biaodaa.common.elastic.indexes.IdxZhaobiaoSnatch;
import com.silita.biaodaa.common.elastic.indexes.IdxZhongbiaoSnatch;
import com.silita.biaodaa.common.redis.RedisClear;
import com.silita.biaodaa.dao_temp.SnatchNoticeHuNanDao;
import com.silita.biaodaa.disruptor.DisruptorOperator;
import com.silita.biaodaa.service.NoticeRuleService;
import com.silita.biaodaa.service.SnatchService;
import com.silita.biaodaa.service.impl.NoticeCleanService;
import com.silita.biaodaa.utils.ChineseCompressUtil;
import com.silita.biaodaa.utils.ComputeResemble;
import com.silita.biaodaa.utils.MyStringUtils;
import com.snatch.model.AnalyzeDetail;
import com.snatch.model.AnalyzeDetailZhongBiao;
import com.snatch.model.EsNotice;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.silita.biaodaa.rules.Interface.RepeatRule.*;
import static com.silita.biaodaa.utils.RuleUtils.*;

/**
 * Created by dh on 2018/3/14.
 */
public abstract class HunanBaseRule {

    private Logger logger = Logger.getLogger(HunanBaseRule.class);


    @Autowired
    private DisruptorOperator disruptorOperator;

    @Autowired
    protected RedisClear redisClear;

    @Autowired
    protected SnatchService snatchService;

    @Autowired
    protected SnatchNoticeHuNanDao snatchNoticeHuNanDao;

    @Autowired
    NoticeCleanService noticeCleanService;

    @Autowired
    NoticeRuleService noticeRuleService;


    protected ChineseCompressUtil chineseCompressUtil = new ChineseCompressUtil();

    protected static String[] normalUrl = {"www.zjjsggzy.gov.cn", "www.sysggzy.com", "ggzyjy.xxz.gov.cn", "ggzy.xiangtan.gov.cn", "csggzy.gov.cn"};

    protected static String[] keyWords1 = {"项目", "施工", "工程", "标段", "监理", "代理", "采购", "勘察", "设计"};

    protected static String[] keyWords2 = {"控制价", "修改", "终止", "废标", "开标", "变更", "更正", "调整", "延期", "推迟", "延长",
            "澄清", "流标", "答疑", "补疑", "质疑", "补充", "补遗", "暂停", "入围", "资格预审", "资审结果",
            "合同", "结果", "成交", "成果", "中选", "比选", "预审", "谈判", "磋商", "询价", "竞价", "单一来源"};

    protected static String[] keyWords3 = {"监理", "代理", "采购", "勘察", "设计"};

    protected static String[] keyWords4 = {"中标", "修改", "终止", "废标", "开标", "变更", "更正", "调整", "延期", "推迟", "延长",
            "澄清", "流标", "答疑", "补疑", "质疑", "补充", "补遗", "暂停"};

    protected static String[] keyWords5 = {"招标", "中标", "修改", "终止", "废标", "开标", "变更", "更正", "调整", "延期", "推迟", "延长", "澄清", "流标", "答疑",
            "补疑", "质疑", "补充", "补遗", "暂停"};

    protected static String[] keyWords6 = {"招标", "中标", "修改", "终止", "废标", "开标", "变更", "更正", "调整", "延期", "推迟", "延长", "澄清",
            "流标", "答疑", "补疑", "质疑", "补充", "补遗", "暂停", "通知", "名称", "其他", "编号"};

    protected static String[] keyWords7 = {"项目", "施工", "工程", "标段", "监理", "代理", "采购", "勘察", "设计", "招标", "中标", "控制价", "修改",
            "终止", "废标", "开标", "变更", "更正", "调整", "延期", "推迟", "延长", "澄清", "流标", "答疑", "补疑", "质疑", "补充", "补遗", "暂停",
            "入围", "资格预审", "资审结果", "合同", "结果", "成交", "成果", "中选", "比选", "预审", "谈判", "磋商", "询价", "竞价", "单一来源",
            "公告", "公示", "关于", "信息公示表", "回复", "候选人", "信息"};

    protected String[] mainWebside = {"ggzyjy.xxz.gov.cn", "ggzy.huaihua.gov.cn", "ggzy.yzcity.gov.cn", "czggzy.czs.gov.cn", "ggzy.yueyang.gov.cn", "sysggzy.com", "ggzy.xiangtan.gov.cn", "hyggzyjy.hengyang.gov.cn", "zzzyjy.cn", "ggzy.changde.gov.cn", "csx.gov.cn", "liuyang.gov.cn", "wangcheng.gov.cn", "61.186.94.156", "csggzy.gov.cn", "zjjsggzy.gov.cn", "ldggzy.hnloudi.gov.cn", "bidding.hunan.gov.cn", "jyzx.yiyang.gov.cn"};

    public String clearKeyWord(String str) {
        for (int i = 0; i < keyWords7.length; i++) {
            if (str.contains(keyWords7[i])) {
                StringBuilder sb = new StringBuilder(str);
                int a = str.indexOf(keyWords7[i]);
                sb = sb.delete(a, a + keyWords7[i].length());
                str = sb.toString();
            }
        }
        return str;
    }

    /**
     * 判断公告是否为张家界、长沙、邵阳、湘西、湘潭
     *
     * @param noticeUrl
     * @return
     */
    public int urlIndexOf(String noticeUrl) {
        for (int i = 0; i < normalUrl.length; i++) {
            if (noticeUrl.contains(normalUrl[i])) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 替换标题中的空格与符号，标题前后添加%
     *
     * @param title
     * @return
     */
    public String replaceStrSymbol(String title) {
        if (MyStringUtils.isNotNull(title)) {
            // 标题中的空格符号替换为%
            title = title.replaceAll("[\\s~·`!！@￥$%^……&*（()）\\-——\\-_=+【\\[\\]】｛{}｝\\|、\\\\；;：:‘'“”\"，,《<。.》>、/？? ]", "%");

            // 替换相关关键字为%
            title = title.replaceAll("(招标|中标|项目|施工|工程)", "%");

            // 标题前后添加%
            if (title.charAt(0) != '%') {
                title = "%" + title;
            }
            if (title.charAt(title.length() - 1) != '%') {
                title = title + "%";
            }
        }
        return title;
    }

    /**
     * 判断字符串的所有字符是否都一样
     *
     * @param str
     * @return
     */
    public boolean allIsSameChars(String str) {
        if (str != null && str.length() > 1) {
            char a = str.charAt(0);
            for (int i = 1; i < str.length(); i++) {
                if (str.charAt(i) != a) {
                    return false;
                }
            }
            return true;
        } else {
            return true;
        }
    }


    /**
     * 标题关键字截取
     *
     * @param title
     * @return
     */
    public String subSearchTitle(String title) {
        if (title.contains("关于") && title.indexOf("关于") == 0) {
            title = title.substring(2);
        }

        // 一级关键字截取
        String[] rank1KeyWord = new String[3];
        System.arraycopy(keyWords1, 0, rank1KeyWord, 0, rank1KeyWord.length);
        int rank1KeyWordIndex = keyWordsIndex(title, rank1KeyWord);
        if (rank1KeyWordIndex != -1) {
            return title.substring(0, rank1KeyWordIndex);
        }

        // 二级关键字截取
        String[] rank2KeyWord = new String[6];
        System.arraycopy(keyWords1, 3, rank2KeyWord, 0, rank2KeyWord.length);
        int rank2KeyWordIndex = keyWordsIndex(title, rank2KeyWord);
        if (rank2KeyWordIndex != -1) {
            return title.substring(0, rank2KeyWordIndex);
        }

        // 三级关键字截取
        String[] rank3KeyWord = {"招标", "中标"};
        int rank3KeyWordIndex = keyWordsIndex(title, rank3KeyWord);
        if (rank3KeyWordIndex != -1) {
            return title.substring(0, rank3KeyWordIndex);
        }

        // 四级关键字截取
        int rank4KeyWordIndex = keyWordsIndex(title, keyWords2);
        if (rank4KeyWordIndex != -1 && !title.contains("维修改造")) {
            return title.substring(0, rank4KeyWordIndex);
        }

        // 截取 50% 的字符
        return subHalfString(title);
    }

    /**
     * 截取中间一半的字符串
     *
     * @param str
     * @return
     */
    public String subHalfString(String str) {
        int a = str.length() / 2;
        int b = a / 2;
        str = str.substring(b);
        str = str.substring(0, str.length() - (a - b));
        return str;
    }

    /**
     * 设置公告主类型type
     * (历史逻辑，修改时需同时修改历史web工程的公告页面展示部分)
     * @param esNotice
     */
    protected int convertType(EsNotice esNotice){
        int type = esNotice.getType();
        if (type == 2 || type == 5 || type == 51 || type == 52
                ||esNotice.getTitle().endsWith("信息公示表")) {
            type = 2;
        } else {
            type = 0;
        }
        return type;
    }


    /**
     * 新进公告属性设置（入库时需要）
     *
     * @param notice
     * @return
     */
    public EsNotice setNoticeAttribute(EsNotice notice) {
        if (StringUtils.isBlank(notice.getSnatchNumber())) {
            notice.setSnatchNumber("");
        }
        String businessType = notice.getBusinessType();
        if (StringUtils.isBlank(businessType)) {
            notice.setBusinessType("");
        }

        if (StringUtils.isNotBlank(businessType) && businessType.equals("0")) {
            businessType = "3";
        } else {
            //判断公告是什么类型
            if (notice.getTitle().indexOf("设计") != -1) {
                businessType = "1";
            } else if (notice.getTitle().indexOf("监理") != -1) {
                businessType = "2";
            } else if (notice.getTitle().indexOf("采购") != -1 || notice.getTitle().indexOf("谈判") != -1 || notice.getTitle().indexOf("磋商") != -1) {
                businessType = "3";
            } else if (notice.getTitle().indexOf("勘察") != -1) {
                businessType = "4";
            } else if (notice.getTitle().indexOf("检测") != -1) {
                businessType = "5";
            } else {
                businessType = "0";
            }
        }
        notice.setBiddingType(businessType);
        // type属性分离为2个字段（type otherType）
        int type = notice.getType();
        int otherType = 0;
        if (type < 10) {
            if (notice.getTitle().indexOf("补充") != -1) {
                otherType = 1;
            } else if (notice.getTitle().indexOf("答疑") != -1) {
                otherType = 2;
            } else if (notice.getTitle().indexOf("流标") != -1) {
                otherType = 3;
            } else if (notice.getTitle().indexOf("澄清") != -1) {
                otherType = 4;
            } else if (notice.getTitle().indexOf("延期") != -1) {
                otherType = 5;
            } else if (notice.getTitle().indexOf("更正公告") != -1) {
                otherType = 6;
            } else if (notice.getTitle().indexOf("废标") != -1 && notice.getTitle().indexOf("终止") != -1) {
                otherType = 7;
            } else if (notice.getTitle().indexOf("终止") != -1) {
                otherType = 8;
            }
        } else if (type == 11) {
            otherType = 1;
        } else if (type == 12) {
            otherType = 2;
        } else if (type == 13) {
            otherType = 3;
        } else if (type == 14) {
            otherType = 4;
        } else if (type == 15) {
            otherType = 5;
        } else if (type == 16) {
            otherType = 6;
        } else if (type == 17) {
            otherType = 7;
        } else if (type == 18) {
            otherType = 8;
        } else if (type == 19) {
            otherType = 9;
        } else if (type == 20) {
            otherType = 10;
        } else if (type == 21) {
            otherType = 11;
        } else if (type == 22) {
            otherType = 12;
        } else if (type == 23) {
            otherType = 13;
        } else if (type == 24) {
            otherType = 14;
        } else {
            otherType = type;
        }
        notice.setType(convertType(notice));
        notice.setOtherType(String.valueOf(otherType));

        String areaRank = notice.getAreaRank();
        if (StringUtils.isBlank(areaRank) || areaRank.equals("___")) {
            //查询湖南的抓取的网站和当前url做对比。获取网站等级
            List<Map<String, Object>> webList = snatchService.querysWebSitePlan(notice.getTableName().replaceAll("mishu.", ""));
            Integer rank = 0;
            for (Map<String, Object> wm : webList) {
                if (notice.getUrl().indexOf(String.valueOf(wm.get("url"))) > -1) {
                    rank = Integer.valueOf(String.valueOf(wm.get("rank")));
                    notice.setWebsitePlanId(Integer.valueOf(String.valueOf(wm.get("id"))));
                    break;
                }
            }
            notice.setRank(rank);
        } else {
            notice.setRank(Integer.parseInt(areaRank));
        }
        if (notice.getWebsitePlanId() == null) {
            notice.setWebsitePlanId(0);
        }
        if (notice.getType() == 2) {
            AnalyzeDetailZhongBiao detailZhongBiao = notice.getDetailZhongBiao();
            if (detailZhongBiao == null) {
                detailZhongBiao = new AnalyzeDetailZhongBiao();
            }
            detailZhongBiao.setGsDate(notice.getOpenDate());
            notice.setDetailZhongBiao(detailZhongBiao);
        } else {
            AnalyzeDetail detail = notice.getDetail();
            if (detail == null) {
                detail = new AnalyzeDetail();
            }
            detail.setGsDate(notice.getOpenDate());
            notice.setDetail(detail);
        }
        return notice;
    }

    /**
     * 招标、中标公告入库(无重复公告)
     *
     * @param notice
     */
    public void handleNotRepeat(EsNotice notice) {
        setNoticeAttribute(notice);
        //插入公告基本信息
        noticeCleanService.insertSnatchUrl(notice);
        Integer id = noticeCleanService.getMaxSnatchUrlIdByUrl(notice);
        //插入公告内容
        noticeCleanService.insertSnatchContent(notice, id);
        noticeCleanService.insertSnatchPress(notice, id);

        notice.setUuid(String.valueOf(id));
        notice.setOtherType(notice.getOtherType());
        notice.setBiddingType(notice.getBiddingType());

        String source = notice.getSource();
        logger.debug("####source:"+source);

        //仅湖南数据更新维度，资质与es
        if (source.equals(Constant.HUNAN_SOURCE)) {
            // 插入维度信息
            noticeCleanService.insertDetail(notice);
            if (notice.getType() == 2) { //中标直接更新索引，不涉及资质
                String insertEs = (String)CustomizedPropertyConfigurer.getContextProperty("es.data.send");
                if(insertEs != null && insertEs.equals("true")){
                    try {
                        logger.info("中标公告插入es：start");
                        snatchNoticeHuNanDao.insertZhongbiaoEsNotice(notice);
                        logger.info("中标公告插入es: finished");
                    } catch (Exception e) {
                        logger.error("中标公告入es异常" + e,e);
                    }
                }else{
                    logger.info("中标公告取消插入es");
                }

            } else {
                //非2中标公告发起资质匹配任务
                disruptorOperator.publishQuaParse(notice);
            }
        }
    }

    /**
     * 解析结果插入招标公告临时维度表
     *
     * @param zhaobiaoDetail
     * @param notice
     */
    public void insertZhaobiaoAnalyzeDetail(AnalyzeDetail zhaobiaoDetail, EsNotice notice) {
        //把解析结果插入维度临时表
        zhaobiaoDetail.setRedisId(Integer.parseInt(notice.getUuid()));
        snatchNoticeHuNanDao.insertOrUpdateAnalyzeDetail(zhaobiaoDetail);
    }


    /**
     * 解析结果插入中标公告临时维度表
     *
     * @param zhongbiaoDetail
     * @param notice
     */
    public void insertZhongbiaoAnalyzeDetail(AnalyzeDetailZhongBiao zhongbiaoDetail, EsNotice notice) {
        //把解析结果插入程序解析维度表
        zhongbiaoDetail.setRedisId(Integer.parseInt(notice.getUuid()));
        snatchNoticeHuNanDao.insertOrUpdateAnalyzeDetailZhongBiao(zhongbiaoDetail);
    }

    /**
     * 判断公告内容是否为附件
     *
     * @param content
     * @return
     */
    public boolean isHasFile(String content) {
        String regex = "(href=\"|src=\"|href =\"|src =\"|href = \"|src = \").*?(zip|rar|7z|docx|doc|jpg|jpeg|png|ppt|xls|wps|xlsx)";
        Pattern pa = Pattern.compile(regex);
        Matcher ma = pa.matcher(content);
        if (ma.find()) {
            String newContent = chineseCompressUtil.getPlainText(content);
            newContent = MyStringUtils.deleteHtmlTag(newContent);
            newContent = newContent.replaceAll(" ", "");
            return newContent.length() < 100;
        }
        return false;
    }


    /**
     * 公告过滤
     *
     * @param notice         新进公告
     * @param historyNotices 历史公告
     */
    public List<EsNotice> noticeFilter(EsNotice notice, List<EsNotice> historyNotices) {
        // 历史公告过滤
        logger.info("@@@@  公告进行分类过滤!  @@@@");
        String title = notice.getTitle();

        // 标段过滤
        if (!historyNotices.isEmpty()) {
            Iterator<EsNotice> it = historyNotices.iterator();
            while (it.hasNext()) {
                String historyTitle = it.next().getTitle();
                if (title.contains("标段")) {
                    if (!historyTitle.contains("标段")) {
                        it.remove();
                        break;
                    }
                    int titleBiaoduanIndex = title.lastIndexOf("标段");
                    int historyBiaoduanIndex = historyTitle.lastIndexOf("标段");
                    String titleNumStr = getNumStr(title.substring(0, titleBiaoduanIndex));
                    String historyTitleNumStr = getNumStr(historyTitle.substring(0, historyBiaoduanIndex));
                    if (!titleNumStr.equals(historyTitleNumStr)) {
                        it.remove();
                    }
                } else if (historyTitle.contains("标段")) {
                    it.remove();
                }
            }
            logger.info("####  标段过滤 ..  historyNotices：" + historyNotices.size() + "  ####");
        }

        // 括号内容过滤
        if (!historyNotices.isEmpty()) {
            if (contaninsBracket(title)) {
                int keyIndex = keyWordsIndex(title, keyWords5); // 获取第一个关键字的位置
                if (keyIndex != -1) {
                    String tempTitle = title.substring(0, keyIndex);
                    if (contaninsBracket(tempTitle)) {
                        Iterator<EsNotice> it = historyNotices.iterator();
                        while (it.hasNext()) {
                            String historyTitle = it.next().getTitle();
                            if (!compareBracketStr(tempTitle, historyTitle, keyWords6)) {
                                it.remove();
                            }
                        }
                    }
                }
            }
            logger.info("####  括号内容过滤 .. historyNotices: " + historyNotices.size() + "  ####");
        }
        return historyNotices;
    }


    /**
     * 两条公告去重
     *
     * @param notice
     */
    @Deprecated
    public boolean handleRepeat(EsNotice notice, EsNotice historyNotice) {
        if (notice.getRank() == 0 && historyNotice.getRank() != 0) {//新公告等级低
            // 插入新进公告(省网)，isshow = 1
            notice.setIsShow(1);
            //插入公告基本信息
            noticeCleanService.insertSnatchUrl(notice);
            Integer id = noticeCleanService.getMaxSnatchUrlIdByUrl(notice);
            //插入公告内容
            noticeCleanService.insertSnatchContent(notice, id);
            noticeCleanService.insertSnatchPress(notice, id);
            logger.info("@@@@  新公告(省网)被去重 .. [redisId:" + notice.getRedisId() + "]title：" + notice.getTitle() + "  历史公告 : " + historyNotice.getTitle() + "  @@@@");
            return false;
        }

        if (notice.getRank() != 0 && historyNotice.getRank() == 0) {//新公告等级高于历史公告
            // 插入新进公告，历史公告isshow = 1
            notice.setEdit(historyNotice.getEdit());
            handleNotRepeat(notice);
            noticeCleanService.updateIsShowById(historyNotice.getUuid(), 1, notice.getSource());

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
            noticeCleanService.delRelationInfoAndEditDetail(notice, historyNotice);
            logger.info("@@@@  新公告入库，历史公告(省网)被去重 .. title：" + notice.getTitle() + "  历史公告 : " + historyNotice.getTitle() + "  @@@@");
            return true;
        }

        // 新进公告与历史公告都不是省网,保留市级
        if (notice.getRank() == 1 && historyNotice.getRank() == 2) {//新公告替换历史
            //新公告update历史公告
            redisClear.clearRepeatNotice(historyNotice.getUuid());

            notice.setUuid(historyNotice.getUuid());
            notice.setEdit(historyNotice.getEdit());

            //更新基本表
            noticeCleanService.updateSnatchUrl(notice, historyNotice);
            //更新内容
            noticeCleanService.updateSnatchurlContent(notice);
            noticeCleanService.updateSnatchpress(notice);

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
                noticeCleanService.delRelationInfoAndEditDetail(notice, historyNotice);
            }
            logger.info("@@@@  新公告替换历史公告 .. title: " + notice.getTitle() + "  历史公告 : " + historyNotice.getTitle() + "  @@@@");
            return true;
        }

        if (notice.getRank() == 2 && historyNotice.getRank() == 1) {
            // 新公告进去重表
            notice.setUuid(historyNotice.getUuid());
            noticeCleanService.insertSnatchurlRepetition(historyNotice);
            logger.info("@@@@  新公告被历史公告去重 .. title: " + notice.getTitle() + "  历史公告 : " + historyNotice.getTitle() + "  @@@@");
            return false;
        }

        handleNotRepeat(notice);
        return true;
    }


    /**
     * 去除指定字符（只去除第一个）
     *
     * @param c
     * @param str
     * @return
     */
    public String clearStrOnlyOne(String c, String str) {
        StringBuilder sb = new StringBuilder(str);
        if (str.contains(c) && sb.indexOf(c) != -1) {
            sb.delete(sb.indexOf(c), sb.indexOf(c) + c.length());
        }
        return sb.toString();
    }

    //2018-9-13 #######################################
    /**
     * 公告内容过滤逻辑
     * @param esNotice
     * @param matchSets
     * @return
     */
    protected String matchSetExecutor(EsNotice esNotice,Set<EsNotice> matchSets) throws Exception{
        String filterState = "";
        if (matchSets.size() > 0) {
            boolean isRepeat = false;
            Iterator iter = matchSets.iterator();
            boolean isReplaced = false;//是否(新公告)已替换历史公告
            boolean intoRepeat = false;//新公告是否已进入去重表
            while (iter.hasNext()) {
                String repeatExecute = "";
                EsNotice esnt = (EsNotice) iter.next();
                Integer detailId = null;
                if (esnt.getDetail() != null) {
                    detailId = esnt.getDetail().getId();
                }

//                //匹配集合进行相似度判断
//                String esntPress = esnt.getPressContent();
//                double computeNum = ComputeResemble.similarDegreeWrapper(esNotice.getPressContent(), esntPress);
//                if (type == 2) {//中标公告20%
//                    if (computeNum > 0.2) {
//                        isRepeat = true;
//                    }
//                } else {//非中标公告85%
//                    if (computeNum > 0.85) {
//                        isRepeat = true;
//                    }
//                }
//                logger.debug("相识度比对结果[type"+type+"][computeNum:"+computeNum+"][isRepeat:"+isRepeat+"][detailId:"+detailId+"]");
                isRepeat=true;
                double computeNum=0.9;
                logger.debug("相识度比对取消`。。。");

                //符合条件，进行去重判断
                if (isRepeat) {
                    if (esNotice.getRank() != 0 && esnt.getRank() == 0) {//去重（历史）省公告
                        //只替换有编辑内容的第一条编辑过的公告（已按ID排序）`
                        if (detailId != null && detailId > 0 ) {
                            repeatExecute = "newReplaceHis";
                        } else {
                            repeatExecute = "delHis";
                        }
                    }else if(esNotice.getRank() == esnt.getRank()){
                        if(computeNum==1){
                            repeatExecute = "intoRepetition";
                        }else{
                            repeatExecute= "newReplaceHis";
                        }
                    }else if(esNotice.getRank()==0 && esnt.getRank() != 0){
                        repeatExecute = "intoRepetition";
                    }else if(esNotice.getRank() != 0 && esnt.getRank() != 0){
                        if(computeNum==1){
                            repeatExecute = "intoRepetition";
                        }else{
                            repeatExecute= "newReplaceHis";
                        }
                    }else{
                        logger.error("网站等级判定异常[title:"+esNotice.getTitle()+"][新公告等级:+"+esNotice.getRank()+"]:[esnt.title:"+esnt.getTitle()+"][匹配公告等级:"+esnt.getRank()+"]");
                    }

                    logger.debug("网站等级判断完毕[repeatExecute:"+repeatExecute+"][esNotice.getRank():"+esNotice.getRank()+"][esnt.getRank():"+esnt.getRank()+"]");


                    if (repeatExecute.equals("newReplaceHis")) {
                        if (!isReplaced) {
                            isReplaced = true;
                            noticeCleanService.replaceHistoryNotice(esNotice, esnt);//新公告替换历史公告，历史公告进入去重表
                        } else {
                            noticeCleanService.delHistoryNotice(esnt);//历史公告删除
                        }
                    } else if (repeatExecute.equals("delHis")) {
                        noticeCleanService.delHistoryNotice(esnt);//历史公告删除
                    } else if (repeatExecute.equals("intoRepetition")) {
                        if (!intoRepeat) {
                            noticeCleanService.insertSnatchurlRepetition(esNotice);
                            intoRepeat = true;
                        } else {
                            continue;
                        }
                    }

                }
            }

            if (isReplaced) {//公告已做替换处理，流程结束
                filterState = IS_UPDATED;
            } else if(intoRepeat) {
                filterState = IS_REPEATED;
            }else{
                filterState = IS_NEW;
            }
        }else{
            filterState = IS_NEW;
        }
        return filterState;
    }

    protected String matchAllExecutor(EsNotice esNotice,Set<EsNotice> matchSets) throws Exception{
        String filterState = "";
        int type = esNotice.getType();
        if (matchSets.size() > 0) {
            boolean isRepeat = false;
            Iterator iter = matchSets.iterator();
            boolean isReplaced = false;//是否(新公告)已替换历史公告
            boolean intoRepeat = false;//新公告是否已进入去重表
            while (iter.hasNext()) {
                String repeatExecute = "";
                EsNotice esnt = (EsNotice) iter.next();
                Integer detailId = null;
                if (esnt.getDetail() != null) {
                    detailId = esnt.getDetail().getId();
                }

                //匹配集合进行相似度判断
                String esntPress = esnt.getPressContent();
                double computeNum = ComputeResemble.similarDegreeWrapper(esNotice.getPressContent(), esntPress);
                if (computeNum > 0.98) {
                    isRepeat = true;
                }

                logger.debug("相识度比对结果[type"+type+"][computeNum:"+computeNum+"][isRepeat:"+isRepeat+"][detailId:"+detailId+"]");

                //符合条件，进行去重判断
                if (isRepeat) {
                    if (esNotice.getRank() != 0 && esnt.getRank() == 0) {//去重（历史）省公告
                        //只替换有编辑内容的第一条编辑过的公告（已按ID排序）`
                        if (detailId != null && detailId > 0 ) {
                            repeatExecute = "newReplaceHis";
                        } else {
                            repeatExecute = "delHis";
                        }
                    }else if(esNotice.getRank() == esnt.getRank()){
                        if(computeNum==1){
                            repeatExecute = "intoRepetition";
                        }else{
                            repeatExecute= "newReplaceHis";
                        }
                    }else if(esNotice.getRank()==0 && esnt.getRank() != 0){
                        repeatExecute = "intoRepetition";
                    }else if(esNotice.getRank() != 0 && esnt.getRank() != 0){
                        if(computeNum==1){
                            repeatExecute = "intoRepetition";
                        }else{
                            repeatExecute= "newReplaceHis";
                        }
                    }else{
                        logger.error("网站等级判定异常[title:"+esNotice.getTitle()+"][新公告等级:+"+esNotice.getRank()+"]:[esnt.title:"+esnt.getTitle()+"][匹配公告等级:"+esnt.getRank()+"]");
                    }

                    logger.debug("网站等级判断完毕[repeatExecute:"+repeatExecute+"][esNotice.getRank():"+esNotice.getRank()+"][esnt.getRank():"+esnt.getRank()+"]");


                    if (repeatExecute.equals("newReplaceHis")) {
                        if (!isReplaced) {
                            isReplaced = true;
                            noticeCleanService.replaceHistoryNotice(esNotice, esnt);//新公告替换历史公告，历史公告进入去重表
                        } else {
                            noticeCleanService.delHistoryNotice(esnt);//历史公告删除
                        }
                    } else if (repeatExecute.equals("delHis")) {
                        noticeCleanService.delHistoryNotice(esnt);//历史公告删除
                    } else if (repeatExecute.equals("intoRepetition")) {
                        if (!intoRepeat) {
                            noticeCleanService.insertSnatchurlRepetition(esNotice);
                            intoRepeat = true;
                        } else {
                            continue;
                        }
                    }

                }
            }

            if (isReplaced) {//公告已做替换处理，流程结束
                filterState = IS_UPDATED;
            } else if(intoRepeat) {
                filterState = IS_REPEATED;
            }else{
                filterState = IS_NEW;
            }
        }else{
            filterState = IS_NEW;
        }
        return filterState;
    }

    /**
     * 根据filterList关键字列表，获取关键字之前的括号内容(列表),
     * 返回列表中排除excludeList集合中的内容。
     * @param filterList
     * @param excludeList
     * @param title
     * @return
     */
    protected List<String> filterSegment(List<Map> filterList,String title,List<Map> excludeList){
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
     * 构造匹配公告的（标题模糊匹配）查询条件
     * @param esNotice
     * @return
     * @throws Exception
     */
    protected Map buildTitleMatchParam(EsNotice esNotice)throws Exception{
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
        String openDate = esNotice.getOpenDate();
        String url = esNotice.getUrl();
        Map argMap = new HashMap();
        argMap.put("titleKey", "%" + titleKey + "%");
        argMap.put("source", source);
        argMap.put("province", province);
        argMap.put("city", city);
        logger.debug("title.indexOf(\"流标公告\"):"+title.indexOf("流标公告"));
        if(title.indexOf("流标公告")==-1) {
            argMap.put("type", convertType(esNotice));
        }
        argMap.put("openDate", openDate);
        argMap.put("url", "%" + extractUrlHost(url) + "%");
        if (title.indexOf("标段") == -1) {
            argMap.put("notLike", "%标段%");
        }

        return argMap;
    }

    protected String extractUrlHost(String url)throws Exception {
        return new URI(url).getHost().toString();
    }

    /**
     * 替换标点符号
     * @param str
     * @param regex
     * @param targetStr
     * @return
     */
    protected String replacePunctuation(String str,String regex,String targetStr){
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
}

