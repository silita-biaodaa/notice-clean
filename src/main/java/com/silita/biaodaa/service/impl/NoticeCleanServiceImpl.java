package com.silita.biaodaa.service.impl;

import com.silita.biaodaa.dao.*;
import com.silita.biaodaa.model.SnatchUrl;
import com.silita.biaodaa.model.SnatchurlRepetition;
import com.silita.biaodaa.service.INoticeCleanService;
import com.silita.biaodaa.utils.ChineseCompressUtil;
import com.silita.biaodaa.utils.RouteUtils;
import com.snatch.model.AnalyzeDetail;
import com.snatch.model.AnalyzeDetailZhongBiao;
import com.snatch.model.EsNotice;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.silita.biaodaa.utils.MyStringUtils.reduceString;

@Service("noticeCleanService")
public class NoticeCleanServiceImpl implements INoticeCleanService {

    @Autowired
    private SnatchurlMapper snatchurlMapper;
    @Autowired
    private SnatchurlRepetitionMapper snatchurlRepetitionMapper;
    @Autowired
    private SnatchpressMapper snatchpressMapper;
    @Autowired
    private SnatchurlcontentMapper snatchurlcontentMapper;
    @Autowired
    private ZhaobiaoAnalyzeDetailMapper zhaobiaoAnalyzeDetailMapper;
    @Autowired
    private ZhaobiaoDetailMapper zhaobiaoDetailMapper;
    @Autowired
    private ZhongbiaoAnalyzeDetailMapper zhongbiaoAnalyzeDetailMapper;
    @Autowired
    private ZhongbiaoDetailMapper zhongbiaoDetailMapper;
    @Autowired
    private SnatchUrlCertMapper snatchUrlCertMapper;

    private Logger logger = Logger.getLogger(NoticeCleanServiceImpl.class);

    ChineseCompressUtil chineseCompressUtil = new ChineseCompressUtil();
    SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    /**
     * 根据url判断公告是否存在
     * @param esNotice
     */
    public int countSnastchUrlByUrl(EsNotice esNotice) {
        Map params = new HashMap<String, Object>();
        params.put("url", esNotice.getUrl());
        params.put("openDate", esNotice.getOpenDate());
        params.put("snatchurlTable", RouteUtils.routeTableName("mishu.snatchurl", esNotice));
        List<SnatchUrl> vo = snatchurlMapper.getSnatchUrlCountByUrl(params);
        if (vo != null && vo.size()>0) {
            return 1;
        }
        return 0;
    }

    /**
     * 根据公告标题查询相关公告
     *
     * @param tempTitle
     * @param url
     * @param esNotice
     * @return
     */
    @Override
    public List<EsNotice> listEsNotice(String tempTitle, String url, EsNotice esNotice) {
        Map params = new HashMap<String, Object>();
        params.put("snatchurlTable", RouteUtils.routeTableName("mishu.snatchurl", esNotice));
        params.put("snatchurlContentTable", RouteUtils.routeTableName("mishu.snatchurlcontent", esNotice));
        params.put("title", tempTitle);
        params.put("url", url);
        params.put("city", esNotice.getCity());
        params.put("isShow", esNotice.getIsShow());
        params.put("type", esNotice.getType());
        params.put("openDate", esNotice.getOpenDate());
        List<EsNotice> esNotices = snatchurlMapper.listSnatchUrl(params);
        return esNotices;
    }

    @Override
    public List<EsNotice> listEsNotice(String url, EsNotice esNotice) {
        Map params = new HashMap<String, Object>();
        params.put("snatchurlTable", RouteUtils.routeTableName("mishu.snatchurl", esNotice));
        params.put("snatchurlContentTable", RouteUtils.routeTableName("mishu.snatchurlcontent", esNotice));
        params.put("url", url);
        params.put("city", esNotice.getCity());
        params.put("isShow", esNotice.getIsShow());
        params.put("type", esNotice.getType());
        params.put("openDate", esNotice.getOpenDate());
        List<EsNotice> esNotices = snatchurlMapper.listSnatchUrl(params);
        return esNotices;
    }

    /**
     * 更新公告显示状态
     *
     * @param id
     * @param isShow
     * @param source
     */
    @Override
    public void updateIsShowById(String id, int isShow, String source) {
        Map params = new HashMap<String, Object>();
        params.put("snatchurlTable", RouteUtils.routeTableName("mishu.snatchurl", source));
        params.put("isShow", isShow);
        params.put("id", id);
        snatchurlMapper.updateSnatchUrlById(params);
    }

    /**
     * 添加关联信息
     *
     * @param esNotice
     * @throws ParseException
     */
    @Override
    public void insertSnatchurlRepetition(EsNotice esNotice) {
        SnatchurlRepetition snatchurlRepetition = new SnatchurlRepetition();
        snatchurlRepetition.setNoticeuuid(esNotice.getUuid());
        snatchurlRepetition.setTitle(esNotice.getTitle());
        snatchurlRepetition.setUrl(esNotice.getUrl());
        try {
            snatchurlRepetition.setOpendate(simple.parse(esNotice.getOpenDate()));
        } catch (ParseException e) {
        }
        snatchurlRepetition.setContent(esNotice.getContent());
        snatchurlRepetition.setRank(esNotice.getRank());
        snatchurlRepetition.setRedisid(esNotice.getRedisId());
        snatchurlRepetition.setWebsiteplanid(esNotice.getWebsitePlanId());
        snatchurlRepetition.setReptmethod(0);
        snatchurlRepetition.setSource(esNotice.getSource());
        snatchurlRepetitionMapper.insertSnatchurlRepetition(snatchurlRepetition);
    }

    /**
     * 删除重复
     *
     * @param id
     */
    public void deleteSnatchUrl(String id,String source) {
        Map map = new HashMap();
        map.put("snatchurlTable",RouteUtils.routeTableName("mishu.snatchurl",source));
        map.put("id",id);
        snatchurlMapper.deleteSnatchUrlById(map);
    }

    /**
     * 删除关联并更新编辑明细
     *
     * @param esNotice
     * @param historyNotice
     */
    @Override
    public int deleteRepetitionAndUpdateDetail(EsNotice esNotice, EsNotice historyNotice) {
        snatchurlRepetitionMapper.deleteSnatchurlRepetition(Long.valueOf(historyNotice.getUuid()));
        Map params = new HashMap<String, Object>();
        params.put("snatchurlTable", RouteUtils.routeTableName("mishu.snatchurl", esNotice));
        params.put("url", esNotice.getUrl());
        int noticeId = snatchurlMapper.getSnatchurlIdByUrl(params);
        // 编辑信息修改// 已编辑
        if (historyNotice.getEdit() == 1) {
            Map detailParams;
            if (historyNotice.getType() == 2) {
                // 中标
                detailParams = new HashMap<String, Object>();
                detailParams.put("snatchUrlId", noticeId);
                detailParams.put("historyId", Integer.valueOf(historyNotice.getUuid()));
                zhongbiaoDetailMapper.updateZhongbiaoDetail(detailParams);
            } else {
                //招标
                detailParams = new HashMap<String, Object>();
                detailParams.put("snatchUrlId", noticeId);
                detailParams.put("historyId", Integer.valueOf(historyNotice.getUuid()));
                zhaobiaoDetailMapper.updateZhaobiaoDetail(detailParams);
            }
        }
        return noticeId;
    }

    @Override
    public void insertSnatchUrl(EsNotice esNotice) {
        Map snatchurlParams = new HashMap<String, Object>();
        snatchurlParams.put("snatchurlTable", RouteUtils.routeTableName("mishu.snatchurl", esNotice));
        snatchurlParams.put("url", esNotice.getUrl());
        snatchurlParams.put("title", reduceString(esNotice.getTitle(),100));
        snatchurlParams.put("type", esNotice.getType());
        snatchurlParams.put("openDate", esNotice.getOpenDate());
        snatchurlParams.put("edit", esNotice.getEdit() == null ? 0 : esNotice.getEdit());
        snatchurlParams.put("biddingType", esNotice.getBiddingType() == null ? 0 : Integer.parseInt(esNotice.getBiddingType()));
        snatchurlParams.put("otherType", esNotice.getOtherType() == null ? 0 : Integer.parseInt(esNotice.getOtherType()));
        snatchurlParams.put("tableName", "mishu.snatchurl");
        snatchurlParams.put("province", esNotice.getProvince());
        snatchurlParams.put("city", esNotice.getCity());
        snatchurlParams.put("county", esNotice.getCounty());
        snatchurlParams.put("rank", esNotice.getRank());
        snatchurlParams.put("redisId", esNotice.getRedisId());
        snatchurlParams.put("websitePlanId", esNotice.getWebsitePlanId());
        snatchurlParams.put("uuid", esNotice.getSnatchNumber());
        snatchurlParams.put("businessType", esNotice.getBusinessType());
        snatchurlParams.put("source", esNotice.getSource());
        snatchurlParams.put("isShow", esNotice.getIsShow() == null ? 0 : esNotice.getIsShow());
        //添加公告基本信息
        snatchurlMapper.insertSnatchUrl(snatchurlParams);
        logger.info("########新插入公告：[source:"+esNotice.getSource()+"][isShow:"+esNotice.getIsShow()+"][title:"+esNotice.getTitle()+"][redis:"+esNotice.getRedisId()+"][type:"+esNotice.getType()+"][url:"+esNotice.getUrl()+"]");
    }

    @Override
    public Integer getMaxSnatchUrlIdByUrl(EsNotice esNotice) {
        Map maxIdparams = new HashMap<String, Object>();
        maxIdparams.put("snatchurlTable", RouteUtils.routeTableName("mishu.snatchurl", esNotice));
        maxIdparams.put("url", esNotice.getUrl());
        //获取刚才基本信息Id
        return snatchurlMapper.getMaxIdByUrl(maxIdparams);
    }

    @Override
    public void insertSnatchContent(EsNotice esNotice, Integer snatchUrlId) {
        Map contentParams = new HashMap<String, Object>();
        contentParams.put("snatchUrlContentTable", RouteUtils.routeTableName("mishu.snatchurlcontent", esNotice));
        contentParams.put("content", esNotice.getContent());
        contentParams.put("snatchUrlId", snatchUrlId);
        //添加公告内容
        snatchurlcontentMapper.insertSnatchurlContent(contentParams);
    }

    @Override
    public void insertSnatchPress(EsNotice esNotice, Integer snatchUrlId) {
        Map pressParams = new HashMap<String, Object>();
        String text = chineseCompressUtil.getPlainText(esNotice.getContent());  //
        pressParams.put("snatchpressTable", RouteUtils.routeTableName("mishu.snatchpress",esNotice));
        pressParams.put("press", text);
        pressParams.put("snatchUrlId", snatchUrlId);
        //添加整理后的公告内容
        snatchpressMapper.insertSnatchPress(pressParams);
    }



    /**
     * 添加维度信息
     *
     * @param esNotice
     */
    @Override
    public void insertDetail(EsNotice esNotice) {
        if (esNotice.getType() == 2) {
            //中标
            AnalyzeDetailZhongBiao zhongBiaoAnalyzeDetail = esNotice.getDetailZhongBiao();
            zhongBiaoAnalyzeDetail.setRedisId(Integer.parseInt(esNotice.getUuid()));
            Integer count = zhongbiaoAnalyzeDetailMapper.getZhongBiaoAnalyzeDetailByUrl(zhongBiaoAnalyzeDetail.getNoticeUrl());
            if (count == 0) {
                zhongbiaoAnalyzeDetailMapper.insertZhongBiaoAnalyzeDetail(zhongBiaoAnalyzeDetail);
            }
        } else {
            //招标
            AnalyzeDetail zhaoBiaoAnalyzeDetail = esNotice.getDetail();
            zhaoBiaoAnalyzeDetail.setRedisId(Integer.parseInt(esNotice.getUuid()));
            Integer count = zhaobiaoAnalyzeDetailMapper.getZhaobiaoAnalyzeDetailByUrl(zhaoBiaoAnalyzeDetail.getNoticeUrl());
            if (count == 0) {
                zhaobiaoAnalyzeDetailMapper.insertZhaobiaoAnalyzeDetail(esNotice.getDetail());
            }
        }
    }

    @Override
    public void updateSnatchUrlCert(Integer id, Integer historyId) {
        Map params = new HashMap<String, Object>();
        params.put("id", id);
        params.put("historyId", historyId);
        snatchUrlCertMapper.updateSnatchurlCert(params);
    }

    @Override
    public void updateSnatchUrl(EsNotice esNotice, String uuid) {
        Map params = new HashMap<String, Object>();
        params.put("snatchurlTable", RouteUtils.routeTableName("mishu.snatchurl",esNotice));
        params.put("url", esNotice.getUrl());
        params.put("title", esNotice.getTitle());
        params.put("openDate", esNotice.getOpenDate());
        params.put("province", esNotice.getProvince());
        params.put("city", esNotice.getCity());

        params.put("county", esNotice.getCounty());
        params.put("rank", esNotice.getRank());
        params.put("websitePlanId", esNotice.getWebsitePlanId());
        params.put("uuid", esNotice.getUuid());
        params.put("businessType", esNotice.getBusinessType());
        params.put("otherType", esNotice.getOtherType());
        params.put("redisId", esNotice.getRedisId());
        params.put("source", esNotice.getSource());
        params.put("id", uuid);
        snatchurlMapper.updateSnatchUrl(params);
    }

    @Override
    public void updateSnatchurlContent(EsNotice esNotice) {
        Map params = new HashMap<String, Object>();
        params.put("snatchUrlContentTable", RouteUtils.routeTableName("mishu.snatchurlcontent",esNotice));
        params.put("content", esNotice.getContent());
        params.put("snatchUrlId", esNotice.getUuid());
        snatchurlcontentMapper.updateSnatchurlContent(params);
    }

    @Override
    public void updateSnatchpress(EsNotice esNotice) {
        Map params = new HashMap<String, Object>();
        params.put("snatchpressTable", RouteUtils.routeTableName("mishu.snatchpress",esNotice));
        params.put("press", chineseCompressUtil.getPlainText(esNotice.getContent()));
        params.put("snatchUrlId", esNotice.getUuid());
        snatchpressMapper.updateSnatchpress(params);
    }

}
