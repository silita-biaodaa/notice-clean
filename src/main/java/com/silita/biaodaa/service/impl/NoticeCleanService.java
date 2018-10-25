package com.silita.biaodaa.service.impl;

import com.silita.biaodaa.common.Constant;
import com.silita.biaodaa.common.elastic.indexes.IdxZhaobiaoSnatch;
import com.silita.biaodaa.common.elastic.indexes.IdxZhongbiaoSnatch;
import com.silita.biaodaa.common.redis.RedisClear;
import com.silita.biaodaa.dao.*;
import com.silita.biaodaa.dao_temp.SnatchNoticeHuNanDao;
import com.silita.biaodaa.model.SnatchUrl;
import com.silita.biaodaa.model.SnatchurlRepetition;
import com.silita.biaodaa.rules.exception.MyRetryException;
import com.silita.biaodaa.utils.ChineseCompressUtil;
import com.silita.biaodaa.utils.RouteUtils;
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
public class NoticeCleanService {

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

    @Autowired
    protected SnatchNoticeHuNanDao snatchNoticeHuNanDao;

    private Logger logger = Logger.getLogger(NoticeCleanService.class);

    ChineseCompressUtil chineseCompressUtil = new ChineseCompressUtil();
    SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd");

    
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
    public void insertSnatchurlRepetition(EsNotice esNotice) {
        try {
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
        }catch(Exception e ){
            logger.error("[reidsId:"+esNotice.getRedisId()+"][title:"+esNotice.getTitle()+"][url:"+esNotice.getUrl()+"]"+e,e);
        }
    }

    /**
     * 删除重复
     *
     * @param id
     */
    public int deleteSnatchUrl(String id,String source,int redisId) {
        Map map = new HashMap();
        map.put("snatchurlTable",RouteUtils.routeTableName("mishu.snatchurl",source));
        map.put("id",id);
        map.put("redisId",redisId);
        return snatchurlMapper.deleteSnatchUrlById(map);
    }

    /**
     * 删除公告的关联信息
     * @param esNotice
     */
    public void deleteSnatchrelation( EsNotice esNotice){
        snatchurlRepetitionMapper.deleteSnatchrelation(Long.valueOf(esNotice.getUuid()));
    }

    /**
     * 删除关联
     *
     * @param esNotice
     * @param historyNotice
     */
    public int deleteRepetitionAndUpdateDetail(EsNotice esNotice, EsNotice historyNotice) {
        int delCount =  snatchurlRepetitionMapper.deleteSnatchrelation(Long.valueOf(historyNotice.getUuid()));
//        Map params = new HashMap<String, Object>();
//        params.put("snatchurlTable", RouteUtils.routeTableName("mishu.snatchurl", esNotice));
//        params.put("url", esNotice.getUrl());
//        int noticeId = snatchurlMapper.getSnatchurlIdByUrl(params);
        //更新编辑信息逻辑废除
//        // 编辑信息修改// 已编辑
//        if (historyNotice.getEdit() == 1) {
//            Map detailParams;
//            if (historyNotice.getType() == 2) {
//                // 中标
//                detailParams = new HashMap<String, Object>();
//                detailParams.put("snatchUrlId", noticeId);
//                detailParams.put("historyId", Integer.valueOf(historyNotice.getUuid()));
//                zhongbiaoDetailMapper.updateZhongbiaoDetail(detailParams);
//            } else {
//                //招标
//                detailParams = new HashMap<String, Object>();
//                detailParams.put("snatchUrlId", noticeId);
//                detailParams.put("historyId", Integer.valueOf(historyNotice.getUuid()));
//                zhaobiaoDetailMapper.updateZhaobiaoDetail(detailParams);
//            }
//        }
        return delCount;
    }

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

    public Integer getMaxSnatchUrlIdByUrl(EsNotice esNotice) {
        Map maxIdparams = new HashMap<String, Object>();
        maxIdparams.put("snatchurlTable", RouteUtils.routeTableName("mishu.snatchurl", esNotice));
        maxIdparams.put("url", esNotice.getUrl());
        //获取刚才基本信息Id
        return snatchurlMapper.getMaxIdByUrl(maxIdparams);
    }

    public void insertSnatchContent(EsNotice esNotice, Integer snatchUrlId) {
        Map contentParams = new HashMap<String, Object>();
        contentParams.put("snatchUrlContentTable", RouteUtils.routeTableName("mishu.snatchurlcontent", esNotice));
        contentParams.put("content", esNotice.getContent());
        contentParams.put("snatchUrlId", snatchUrlId);
        //添加公告内容
        snatchurlcontentMapper.insertSnatchurlContent(contentParams);
    }

    public void insertSnatchPress(EsNotice esNotice, Integer snatchUrlId) {
        Map pressParams = new HashMap<String, Object>();
        pressParams.put("snatchpressTable", RouteUtils.routeTableName("mishu.snatchpress",esNotice));
        pressParams.put("press", esNotice.getPressContent());
        pressParams.put("snatchUrlId", snatchUrlId);
        //添加整理后的公告内容
        snatchpressMapper.insertSnatchPress(pressParams);
    }

    public void updateSnatchUrlCert(Integer id, Integer historyId) {
        Map params = new HashMap<String, Object>();
        params.put("id", id);
        params.put("historyId", historyId);
        snatchUrlCertMapper.updateSnatchurlCert(params);
    }

    public int updateSnatchUrl(EsNotice esNotice,EsNotice historyNotice) {
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
        params.put("id", historyNotice.getUuid());
        params.put("hisRedisId",historyNotice.getRedisId());
        return snatchurlMapper.updateSnatchUrl(params);
    }

    public int updateSnatchurlContent(EsNotice esNotice) {
        Map params = new HashMap<String, Object>();
        params.put("snatchUrlContentTable", RouteUtils.routeTableName("mishu.snatchurlcontent",esNotice));
        params.put("content", esNotice.getContent());
        params.put("snatchUrlId", esNotice.getUuid());
        return snatchurlcontentMapper.updateSnatchurlContent(params);
    }

    public int updateSnatchpress(EsNotice esNotice) {
        Map params = new HashMap<String, Object>();
        params.put("snatchpressTable", RouteUtils.routeTableName("mishu.snatchpress",esNotice));
        params.put("press", esNotice.getPressContent());
        params.put("snatchUrlId", esNotice.getUuid());
        return snatchpressMapper.updateSnatchpress(params);
    }

    @Autowired
    protected RedisClear redisClear;

    /**
     * 新进公告替换历史公告，保留历史公告进入去重表
     * @param notice
     * @param historyNotice
     * @return
     */
    public boolean replaceHistoryNotice(EsNotice notice, EsNotice historyNotice)throws Exception{
        notice.setUuid(historyNotice.getUuid());
        notice.setEdit(historyNotice.getEdit());
        //更新基本表
        int baseMatch = updateSnatchUrl(notice,historyNotice);
        if(baseMatch !=1){
            throw new MyRetryException("更新基本表失败[hisRedisid:"+historyNotice.getRedisId()+"][his.title:"+historyNotice.getTitle()+"][his.source:"+historyNotice.getSource()+"]");
        }
        //更新内容，压缩内容
        int contentMatch = updateSnatchurlContent(notice);
        int pressMatch = updateSnatchpress(notice);
        logger.info("新进公告替换历史公告.[hisRedisid:"+historyNotice.getRedisId()+"][contentMatch:"+contentMatch+"][pressMatch:"+pressMatch+"]");

        try {
            //保留历史公告进入去重表
            insertSnatchurlRepetition(historyNotice);
            //仅湖南数据处理es,清洗缓存等
            if (notice.getSource().equals(Constant.HUNAN_SOURCE)) {
                // 历史公告关联信息删除，更新资质
                deleteRepetitionAndUpdateDetail(notice, historyNotice);
                //清理页面缓存
                redisClear.clearGonggaoRelation(historyNotice.getUuid());
                redisClear.clearRepeatNotice(historyNotice.getUuid());

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
            }
        }catch(Exception e ){
            logger.error("[reidsId:"+notice.getRedisId()+"][title:"+notice.getTitle()+"][url:"+notice.getUrl()+"]"+e,e);
        }
        logger.info("###新公告替换历史公告 ..[reidsId:"+notice.getRedisId()+"][title:"+notice.getTitle()+"]  历史公告 : [his.reidsId:"+historyNotice.getRedisId()+"][his.title:"+historyNotice.getTitle()+"] ");
        return true;
    }

    /**
     * 历史公告关联信息删除、编辑信息更改
     *
     * @param notice
     * @param historyNotice
     */
    public void delRelationInfoAndEditDetail(EsNotice notice, EsNotice historyNotice) {

    }

    /**
     * 历史公告删除
     * @param historyNotice
     * @return
     */
    public boolean delHistoryNotice( EsNotice historyNotice)throws MyRetryException{
        try {
            int match = deleteSnatchUrl(historyNotice.getUuid(), historyNotice.getSource(),historyNotice.getRedisId());
            if(match==1) {
                insertSnatchurlRepetition(historyNotice);
                if (historyNotice.getSource().equals(Constant.HUNAN_SOURCE)) {
                    // 历史公告关联信息删除
                    delRelationInfos(historyNotice);
                    // 删除es上的历史公告索引
                    if (historyNotice.getType() == 2) {
                        // 删除中标公告索引
                        snatchNoticeHuNanDao.deleteIndexById(IdxZhongbiaoSnatch.class, historyNotice.getUuid());
                    } else {
                        // 删除招标公告索引
                        snatchNoticeHuNanDao.deleteIndexById(IdxZhaobiaoSnatch.class, historyNotice.getUuid());
                    }
                }
                logger.info("###  历史公告被去重 .. title：" + historyNotice.getTitle() + "  ###");
            }else{
                throw new MyRetryException("历史公告删除失败[hisRedisid:"+historyNotice.getRedisId()+"][his.title:"+historyNotice.getTitle()+"][his.source:"+historyNotice.getSource()+"]");
            }
        }catch (MyRetryException rt) {
            throw rt;
        }catch (Exception e ){
            logger.error("[reidsId:"+historyNotice.getRedisId()+"][title:"+historyNotice.getTitle()+"][url:"+historyNotice.getUrl()+"]"+e,e);
        }
        return true;
    }

    /**
     * 删除公告的关联信息
     * @param esNotice
     */
    public void delRelationInfos(EsNotice esNotice){
        redisClear.clearGonggaoRelation(esNotice.getUuid());   //清理公告关联信息缓存
        deleteSnatchrelation(esNotice);
    }

}
