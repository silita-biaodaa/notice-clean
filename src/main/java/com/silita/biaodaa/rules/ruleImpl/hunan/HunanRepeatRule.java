package com.silita.biaodaa.rules.ruleImpl.hunan;

import com.silita.biaodaa.common.elastic.indexes.IdxZhaobiaoSnatch;
import com.silita.biaodaa.common.elastic.indexes.IdxZhongbiaoSnatch;
import com.silita.biaodaa.rules.Interface.RepeatRule;
import com.silita.biaodaa.service.INoticeCleanService;
import com.silita.biaodaa.utils.ComputeResemble;
import com.silita.biaodaa.utils.MyStringUtils;
import com.snatch.model.EsNotice;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by dh on 2018/3/14.
 */
@Component
public class HunanRepeatRule extends HunanBaseRule implements RepeatRule {
    private static Logger logger = Logger.getLogger(HunanRepeatRule.class);

    @Autowired
    INoticeCleanService noticeCleanService;

    @Override
    public boolean executeRule(EsNotice esNotice) {
        logger.info("湖南去重开始[redisId:"+esNotice.getRedisId()+"][source:"+esNotice.getSource()+"][ur:"+esNotice.getUrl()+"]" + esNotice.getTitle() + esNotice.getOpenDate());
        try {
            int isExist = noticeCleanService.countSnastchUrlByUrl(esNotice);
            //url判断（已存在不入库）
            if (isExist != 0) {
                logger.info("#### 数据库中已存在相同url:" + esNotice.getUrl() + " ####");
                return false;
            }

            String title = esNotice.getTitle();
            // 截取模糊匹配关键字
            String tempTitle = subSearchTitle(title);

            // 截取后的模糊匹配关键字替换 %
            tempTitle = replaceStrSymbol(tempTitle);
            if (MyStringUtils.isNotNull(tempTitle) && !allIsSameChars(tempTitle)) {//判断tempTitle的所有字符都一样
                long repStartTime = System.currentTimeMillis(); // 去重开始时间
                // 进行模糊匹配
                String baseUri = "";
                try {
                    baseUri = new URI(esNotice.getUrl()).getHost();
                } catch (URISyntaxException e) {
                    for (String a : mainWebside) {
                        if (esNotice.getUrl().contains(a)) {
                            baseUri = a;
                            break;
                        }
                    }
                }
                if (MyStringUtils.isNull(baseUri)) {
                    // 直接入库,更新到es
                    handleNotRepeat(esNotice);
                    return true;
                }

                // 标题模糊搜索公告
                List<EsNotice> resultNotices = noticeCleanService.listEsNotice(tempTitle, baseUri, esNotice);

                // set去重
                Set<EsNotice> esNotices = new HashSet<EsNotice>(resultNotices);

                // 标题相似度搜索公告
                List<EsNotice> result2 = noticeCleanService.listEsNotice(baseUri, esNotice);
                for (EsNotice no : result2) { // 只保留与新进公告标题80%相似度以上的
                    if (ComputeResemble.similarDegreeWrapper(title, no.getTitle()) > 0.8) {
                        esNotices.add(no);
                    }
                }

                resultNotices = new ArrayList<EsNotice>(esNotices);
                logger.info("##### 查询消耗时间：" + (System.currentTimeMillis() - repStartTime) + " ms #####");

                if (resultNotices.isEmpty()) {
                    // 直接入库,湖南数据更新到es
                    handleNotRepeat(esNotice);
                    return true;
                } else if (resultNotices.size() == 1) {
                    if (title.contains("关于") && title.indexOf("关于") == 0) {
                        title = title.substring(2);
                    }
                    EsNotice historyNotice = resultNotices.get(0);
                    String historyTitle = historyNotice.getTitle();
                    if (historyTitle.contains("关于") && historyTitle.indexOf("关于") == 0) {
                        historyTitle = historyTitle.substring(2);
                    }
                    if (title.equals(historyTitle)) { // 新进公告与数据库中标题一样
                        // 判断是否有附件
                        boolean historyNoticeHasFile = isHasFile(historyNotice.getContent());
                        boolean esNoticeHasFile = isHasFile(esNotice.getContent());
                        if (historyNoticeHasFile) {
                            // 历史公告有附件
                            if (esNoticeHasFile) {
                                // 历史公告与新进公告都有附件，保留市级公告
                                return handleRepeat(esNotice, historyNotice);
                            } else {
                                // 新进公告没有附件,历史公告有附件，新公告(历史公告去重)
                                handleNotRepeat(esNotice);
                                if (historyNotice.getRank() == 0) {
                                    //update历史公告 isshow = 1
                                    noticeCleanService.updateIsShowById(historyNotice.getUuid(), 1, historyNotice.getSource());
//                                    snatchNoticeHuNanDao.updateSnatchurlisShow(historyNotice.getUuid(), 1, historyNotice.getSource());
                                    // 删除es上的历史公告索引
                                    if (historyNotice.getType() == 2) {
                                        // 删除中标公告索引
                                        snatchNoticeHuNanDao.deleteIndexById(IdxZhongbiaoSnatch.class, historyNotice.getUuid());
                                    } else {
                                        // 删除招标公告索引
                                        snatchNoticeHuNanDao.deleteIndexById(IdxZhaobiaoSnatch.class, historyNotice.getUuid());
                                    }
                                    // 历史公告关联信息删除、编辑信息更改
                                    delRelationInfoAndEditDetail(esNotice, historyNotice);

                                    logger.info("@@@@  新公告入库，历史公告(省网)被去重 .. title：" + esNotice.getTitle() + "  历史公告 : " + historyNotice.getTitle() + "  @@@@");
                                } else {
                                    esNotice.setUuid(historyNotice.getUuid());
                                    noticeCleanService.insertSnatchurlRepetition(historyNotice);
                                    noticeCleanService.deleteSnatchUrl(historyNotice.getUuid());

                                    // 历史公告关联信息删除、编辑信息更改
                                    delRelationInfoAndEditDetail(esNotice, historyNotice);

                                    logger.info("@@@@  新公告入库，历史公告被去重 .. title: " + esNotice.getTitle() + "  历史公告 : " + historyNotice.getTitle() + "  @@@@");
                                }
                                return true;
                            }
                        } else {
                            if (esNoticeHasFile) {
                                // 历史公告没附件，新进公告有附件,保留历史公告(新公告进去重表)
                                esNotice.setUuid(historyNotice.getUuid());
                                noticeCleanService.insertSnatchurlRepetition(esNotice);
                                logger.info("@@@@  新公告被历史公告去重 .. title: " + esNotice.getTitle() + "  历史公告 : " + historyNotice.getTitle() + "  @@@@");
                                return false;
                            } else {
                            /* 历史公告与新进公告的内容处理后进行相似度对比 */
                                // 历史公告与新进公告都没附件，判断历史公告与新进公告的内容相似度
                                String content = esNotice.getContent();
                                String historyContent = historyNotice.getContent();
                                if (esNotice.getType() == 2) {
                                    // 若为中标公告需去除<table>标签
                                    content = MyStringUtils.excludeStringByKey(content, "<table>", "</table>");
                                    historyContent = MyStringUtils.excludeStringByKey(historyContent, "<table>", "</table>");
                                }
                                content = chineseCompressUtil.getPlainText(content);
                                content = MyStringUtils.deleteHtmlTag(content);
                                content = content.replaceAll(" ", "");
                                content = content.replaceAll("[\\s~·`!！@￥$%^……&*（()）\\-——\\-_=+【\\[\\]】｛{}｝\\|、\\\\；;：:‘'“”\"，,《<。.》>、/？? ]", "");
                                historyContent = chineseCompressUtil.getPlainText(historyContent);
                                historyContent = MyStringUtils.deleteHtmlTag(historyContent);
                                historyContent = historyContent.replaceAll(" ", "");
                                historyContent = historyContent.replaceAll("[\\s~·`!！@￥$%^……&*（()）\\-——\\-_=+【\\[\\]】｛{}｝\\|、\\\\；;：:‘'“”\"，,《<。.》>、/？? ]", "");
                            /* 新进公告内容与历史公告内容相似度对比 */
                                if (ComputeResemble.similarDegreeWrapper(content, historyContent) > 0.2) {
                                    return handleRepeat(esNotice, historyNotice);
                                } else {
                                    // 直接入库
                                    handleNotRepeat(esNotice);
                                    return true;
                                }
                            }
                        }
                    } else {
                        // 分类逻辑
                        resultNotices = noticeFilter(esNotice, resultNotices);
                    }
                } else {
                    // 分类逻辑
                    resultNotices = noticeFilter(esNotice, resultNotices);
                }

                if (resultNotices.isEmpty()) {
                    // 直接入库,更新到es
                    handleNotRepeat(esNotice);
                    return true;
                } else {
                    boolean hasHightLikeNotice = false;
                /* 新进公告内容处理 */
                    String content = esNotice.getContent();
                    content = chineseCompressUtil.getPlainText(content);
                    content = MyStringUtils.deleteHtmlTag(content);
                    content = content.replaceAll(" ", "");
                    content = clearStrOnlyOne(title, content);
                    content = content.replaceAll("[\\s~·`!！@￥$%^……&*（()）\\-——\\-_=+【\\[\\]】｛{}｝\\|、\\\\；;：:‘'“”\"，,《<。.》>、/？? ]", "");
                    EsNotice n = new EsNotice();
                    for (EsNotice no : resultNotices) {
                    /* 历史公告内容处理 */
                        String historyContent = no.getContent();
                        historyContent = chineseCompressUtil.getPlainText(historyContent);
                        historyContent = MyStringUtils.deleteHtmlTag(historyContent);
                        historyContent = historyContent.replaceAll(" ", "");
                        historyContent = clearStrOnlyOne(no.getTitle(), historyContent);
                        historyContent = historyContent.replaceAll("[\\s~·`!！@￥$%^……&*（()）\\-——\\-_=+【\\[\\]】｛{}｝\\|、\\\\；;：:‘'“”\"，,《<。.》>、/？? ]", "");
                        double b = 0.85;
                        if (esNotice.getType() == 2) {
                            // 若为中标公告只需大于20%相似度
                            b = 0.2;
                        }
                    /* 新进公告内容与历史公告内容相似度对比 */
                        if (ComputeResemble.similarDegreeWrapper(content, historyContent) > b) {
                            hasHightLikeNotice = true;
                            n = no;
                            break;
                        }
                    }

                    if (hasHightLikeNotice) {
                        // 公告相似度大于 85%,去重
                        return handleRepeat(esNotice, n);
                    } else {
                        // 直接入库
                        handleNotRepeat(esNotice);
                        return true;
                    }
                }
            } else {
                // 直接入库
                handleNotRepeat(esNotice);
            }
        }catch (Exception e){
            logger.error("湖南去重异常："+e,e);
        }finally {
            logger.info("湖南去重结束:[redisId:"+esNotice.getRedisId()+"][source:"+esNotice.getSource()+"][ur:"+esNotice.getUrl()+"]" + esNotice.getTitle() + esNotice.getOpenDate());
        }

        return true;
    }

}
