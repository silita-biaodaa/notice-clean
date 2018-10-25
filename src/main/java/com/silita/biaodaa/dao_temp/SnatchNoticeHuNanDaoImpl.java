package com.silita.biaodaa.dao_temp;

import com.alibaba.fastjson.JSON;
import com.silita.biaodaa.common.elastic.ElaticsearchUtils;
import com.silita.biaodaa.common.elastic.indexes.IdxZhaobiaoSnatch;
import com.silita.biaodaa.common.elastic.indexes.IdxZhongbiaoSnatch;
import com.silita.biaodaa.common.elastic.model.ElasticEntity;
import com.silita.biaodaa.common.jdbc.JdbcBase;
import com.silita.biaodaa.common.redis.RedisClear;
import com.silita.biaodaa.disruptor.DisruptorOperator;
import com.silita.biaodaa.service.SnatchService;
import com.silita.biaodaa.utils.*;
import com.snatch.model.AnalyzeDetail;
import com.snatch.model.AnalyzeDetailZhongBiao;
import com.snatch.model.EsNotice;
import org.apache.commons.collections.BeanMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 湖南数据整合专用
 *
 * @author Administrator
 */
@Repository
@Deprecated
public class SnatchNoticeHuNanDaoImpl extends JdbcBase implements SnatchNoticeHuNanDao {

    @Autowired
    private Client client;

    @Autowired
    SnatchService service;

    @Autowired
    SnatchDao snatchDao;

    @Autowired
    private DisruptorOperator disruptorOperator;

    ChineseCompressUtil chineseCompressUtil = new ChineseCompressUtil();


    private Logger logger = Logger.getLogger(SnatchNoticeHuNanDaoImpl.class);

    @Autowired
    private RedisClear redisClear;

    @Autowired
    private ElaticsearchUtils elaticsearchUtils;

    private String[] mainWebside = {"ggzyjy.xxz.gov.cn", "ggzy.huaihua.gov.cn", "ggzy.yzcity.gov.cn", "czggzy.czs.gov.cn", "ggzy.yueyang.gov.cn", "sysggzy.com", "ggzy.xiangtan.gov.cn", "hyggzyjy.hengyang.gov.cn", "zzzyjy.cn", "ggzy.changde.gov.cn", "csx.gov.cn", "liuyang.gov.cn", "wangcheng.gov.cn", "61.186.94.156", "csggzy.gov.cn", "zjjsggzy.gov.cn", "ldggzy.hnloudi.gov.cn", "bidding.hunan.gov.cn", "jyzx.yiyang.gov.cn"};
    private static String bidding = "bidding.hunan.gov.cn";
    private static String czggzy = "czggzy.czs.gov.cn";
    /**
     * 插公告表
     */
    public synchronized void insertNewUrl(EsNotice n) {
        this.getJdbcTemplate().update("INSERT INTO "+ RouteUtils.routeTableName("mishu.snatchurl",n)+"(" +
                        "url,title, snatchDatetime, snatchPlanId," +
                        "`type`,`status`,openDate," +
                        "`range`,edit,randomNum,biddingType,otherType," +
                        "tableName,suuid," +
                        "province,city,county,rank," +
                        "redisId,websitePlanId,uuid,businessType,source,isShow)VALUES(" +
                        "?,?,NOW(),?," +
                        "?,0,DATE_FORMAT(?,'%Y-%m-%d')," +
                        "YEAR(?),?,?,?,?," +
                        "'mishu.snatchurl',REPLACE(UUID(),'-','')," +
                        "?,?,?,?," +
                        "?,?,?,?,?,?)",
                n.getUrl(), n.getTitle(), (long) 2, n.getType(),
                n.getOpenDate(),n.getOpenDate(),
                n.getEdit() == null?0:n.getEdit(),
                0,
                n.getBiddingType()==null ? 0 : Integer.parseInt(n.getBiddingType()),
                n.getOtherType()==null ? 0 : Integer.parseInt(n.getOtherType()),
                n.getProvince(),
                n.getCity(), n.getCounty(), n.getRank(), n.getRedisId(),
                n.getWebsitePlanId(), n.getSnatchNumber(), n.getBusinessType(),n.getSource(),n.getIsShow()==null ? 0:n.getIsShow());
        logger.info("########新插入公告：[source:"+n.getSource()+"][isShow:"+n.getIsShow()+"][title:"+n.getTitle()+"][redis:"+n.getRedisId()+"][type:"+n.getType()+"][url:"+n.getUrl()+"]");
    }

    /**
     * 插入整理后的公告内容表
     *
     * @param n
     * @param snatchUrlId
     */
    @Override
    public void insertCompress(EsNotice n, Long snatchUrlId) {
        String text = chineseCompressUtil.getPlainText(n.getContent());
        this.getJdbcTemplate().update("INSERT INTO "+RouteUtils.routeTableName("mishu.snatchpress",n)+" (press, snatchUrlId)VALUES(?,?)", text, snatchUrlId);
    }

    /**
     * 公告插数据库
     *
     * @param n
     * @return
     */
    @Override
    public Map<String, String> insertNotice(EsNotice n) {
        insertNewUrl(n);
        int id = this.queryForInt("select max(id) from "+ RouteUtils.routeTableName("mishu.snatchurl",n)+" where url=?", new Object[]{n.getUrl()});
        insertSnatchContent(id,n);
        insertCompress(n, Long.valueOf((id)));//插入整理后的文档,去掉内容中有重复标题
        updateSnatchurlStatus(id,n.getSource());

        Map<String, String> map = new HashMap<String, String>();    //用于ES更新
        map.put("id", String.valueOf(id));
        map.put("otherType", n.getOtherType());
        map.put("biddingType", n.getBiddingType());
        return map;
    }

    @Override
    public void insertSnatchContent (int id,EsNotice n ){
        this.getJdbcTemplate().update("INSERT INTO "+RouteUtils.routeTableName("mishu.snatchurlcontent",n)+" (content, snatchUrlId)VALUES(?,?)", n.getContent(), id);
    }

    @Override
    public void updateSnatchurlStatus (int id,String source) {
        this.getJdbcTemplate().update("update "+RouteUtils.routeTableName("mishu.snatchurl",source)+" set status=1 where id=?", id);
    }

    @Override
    public void updateSnatchurlisShow (String id,int isShow,String source) {
        this.getJdbcTemplate().update("UPDATE "+RouteUtils.routeTableName("mishu.snatchurl",source)+" SET isShow = ? WHERE id = ? ",isShow,id);
    }

    @Override
    public void updateSnatchurlNotice (EsNotice notice, String uuid) {
        this.getJdbcTemplate().update("update "+RouteUtils.routeTableName("mishu.snatchurl",notice)+" set " +
                        "url=?,title=?,openDate=?,province=?," +
                        "city=?,county=?,rank=?,websitePlanId=?,uuid=?,businessType=?," +
                        "changeNum=changeNum+1,otherType=?,snatchDateTime=NOW(),redisId=?,source=? where id =? ",
                notice.getUrl(), notice.getTitle(), notice.getOpenDate(), notice.getProvince(),
                notice.getCity(), notice.getCounty(), notice.getRank(), notice.getWebsitePlanId(), notice.getSnatchNumber(), notice.getBusinessType(),
                notice.getOtherType(), notice.getRedisId(),notice.getIsShow(), uuid);
    }

    @Override
    public void updateSnatchContent (EsNotice notice) {
        this.getJdbcTemplate().update("update "+RouteUtils.routeTableName("mishu.snatchurlcontent",notice)
                +" set content =? where snatchUrlId = ?", notice.getContent(), notice.getUuid());
    }

    @Override
    public void updateSnatchPress (EsNotice notice) {
        String press = chineseCompressUtil.getPlainText(notice.getContent());
        this.getJdbcTemplate().update("update "+RouteUtils.routeTableName("mishu.snatchpress",notice)
                +" set press =? where snatchUrlId = ?", press, notice.getUuid());
    }

    @Override
    public void deleteIndexById (Class<? extends ElasticEntity> clazz, String id) {
        elaticsearchUtils.deleteById(clazz,id);
    }

    @Override
    public void deleteRelationInfo (Integer relationId) {
        this.getJdbcTemplate().update("DELETE FROM  mishu.snatchrelation WHERE mainId = ? OR nextId = ?", relationId,relationId);
    }

    @Override
    public void updateSnatchurlEdit (Integer id) {
        this.getJdbcTemplate().update("UPDATE mishu.snatchurl SET edit = 1 WHERE id = ? ", id);
    }

    @Override
    public void editZhaobiaoDetail (Integer id,Integer historyId) {
        this.getJdbcTemplate().update("UPDATE mishu.zhaobiao_detail SET snatchUrlId = ? WHERE snatchUrlId = ? ", id,historyId);
    }

    @Override
    public void editZhongbiaoDetail (Integer id,Integer historyId) {
        this.getJdbcTemplate().update("UPDATE mishu.zhongbiao_detail SET snatchUrlId = ? WHERE snatchUrlId = ? ", id,historyId);
    }


    /**
     * 插入重复记录表
     *
     * @param n
     * @return
     */
    @Override
    public synchronized void insertNoticeRepetition(EsNotice n) {
        this.getJdbcTemplate().update("INSERT INTO mishu.snatchurl_repetition (" +
                        "noticeUuid, title, url, openDate, " +
                        "content, rank, redisId, websitePlanId,reptMethod,source)VALUES(" +
                        "?,?,?,?," +
                        "?,?,?,?,0,?)", n.getUuid(), n.getTitle(), n.getUrl(), n.getOpenDate(),
                n.getContent(), n.getRank(), n.getRedisId(), n.getWebsitePlanId(),n.getSource());
    }

    @Override
    public void deleteSnatchUrlById (String id) {
        String sql = "DELETE FROM mishu.snatchurl WHERE id = ?";
        this.getJdbcTemplate().update(sql,id);
    }

    /**
     * 删除关联公告信息
     * @param id
     */
    public synchronized void deleteRelationInfo (Long id) {
        String sql = "DELETE FROM mishu.snatchrelation WHERE mainId = ? OR nextId = ?";
        this.getJdbcTemplate().update(sql,id,id);
    }

    /**
     * 根据标题查询公告
     *
     * @return
     */
    public synchronized EsNotice queryNoticeByTitle(String title, String openDate, int type) {
        String sql = "SELECT a.id uuid,a.title,a.url,a.openDate," +
                "ifnull(a.province,'') province,ifnull(a.city,'') city,ifnull(a.county,'') county,ifnull(type,0) type," +
                "ifnull(rank,0) rank,ifnull(redisId,0) redisId,ifnull(websitePlanId,0) websitePlanId," +
                "ifnull(tableName,'') tableName,b.content " +
                "FROM mishu.snatchurl a,mishu.snatchurlcontent b " +
                "WHERE a.id=b.snatchUrlId AND a.title like ? " +
                "AND a.openDate BETWEEN DATE_SUB(?,INTERVAL 3 DAY) AND DATE_SUB(?,INTERVAL -3 DAY) " +
                "AND a.type=?";
        List<Map<String, Object>> list = this.getJdbcTemplate().queryForList(sql, title, openDate, openDate, type);
        //取第一条
        if (list.size() > 0) {
            Map<String, Object> m = list.get(0);
            EsNotice obj = new EsNotice();
            obj.setUuid(String.valueOf(m.get("uuid")));
            obj.setTitle(String.valueOf(m.get("title")));
            obj.setUrl(String.valueOf(m.get("url")));
            obj.setOpenDate(String.valueOf(m.get("openDate")));
            obj.setContent(String.valueOf(m.get("content")));
            obj.setProvince(String.valueOf(m.get("province")));
            obj.setCity(String.valueOf(m.get("city")));
            obj.setCounty(String.valueOf(m.get("county")));
            obj.setType(Integer.valueOf(String.valueOf(m.get("type"))));
            obj.setRank(Integer.valueOf(String.valueOf(m.get("rank"))));
            obj.setRedisId(Integer.valueOf(String.valueOf(m.get("redisId"))));
            obj.setWebsitePlanId(Integer.valueOf(String.valueOf(m.get("websitePlanId"))));
            obj.setTableName(String.valueOf(m.get("tableName")));
            return obj;
        } else {
            return null;
        }
    }

    /**
     * 根据标题查询公告(查询公示时间前后3天的)
     *
     * @param openDate
     * @param type
     * @return
     */
    public synchronized List<EsNotice> queryNoticeList(String openDate, int type, String otherType, String city) {
        String sql = "SELECT a.id uuid,a.title,a.url,a.openDate," +
                "ifnull(a.province,'') province,ifnull(a.city,'') city,ifnull(a.county,'') county,ifnull(type,0) type," +
                "ifnull(rank,0) rank,ifnull(redisId,0) redisId,ifnull(websitePlanId,0) websitePlanId," +
                "ifnull(tableName,'') tableName,b.content,a.otherType,a.uuid snatchNumber,a.biddingType,a.businessType,a.edit edit " +
                "FROM mishu.snatchurl a,mishu.snatchurlcontent b " +
                "WHERE a.id=b.snatchUrlId AND a.isShow = 0 AND a.type=? #otherType #city " +
                "AND a.openDate BETWEEN DATE_SUB(?,INTERVAL 3 DAY) AND DATE_SUB(?,INTERVAL -3 DAY) ";

        List<Map<String, Object>> list = new ArrayList<>();

        boolean otherTypeBool = false;
        boolean cityBool = false;
        if (StringUtils.isNotBlank(otherType) && !otherType.equals("0"))
            otherTypeBool = true;
        if (StringUtils.isNotBlank(city))
            cityBool = true;

        if (otherTypeBool && cityBool) {
            sql = sql.replace("#otherType", "AND a.otherType=? ");
            sql = sql.replace("#city", "AND a.city=? ");
            list = this.getJdbcTemplate().queryForList(sql, type, otherType, city, openDate, openDate);
        } else if (otherTypeBool && !cityBool) {
            sql = sql.replace("#otherType", "AND a.otherType=? ");
            sql = sql.replace("#city", " ");
            list = this.getJdbcTemplate().queryForList(sql, type, otherType, openDate, openDate);
        } else if (!otherTypeBool && cityBool) {
            sql = sql.replace("#otherType", " ");
            sql = sql.replace("#city", "AND a.city=? ");
            list = this.getJdbcTemplate().queryForList(sql, type, city, openDate, openDate);
        } else {
            sql = sql.replace("#otherType", " ");
            sql = sql.replace("#city", " ");
            list = this.getJdbcTemplate().queryForList(sql, type, openDate, openDate);
        }

        List<EsNotice> noticeList = new ArrayList<EsNotice>();
        if (list.size() > 0) {
            for (Map<String, Object> map : list) {
                EsNotice obj = new EsNotice();
                obj.setUuid(String.valueOf(map.get("uuid")));
                obj.setTitle(String.valueOf(map.get("title")));
                obj.setUrl(String.valueOf(map.get("url")));
                obj.setOpenDate(String.valueOf(map.get("openDate")));
                obj.setContent(String.valueOf(map.get("content")));
                obj.setProvince(String.valueOf(map.get("province")));
                obj.setCity(String.valueOf(map.get("city")));
                obj.setCounty(String.valueOf(map.get("county")));
                obj.setType(Integer.valueOf(String.valueOf(map.get("type"))));
                obj.setRank(Integer.valueOf(String.valueOf(map.get("rank"))));
                obj.setRedisId(Integer.valueOf(String.valueOf(map.get("redisId"))));
                obj.setWebsitePlanId(Integer.valueOf(String.valueOf(map.get("websitePlanId"))));
                obj.setTableName(String.valueOf(map.get("tableName")));
                obj.setOtherType(String.valueOf(map.get("otherType")));
                obj.setSnatchNumber(String.valueOf(map.get("snatchNumber")));
                obj.setBiddingType(String.valueOf(map.get("biddingType")));
                obj.setBusinessType(String.valueOf(map.get("businessType") == null ? "" : map.get("businessType")));
                obj.setEdit(map.get("edit") == null ? 0 : Integer.parseInt(String.valueOf(map.get("edit"))));
                noticeList.add(obj);
            }
        }
        return noticeList;
    }


    /**
     * 把接收的解析详情插入到解析维度临时表（临时表只保存维度匹配不重复的记录）
     *
     * @param ad
     * @return
     */
    @Override
    public int insertOrUpdateAnalyzeDetail(AnalyzeDetail ad) {
        int flag = -1;
        Boolean exist = existNoticeUrl(ad);
        if (exist == false) {
            flag = insertZhaobiaoAnalyzeDetail(ad);
        }
        return flag;
    }

    /**
     * 把接收的解析详情插入到解析维度临时表（临时表只保存维度匹配不重复的记录）
     *
     * @param ad
     * @return
     */
    @Override
    public int insertOrUpdateAnalyzeDetailZhongBiao(AnalyzeDetailZhongBiao ad) {
        int flag = -1;
        Boolean exist = existNoticeUrlZhongBiao(ad);
        if (exist == false) {
            flag = insertZhongBiaoAnalyzeDetail(ad);
        }
        return flag;
    }

//    /**
//     * 湖南公告入库
//     */
//    @Override
//    public boolean insertEsNotice(EsNotice notice) {
//
//        logger.info("新公告title：" + notice.getTitle());
//
//        //url判断（已存在不入库）
//        String CountNoticeUrlSql = "SELECT COUNT(*) FROM mishu.snatchurl WHERE url = ? and openDate = ?";
//        int NoticeCount = this.queryForInt(CountNoticeUrlSql, new Object[]{notice.getUrl(), notice.getOpenDate()});
//        if (NoticeCount != 0) {
//            logger.info("####数据库中已存在相同url:" + notice.getUrl() + "####");
//            return false;
//        }
//        notice.setTitle(notice.getTitle().trim());    //去title首尾空格
//
//        String snatchNumber = notice.getSnatchNumber();
//        if (StringUtils.isBlank(snatchNumber)) {
//            notice.setSnatchNumber("");
//        }
//        String businessType = notice.getBusinessType();
//        if (StringUtils.isBlank(businessType)) {
//            notice.setBusinessType("");
//        }
//
//        if (StringUtils.isNotBlank(businessType) && businessType.equals("0")) {
//            businessType = "3";
//        }else {
//            //判断公告是什么类型
//            if (notice.getTitle().indexOf("设计") != -1) {
//                businessType = "1";
//            } else if (notice.getTitle().indexOf("监理") != -1) {
//                businessType = "2";
//            } else if (notice.getTitle().indexOf("采购") != -1 || notice.getTitle().indexOf("谈判") != -1 || notice.getTitle().indexOf("磋商") != -1) {
//                businessType = "3";
//            } else if (notice.getTitle().indexOf("勘察") != -1) {
//                businessType = "4";
//            } else if (notice.getTitle().indexOf("检测") != -1) {
//                businessType = "5";
//            } else {
//                businessType = "0";
//            }
//        }
//        notice.setBiddingType(businessType);
//        // type属性分离为2个字段（type otherType）
//        int type = notice.getType();
//        int otherType = 0;
//        if (type < 10) {
//            if (notice.getTitle().indexOf("补充") != -1) {
//                otherType = 1;
//            } else if (notice.getTitle().indexOf("答疑") != -1) {
//                otherType = 2;
//            } else if (notice.getTitle().indexOf("流标") != -1) {
//                otherType = 3;
//            } else if (notice.getTitle().indexOf("澄清") != -1) {
//                otherType = 4;
//            } else if (notice.getTitle().indexOf("延期") != -1) {
//                otherType = 5;
//            } else if (notice.getTitle().indexOf("更正公告") != -1) {
//                otherType = 6;
//            } else if (notice.getTitle().indexOf("废标") != -1 && notice.getTitle().indexOf("终止") != -1) {
//                otherType = 7;
//            } else if (notice.getTitle().indexOf("终止") != -1) {
//                otherType = 8;
//            }
//        } else if (type == 11) {
//            otherType = 1;
//        } else if (type == 12) {
//            otherType = 2;
//        } else if (type == 13) {
//            otherType = 3;
//        } else if (type == 14) {
//            otherType = 4;
//        } else if (type == 15) {
//            otherType = 5;
//        } else if (type == 16) {
//            otherType = 6;
//        } else if (type == 17) {
//            otherType = 7;
//        } else if (type == 18) {
//            otherType = 8;
//        } else if (type == 19) {
//            otherType = 9;
//        } else if (type == 20) {
//            otherType = 10;
//        } else if (type == 21) {
//            otherType = 11;
//        } else if (type == 22) {
//            otherType = 12;
//        } else if (type == 23) {
//            otherType = 13;
//        } else if (type == 24) {
//            otherType = 14;
//        } else {
//            otherType = type;
//        }
//        notice.setOtherType(String.valueOf(otherType));
//        notice.setType(RuleUtils.noticeTypeAdapter(notice));
//
//        String areaRank = notice.getAreaRank();
//        if (StringUtils.isBlank(areaRank) || areaRank.equals("___")) {
//            //查询湖南的抓取的网站和当前url做对比。获取网站等级
//            List<Map<String, Object>> webList = service.querysWebSitePlan(notice.getTableName().replaceAll("mishu.", ""));
//            Integer rank = 0;
//            for (Map<String, Object> wm : webList) {
//                if (notice.getUrl().indexOf(String.valueOf(wm.get("url"))) > -1) {
//                    rank = Integer.valueOf(String.valueOf(wm.get("rank")));
//                    notice.setWebsitePlanId(Integer.valueOf(String.valueOf(wm.get("id"))));
//                    break;
//                }
//            }
//            notice.setRank(rank);
//        } else {
//            notice.setRank(Integer.parseInt(areaRank));
//        }
//        if (notice.getWebsitePlanId() == null) {
//            notice.setWebsitePlanId(0);
//        }
//
//        if (notice.getType() == 2) {
//            AnalyzeDetailZhongBiao detailZhongBiao = notice.getDetailZhongBiao();
//            if (detailZhongBiao == null){
//                detailZhongBiao = new AnalyzeDetailZhongBiao();
//            }
//            detailZhongBiao.setGsDate(notice.getOpenDate());
//            notice.setDetailZhongBiao(detailZhongBiao);
//        } else {
//            AnalyzeDetail detail = notice.getDetail();
//            if (detail == null) {
//                detail = new AnalyzeDetail();
//            }
//            detail.setGsDate(notice.getOpenDate());
//            notice.setDetail(detail);
//        }
//
//
//        LoggerUtils.showJVM("去重规则开始");
//        //抓取批次不为空进行新去重判断
//        if (StringUtils.isNotBlank(notice.getSnatchNumber())) {
//            // 使用新增公告的标题和公示时间进行查询
//            String tempOtherType = checkMainWebSide(notice.getUrl()) ? "" : notice.getOtherType();
//            List<EsNotice> noticeList = new ArrayList<>();
//            String titleCond = notice.getTitle();
//            titleCond = notice.getType() == 2 ? MyStringUtils.subZhongBiaoTile(titleCond) : MyStringUtils.subZhaobiaoTile(titleCond);
//            titleCond = MyStringUtils.trimInnerSpaceStr(titleCond);    //去title首尾空格
//            if (StringUtils.isNotBlank(titleCond)) {//过滤关键字后为空串的直接插入
//                long repStartTime = System.currentTimeMillis(); // 去重开始时间
//                noticeList = this.queryNoticeList(notice.getOpenDate(), notice.getType(), tempOtherType, notice.getCity());//根据标题查询公示时间前后3天的
//                logger.info("##### 查询消耗时间：" + (System.currentTimeMillis() - repStartTime) + " ms #####");
//            }
//
//            //只获取标题相似度高的公告
//            if (!noticeList.isEmpty()) {
//                List<EsNotice> repeatNotices = new ArrayList<>();
//                String newTitle = notice.getTitle();
//                newTitle = notice.getType() == 2 ? MyStringUtils.subZhongBiaoTile(newTitle) : MyStringUtils.subZhaobiaoTile(newTitle);
//                String newContent = chineseCompressUtil.getPlainText(notice.getContent());
//                newContent = MyStringUtils.deleteHtmlTag(newContent);
//                newContent = newContent.replaceAll(" ", "");
//                String tempKeyWord = "标段";
//                for (EsNotice tempNotice : noticeList) {
//                    String tempTitle = tempNotice.getTitle();
//                    tempTitle = tempNotice.getType() == 2 ? MyStringUtils.subZhongBiaoTile(tempTitle) : MyStringUtils.subZhaobiaoTile(tempTitle);
//                    String tempContent = chineseCompressUtil.getPlainText(tempNotice.getContent());
//                    tempContent = MyStringUtils.deleteHtmlTag(tempContent);
//                    tempContent = tempContent.replaceAll(" ", "");
//                    if (newTitle.equals(tempTitle)) {
//                        logger.debug("标题完全一样");
//                        if (hasCzAndHnUrl(tempNotice.getUrl(),notice.getUrl())) {
//                            // 郴州与省网去重
//                            if ((tempNotice.getUrl().contains(czggzy) || notice.getUrl().contains(czggzy)) && ComputeResemble.similarDegreeWrapper(newContent, tempContent) < 0.2) {
//                                if (tempContent.length() < 100 || newContent.length() < 100) {
//                                    repeatNotices.add(tempNotice);
//                                    break;
//                                }
//                            } else {
//                                repeatNotices.add(tempNotice);
//                                break;
//                            }
//                        } else if (ComputeResemble.similarDegreeWrapper(newContent, tempContent) > 0.2) {
//                            logger.debug("详情似度大于20%，应该去重。。");
//                            //存在标题一致的优先去重
//                            repeatNotices.add(tempNotice);
//                            break;
//                        }
//                    } else if (!tempTitle.contains(tempKeyWord) && ComputeResemble.similarDegreeWrapper(newTitle, tempTitle) > 0.8) {
//                        logger.debug("标题相似度大于80%，进行详情比对。。");
//                        if (ComputeResemble.similarDegreeWrapper(newContent, tempContent) > 0.99) {
//                            logger.debug("详情似度大于99%，应该去重。。");
//                            repeatNotices.add(tempNotice);
//                        }
//                    } else if (tempTitle.contains(tempKeyWord) && ComputeResemble.similarDegreeWrapper(newTitle, tempTitle) > 0.97) {
//                        logger.debug("标题相似度大于97%，进行详情比对。。");
//                        if (ComputeResemble.similarDegreeWrapper(newContent, tempContent) > 0.99999) {
//                            logger.debug("详情似度大于99.999%，应该去重。。");
//                            repeatNotices.add(tempNotice);
//                        }
//                    }
//                }
//                noticeList = repeatNotices;
//            }
//
//            if (noticeList.isEmpty()) {
//                logger.info("####前后3天无相同标题公告，入库!titlt:[" + notice.getTitle() + "]####");
//                handleNotRepeatZhaobiao(notice);
//                if (notice.getType() == 2)
//                    insertZhongbiaoAnalyzeDetail(notice.getDetailZhongBiao(), notice);
//                else
//                    insertZhaobiaoAnalyzeDetail(notice.getDetail(), notice);
//                return true;
//            } else if (noticeList.size() == 1) {
//                // 根据网站等级去重，保留网站等级低的公告，去除网站等级高的
//                logger.info("####开始依据网站等级去重和保留!####");
//                long repStartTime = System.currentTimeMillis(); // 去重开始时间
//                handleRepeat(noticeList.get(0), notice);
//                logger.info("##### 去重消耗时间：" + (System.currentTimeMillis() - repStartTime) + " ms #####");
//                return false;
//            } else {
//                handleNotRepeatZhaobiao(notice);
//                if (notice.getType() == 2)
//                    insertZhongbiaoAnalyzeDetail(notice.getDetailZhongBiao(), notice);
//                else
//                    insertZhaobiaoAnalyzeDetail(notice.getDetail(), notice);
//                logger.warn("多条公告重复，不替换，noticeList：" + JSON.toJSONString(noticeList) + " ms #####");
//                return false;
//            }
//        } else {
//            // 使用以前的去重规则
//            LoggerUtils.showJVM("采用旧的去重规则");
//            if (notice.getType() == 2) {
//                return this.oldZhongBiaoReption(notice);
//            }else {
//                return this.oldZhaoBiaoReption(notice);
//            }
//
//        }
//
//    }

    private void insertZhaobiaoAnalyzeDetail(AnalyzeDetail zhaobiaoDetail, EsNotice notice) {
        //把解析结果插入维度临时表，根据业务字段匹配规则进行去重
        zhaobiaoDetail.setRedisId(Integer.parseInt(notice.getUuid()));
        insertOrUpdateAnalyzeDetail(zhaobiaoDetail);
    }

    private void insertZhongbiaoAnalyzeDetail(AnalyzeDetailZhongBiao zhongbiaoDetail, EsNotice notice) {
        //把解析结果插入程序解析维度表，根据业务字段匹配规则进行去重
        zhongbiaoDetail.setRedisId(Integer.parseInt(notice.getUuid()));
        insertOrUpdateAnalyzeDetailZhongBiao(zhongbiaoDetail);
    }

    /**
     * 根据项目地区、项目金额、评标办法匹配，项目县市任意维度为空则取消匹配
     *
     * @param ad
     * @return
     */
    private EsNotice matchAnalyzeZhaobiao(AnalyzeDetail ad) {
        if (MyStringUtils.isNull(ad.getProjDq())
                || MyStringUtils.isNull(ad.getProjSum())
                || MyStringUtils.isNull(ad.getPbMode())) {
            return null;
        }

        String sql = "select a.redisId uuid,a.title title,a.gsDate openDate ,a.noticeUrl url," +
                "ifnull(b.province,'') province,ifnull(b.city,'') city," +
                "ifnull(b.county,'') county, ifnull(type,0) type," +
                "ifnull(b.rank,0) rank,ifnull(b.redisId,0) redisId," +
                "ifnull(b.websitePlanId,0) websitePlanId," +
                "ifnull(b.tableName,'') tableName,c.content " +
                "from mishu.zhaobiao_analyze_detail a " +
                "left join mishu.snatchurl b on a.redisId=b.id " +
                "left join mishu.snatchurlcontent c on c.snatchUrlId= a.redisId " +
                "where a.projDq=? and a.projSum=? and a.pbMode=? and a.gsDate BETWEEN DATE_SUB(?,INTERVAL 3 DAY) AND DATE_SUB(?,INTERVAL -3 DAY) " +
                "union all " +
                "select a.snatchUrlId uuid,a.projName title,a.gsDate openDate,b.url url, " +
                "ifnull(b.province,'') province,ifnull(b.city,'') city, " +
                "ifnull(b.county,'') county, ifnull(type,0) type," +
                "ifnull(b.rank,0) rank,ifnull(b.redisId,0) redisId," +
                "ifnull(b.websitePlanId,0) websitePlanId," +
                "ifnull(b.tableName,'') tableName,c.content " +
                "from mishu.zhaobiao_detail a " +
                "left join mishu.snatchurl b on a.snatchUrlId=b.id " +
                "left join mishu.snatchurlcontent c on c.snatchUrlId= a.snatchUrlId " +
                "where a.projDq=? and a.projSum=? and a.pbMode=? and a.gsDate BETWEEN DATE_SUB(?,INTERVAL 3 DAY) AND DATE_SUB(?,INTERVAL -3 DAY)";

        List<Map<String, Object>> list = this.getJdbcTemplate().queryForList(sql, ad.getProjDq(), ad.getProjSum(), ad.getPbMode(), ad.getGsDate(), ad.getGsDate(),
                ad.getProjDq(), ad.getProjSum(), ad.getPbMode(), ad.getGsDate(), ad.getGsDate());
        //取第一条
        if (list.size() > 0) {
            Map<String, Object> m = list.get(0);
            EsNotice obj = new EsNotice();
            obj.setUuid(String.valueOf(m.get("uuid")));
            obj.setTitle(String.valueOf(m.get("title")));
            obj.setUrl(String.valueOf(m.get("url")));
            obj.setOpenDate(String.valueOf(m.get("openDate")));
            obj.setContent(String.valueOf(m.get("content")));
            obj.setProvince(String.valueOf(m.get("province")));
            obj.setCity(String.valueOf(m.get("city")));
            obj.setCounty(String.valueOf(m.get("county")));
            obj.setType(Integer.valueOf(String.valueOf(m.get("type"))));
            obj.setRank(Integer.valueOf(String.valueOf(m.get("rank"))));
            obj.setRedisId(Integer.valueOf(String.valueOf(m.get("redisId"))));
            obj.setWebsitePlanId(Integer.valueOf(String.valueOf(m.get("websitePlanId"))));
            obj.setTableName(String.valueOf(m.get("tableName")));
            return obj;
        } else {
            return null;
        }
    }

    /**
     * 根据第一第二第三，任意维度为空则取消匹配
     *
     * @param ad
     * @return
     */
    private EsNotice matchAnalyzeZhongBiao(AnalyzeDetailZhongBiao ad) {
        if (MyStringUtils.isNull(ad.getOneName())
                || MyStringUtils.isNull(ad.getTwoName())
                || MyStringUtils.isNull(ad.getThreeName())) {
            return null;
        }

        String sql = "select a.redisId uuid,a.title title,a.gsDate openDate ,a.noticeUrl url," +
                "ifnull(b.province,'') province,ifnull(b.city,'') city," +
                "ifnull(b.county,'') county, ifnull(type,0) type," +
                "ifnull(b.rank,0) rank,ifnull(b.redisId,0) redisId," +
                "ifnull(b.websitePlanId,0) websitePlanId," +
                "ifnull(b.tableName,'') tableName,c.content " +
                "from mishu.zhongbiao_analyze_detail a " +
                "left join mishu.snatchurl b on a.redisId=b.id " +
                "left join mishu.snatchurlcontent c on c.snatchUrlId= a.redisId " +
                "where a.oneName=? and a.twoName=? and a.threeName=? and a.gsDate BETWEEN DATE_SUB(?,INTERVAL 3 DAY) AND DATE_SUB(?,INTERVAL -3 DAY) " +
                "union all " +
                "select a.snatchUrlId uuid,a.projName title,a.gsDate openDate,b.url url, " +
                "ifnull(b.province,'') province,ifnull(b.city,'') city, " +
                "ifnull(b.county,'') county, ifnull(type,0) type," +
                "ifnull(b.rank,0) rank,ifnull(b.redisId,0) redisId," +
                "ifnull(b.websitePlanId,0) websitePlanId," +
                "ifnull(b.tableName,'') tableName,c.content " +
                "from mishu.zhongbiao_detail a " +
                "left join mishu.snatchurl b on a.snatchUrlId=b.id " +
                "left join mishu.snatchurlcontent c on c.snatchUrlId= a.snatchUrlId " +
                "where a.oneName=? and a.twoName=? and a.threeName=? and a.gsDate BETWEEN DATE_SUB(?,INTERVAL 3 DAY) AND DATE_SUB(?,INTERVAL -3 DAY)";

        List<Map<String, Object>> list = this.getJdbcTemplate().queryForList(sql, ad.getOneName(), ad.getTwoName(), ad.getThreeName(), ad.getGsDate(), ad.getGsDate(),
                ad.getOneName(), ad.getTwoName(), ad.getThreeName(), ad.getGsDate(), ad.getGsDate());
        //取第一条
        if (list.size() > 0) {
            Map<String, Object> m = list.get(0);
            EsNotice obj = new EsNotice();
            obj.setUuid(String.valueOf(m.get("uuid")));
            obj.setTitle(String.valueOf(m.get("title")));
            obj.setUrl(String.valueOf(m.get("url")));
            obj.setOpenDate(String.valueOf(m.get("openDate")));
            obj.setContent(String.valueOf(m.get("content")));
            obj.setProvince(String.valueOf(m.get("province")));
            obj.setCity(String.valueOf(m.get("city")));
            obj.setCounty(String.valueOf(m.get("county")));
            obj.setType(Integer.valueOf(String.valueOf(m.get("type"))));
            obj.setRank(Integer.valueOf(String.valueOf(m.get("rank"))));
            obj.setRedisId(Integer.valueOf(String.valueOf(m.get("redisId"))));
            obj.setWebsitePlanId(Integer.valueOf(String.valueOf(m.get("websitePlanId"))));
            obj.setTableName(String.valueOf(m.get("tableName")));
            return obj;
        } else {
            return null;
        }
    }


    /**
     * 现在采购与非采购分开、有资质的才入库
     * 采购的入库规则(资质->内容)
     *
     * @param notice
     */

    @Override
    public void handleNotRepeatZhaobiao(EsNotice notice) {
        Map<String, String> map = this.insertNotice(notice);//插入公告内容以及url表
        String uuid = map.get("id");
        String otherType = map.get("otherType");
        String biddingType = map.get("biddingType");

        notice.setOtherType(otherType);
        notice.setBiddingType(biddingType);
        notice.setUuid(uuid);
        //非2中标公告进入资质解析
        if (notice.getType() != 2) {
            disruptorOperator.publishQuaParse(notice);
        }else if (notice.getType() == 2) { //中标
            try {
                insertZhongbiaoEsNotice(notice);
            } catch (Exception e) {
                logger.error("@@@@ES中标入库报错" + e);
            }
        } else {

        }

    }

    /**
     * 判断网址是否是省招投标监管网或者公共资源网的
     */
    private boolean checkMainWebSide(String url) {
        for (String webSide : mainWebside) {
            if (url.contains(webSide))
                return true;
        }
        return false;
    }

    /**
     * 重复数据，进行去重处理(更新或去重)
     *
     * @param obj    匹配到的历史公告
     * @param notice 当前待插入的公告
     */
    private void handleRepeatZhaobiao(EsNotice obj, EsNotice notice) {
        if (!obj.getUrl().equals(notice.getUrl())) {
            notice.setUuid(obj.getUuid());
            int openDateFlag = DateUtils.compareStrDate(obj.getOpenDate(), notice.getOpenDate());    //比较公告时间()
            //抓取地区等级>数据库地区等级，更新数据库，且更新搜索引擎
            if (notice.getRank() > obj.getRank()) {
                insertNoticeRepetition(obj);//库中的记录插入到重复记录表
                //把最新进入的公告内容更新到库中
                this.getJdbcTemplate().update("update mishu.snatchurl set " +
                                "url=?,title=?,openDate=?,province=?," +
                                "city=?,county=?,rank=?,websitePlanId=?," +
                                "changeNum=changeNum+1 where id =?",
                        notice.getUrl(), notice.getTitle(), notice.getOpenDate(), notice.getProvince(),
                        notice.getCity(), notice.getCounty(), notice.getRank(), notice.getWebsitePlanId(),
                        notice.getUuid());

                this.getJdbcTemplate().update("update mishu.snatchurlcontent set content =? where snatchUrlId = ?", notice.getContent(), notice.getUuid());
                String plainHtml = chineseCompressUtil.getPlainText(notice.getContent());
                this.getJdbcTemplate().update("update mishu.snatchpress set press =? where snatchUrlId = ?", plainHtml, notice.getUuid());
//                this.getJdbcTemplate().update("UPDATE mishu.message_push SET title = ? where mainId = ?", notice.getTitle(), notice.getUuid());
                //清理缓存公告内容
                redisClear.clearGonggaoContent(notice.getUuid());
                redisClear.clearGonggaoDataContent(notice.getUuid());
                logger.info("清理缓存公告内容:" + notice.getUuid());
                logger.info("@@***更新:根据地区等级更新,历史标题" + obj.getTitle() + "被" + notice.getTitle() + "替换***@@");
//                insertEsNoticeElasticSearch(notice);//更新到搜索引擎
                if (notice.getType() == 2) { //中标
                    try {
                        insertZhongbiaoEsNotice(notice);
                    } catch (Exception e) {
                        logger.error("@@@@ES中标去重更新失败" + e);
                    }
                } else {
                    try {
                        updateZhaobiaoEsNotice(notice);
                    } catch (Exception e) {
                        logger.error("@@@@ES招标去重更新失败" + e);
                    }
                }
            }
            /*else if (openDateFlag == 1 || openDateFlag == 0) {    //抓取公式时间>数据库公式时间，更新数据库，且更新搜索引擎
                insertNoticeRepetition(obj);//库中的记录插入到重复记录表
                //把最新进入的公告内容更新到库中
                this.getJdbcTemplate().update("update mishu.snatchurl set " +
                                "url=?,title=?,openDate=?,province=?," +
                                "city=?,county=?,rank=?,websitePlanId=?," +
                                "changeNum=changeNum+1 where id =?",
                        notice.getUrl(), notice.getTitle(), notice.getOpenDate(), notice.getProvince(),
                        notice.getCity(), notice.getCounty(), notice.getRank(), notice.getWebsitePlanId(),
                        notice.getUuid());

                this.getJdbcTemplate().update("update mishu.snatchurlcontent set content =? where snatchUrlId = ?", notice.getContent(), notice.getUuid());
                String plainHtml = chineseCompressUtil.getPlainText(notice.getContent());
                this.getJdbcTemplate().update("update mishu.snatchpress set press =? where snatchUrlId = ?", plainHtml, notice.getUuid());
                //清理缓存公告内容
                redisClear.clearGonggaoContent(notice.getUuid());
                redisClear.clearGonggaoDataContent(notice.getUuid());
                logger.info("清理缓存公告内容:" + notice.getUuid());
                logger.info("***根据公示时间更新,历史标题" + obj.getTitle() + "被" + notice.getTitle() + "替换***");
                insertEsNoticeElasticSearch(notice);//更新到搜索引擎
            } */
            else {//插入到重复公告记录表
                if (this.getJdbcTemplate().queryForObject("select count(1) from mishu.snatchurl_repetition where url=?",
                        new Object[]{notice.getUrl()}, Integer.class) < 1
                        && !obj.getUrl().equals(notice.getUrl())) {    //url不同且重复记录表没有该url
                    insertNoticeRepetition(notice);//最新公告数据插入重复记录表
                    logger.info("@@@去重:保留历史标题：" + obj.getTitle() + "url：" + obj.getUrl() + "@@@");
                }
            }
        }
    }

    /**
     * 重复数据，进行去重处理(更新或去重)
     *
     * @param objs   匹配到的历史公告
     * @param notice 当前待插入的公告
     */
    private void handleRepeat(List<EsNotice> objs, EsNotice notice) {

        List<EsNotice> provinceEsNotices = new ArrayList<>(); // 省级普通网站
        List<EsNotice> provinceMainEsNotices = new ArrayList<>();   //省级监管网站
        List<EsNotice> cityEsNotices = new ArrayList<>();    //市级普通网站
        List<EsNotice> cityMainEsNotices = new ArrayList<>();    //市级公共资源网站
        List<EsNotice> countyEsNotices = new ArrayList<>();  //区县级普通该网站
        List<EsNotice> countyMainEsNotices = new ArrayList<>();  //区县级公共资源网站

        //对重复的历史公告进行分类
        for (EsNotice obj : objs) {
            boolean isMainWebSide = checkMainWebSide(obj.getUrl());
            if (obj.getRank() == 0 && isMainWebSide) {
                provinceMainEsNotices.add(obj);
            } else if (obj.getRank() == 0) {
                provinceEsNotices.add(obj);
            } else if (obj.getRank() == 1 && isMainWebSide) {
                cityMainEsNotices.add(obj);
            } else if (obj.getRank() == 1) {
                cityEsNotices.add(obj);
            } else if (obj.getRank() == 2 && isMainWebSide) {
                countyMainEsNotices.add(obj);
            } else if (obj.getRank() == 2) {
                countyEsNotices.add(obj);
            }
        }
        boolean isMainWebSideNew = checkMainWebSide(notice.getUrl());
        List<EsNotice> repeatEsNotices = new ArrayList<>();
        List<EsNotice> delEsNotices = new ArrayList<>();
        boolean isInsert = false;
        //已存在市公共资源的数据
        if (!cityMainEsNotices.isEmpty()) {
            //其他公告进入重复集合
            repeatEsNotices.addAll(cityEsNotices);
            repeatEsNotices.addAll(provinceEsNotices);
            repeatEsNotices.addAll(countyEsNotices);
            repeatEsNotices.addAll(countyMainEsNotices);
            //省级监管网进入删除集合
            delEsNotices.addAll(provinceMainEsNotices);
            //新公告是市公共资源的数据
            //新增
//插入重复公告
            isInsert = notice.getRank() == 1 && isMainWebSideNew;
        } else {
            //新公告是省级网站公告
            if (notice.getRank() == 0) {
                //其他公告进入重复集合
                repeatEsNotices.addAll(cityEsNotices);
                repeatEsNotices.addAll(countyEsNotices);
                repeatEsNotices.addAll(countyMainEsNotices);
                if (isMainWebSideNew)//如果是省级监管网 将以前的省普通网站数据添加到重复集合
                    repeatEsNotices.addAll(provinceEsNotices);
                isInsert = true;//新增
            } else if (notice.getRank() == 1) {
                if (isMainWebSideNew) {
                    repeatEsNotices.addAll(cityEsNotices);
                    repeatEsNotices.addAll(provinceEsNotices);
                    delEsNotices.addAll(provinceMainEsNotices);
                    repeatEsNotices.addAll(countyEsNotices);
                    repeatEsNotices.addAll(countyMainEsNotices);
                    isInsert = true;//新增
                } else {
                    if (!provinceEsNotices.isEmpty() || !provinceMainEsNotices.isEmpty()) {
                        repeatEsNotices.addAll(cityEsNotices);
                        repeatEsNotices.addAll(countyEsNotices);
                        repeatEsNotices.addAll(countyMainEsNotices);
                        isInsert = false;//插入重复公告
                    } else {
                        repeatEsNotices.addAll(countyEsNotices);
                        repeatEsNotices.addAll(countyMainEsNotices);
                        isInsert = true;//新增
                    }
                }
            } else if (notice.getRank() == 2) {
                if (!provinceEsNotices.isEmpty() || !provinceMainEsNotices.isEmpty() || !cityEsNotices.isEmpty()) {
                    repeatEsNotices.addAll(countyEsNotices);
                    repeatEsNotices.addAll(countyMainEsNotices);
                    isInsert = false;//插入重复公告
                } else {
                    isInsert = true;//新增
                }
            }
        }
        for (EsNotice delEsNotice : delEsNotices) {
            redisClear.clearGonggaoContent(delEsNotice.getUuid());
            redisClear.clearGonggaoDataContent(delEsNotice.getUuid());
            if (isInsert) {
                notice.setUuid(delEsNotice.getUuid());
                notice.setEdit(delEsNotice.getEdit());
                //把最新进入的公告内容更新到库中
                this.getJdbcTemplate().update("update mishu.snatchurl set " +
                                "url=?,title=?,openDate=?,province=?," +
                                "city=?,county=?,rank=?,websitePlanId=?,uuid=?,businessType=?," +
                                "changeNum=changeNum+1,otherType=?,snatchDateTime=NOW(),redisId=?,source=? where id =? ",
                        notice.getUrl(), notice.getTitle(), notice.getOpenDate(), notice.getProvince(),
                        notice.getCity(), notice.getCounty(), notice.getRank(), notice.getWebsitePlanId(), notice.getSnatchNumber(), notice.getBusinessType(),
                        notice.getOtherType(), notice.getRedisId(),notice.getSource(), delEsNotice.getUuid());
                this.getJdbcTemplate().update("update mishu.snatchurlcontent set content =? where snatchUrlId = ?", notice.getContent(), notice.getUuid());
                String plainHtml = chineseCompressUtil.getPlainText(notice.getContent());
                this.getJdbcTemplate().update("update mishu.snatchpress set press =? where snatchUrlId = ?", plainHtml, notice.getUuid());
                if (notice.getType() == 2) { //中标
                    try {
                        insertZhongbiaoEsNotice(notice);
                    } catch (Exception e) {
                        logger.error("@@@@ES中标去重更新失败" + e);
                    }
                } else {
                    try {
                        updateZhaobiaoEsNotice(notice);
                    } catch (Exception e) {
                        logger.error("@@@@ES招标去重更新失败" + e);
                    }
                }
                //清理缓存公告内容
                logger.info("@@***更新:根据地区等级更新,历史标题" + delEsNotice.getTitle() + "被" + notice.getTitle() + "替换***@@");
                delEsNotice.setIsShow(1);
                insertNewUrl(delEsNotice);
            } else {
                //逻辑删除原公告
                this.getJdbcTemplate().update("update mishu.snatchurl set isShow = 1 where id =?", delEsNotice.getUuid());
                delEsNotice(delEsNotice.getType(), delEsNotice.getUuid());
                logger.info("清理省监管网缓存公告内容:" + delEsNotice.getUuid());
            }
        }

        for (EsNotice repeatEsNotice : repeatEsNotices) {
            insertNoticeRepetition(repeatEsNotice);//库中的记录插入到重复记录表
            redisClear.clearGonggaoContent(repeatEsNotice.getUuid());
            redisClear.clearGonggaoDataContent(repeatEsNotice.getUuid());
            logger.info("清理重复缓存公告内容:" + repeatEsNotice.getUuid());
            if (isInsert) {
                notice.setUuid(repeatEsNotice.getUuid());
                notice.setEdit(repeatEsNotice.getEdit());
                //把最新进入的公告内容更新到库中
                this.getJdbcTemplate().update("update mishu.snatchurl set " +
                                "url=?,title=?,openDate=?,province=?," +
                                "city=?,county=?,rank=?,websitePlanId=?,uuid=?,businessType=?," +
                                "changeNum=changeNum+1,otherType=?,snatchDateTime=NOW(),redisId=? where id =? ",
                        notice.getUrl(), notice.getTitle(), notice.getOpenDate(), notice.getProvince(),
                        notice.getCity(), notice.getCounty(), notice.getRank(), notice.getWebsitePlanId(), notice.getSnatchNumber(), notice.getBusinessType(),
                        notice.getOtherType(), notice.getRedisId(), repeatEsNotice.getUuid());

                this.getJdbcTemplate().update("update mishu.snatchurlcontent set content =? where snatchUrlId = ?", notice.getContent(), notice.getUuid());
                String plainHtml = chineseCompressUtil.getPlainText(notice.getContent());
                this.getJdbcTemplate().update("update mishu.snatchpress set press =? where snatchUrlId = ?", plainHtml, notice.getUuid());
                //清理缓存公告内容
                logger.info("@@***更新:根据地区等级更新,历史标题" + repeatEsNotice.getTitle() + "被" + notice.getTitle() + "替换***@@");
                if (notice.getType() == 2) { //中标
                    try {
                        insertZhongbiaoEsNotice(notice);
                    } catch (Exception e) {
                        logger.error("@@@@ES中标去重更新失败" + e);
                    }
                } else {
                    try {
                        updateZhaobiaoEsNotice(notice);
                    } catch (Exception e) {
                        logger.error("@@@@ES招标去重更新失败" + e);
                    }
                }
            } else {
                this.getJdbcTemplate().update("update mishu.snatchurl set isShow = 1 where id =?", repeatEsNotice.getUuid());  //逻辑删除新公告
                delEsNotice(repeatEsNotice.getType(), repeatEsNotice.getUuid());
            }
        }

        if (isInsert && repeatEsNotices.isEmpty() && delEsNotices.isEmpty()) {
            logger.info("新增公告:" + notice.getTitle());
            //市级公共资源公告入库
            handleNotRepeatZhaobiao(notice);
            if (notice.getType() == 2) {
                insertZhongbiaoAnalyzeDetail(notice.getDetailZhongBiao(), notice);
            } else {
                insertZhaobiaoAnalyzeDetail(notice.getDetail(), notice);
            }
        } else if (!isInsert) {
            if (notice.getRank() == 0 && isMainWebSideNew) {//省级监管网插入url表并设置为逻辑删除
                notice.setIsShow(1);
                insertNewUrl(notice);
                try{
                    // 删除相关公告信息
                    Long id = this.getJdbcTemplate().queryForObject("SELECT id FROM mishu.snatchurl WHERE title = ? AND url = ?",new Object[]{notice.getTitle(),notice.getUrl()},Long.class);
                    deleteRelationInfo(id);
                } catch (EmptyResultDataAccessException e) {
                    logger.info("###ERROR:"+e.getMessage());
                }
            }
            else if (this.getJdbcTemplate().queryForObject("select count(1) from mishu.snatchurl_repetition where url=?",
                    new Object[]{notice.getUrl()}, Integer.class) < 1) {    //url不同且重复记录表没有该url
                insertNoticeRepetition(notice);//最新公告数据插入重复记录表
                logger.info("@@@去重:保留新标题：" + notice.getTitle() + "url：" + notice.getUrl() + "@@@");
            } else {
                logger.info("@@@去重:重复表已存在相同公告：" + notice.getTitle() + "url：" + notice.getUrl() + "@@@");
            }

        }
    }


    /**
     * 重复数据，进行去重处理(更新或去重)
     *
     * @param oldNotice 匹配到的历史公告
     * @param notice    当前待插入的公告
     */
    private void handleRepeat(EsNotice oldNotice, EsNotice notice) {

        int oldRank = oldNotice.getRank();
        int newRank = notice.getRank();
        boolean isMainWebSideOld = checkMainWebSide(oldNotice.getUrl());
        boolean isMainWebSideNew = checkMainWebSide(notice.getUrl());
        boolean isRepeatOld = false;//老公告是否进重复
        boolean isInsertNew = false;//新公告是否新增， false进行重复
        boolean isNewCzHasFile = false; // 新公告是郴州且带有附件
        boolean isOldCzHasFile = false; // 旧公告是郴州且带有附件


        //新公告是省级网站公告
        if (newRank == 1 && isMainWebSideNew) {
            if (oldRank != 1 || !isMainWebSideOld)
                isRepeatOld = true;
            isInsertNew = true;
        } else if (oldRank == newRank) {
            if (isMainWebSideOld == isMainWebSideNew)
                isInsertNew = true;
            else if (isMainWebSideNew) {
                isRepeatOld = true;
                isInsertNew = true;
            }
        } else if (newRank > oldRank) {
            isInsertNew = true;
            isRepeatOld = true;
        }

        //临时添加 郴州公共资源和省监管网规则
        String oldUrl = oldNotice.getUrl();
        String newUrl = notice.getUrl();
        if (oldUrl.contains(bidding) && newUrl.contains(czggzy)) {
            isInsertNew = false;
            isRepeatOld = false;
            String czContent = notice.getContent();
            String regex = "(href=\"|src=\"|href =\"|src =\"|href = \"|src = \").*?(zip|rar|7z|docx|doc|jpg|jpeg|png|ppt|xls|wps|xlsx)";
            Pattern pa = Pattern.compile(regex);
            if (pa.matcher(czContent).find()) {
                // 郴州公告内容有附件
                isNewCzHasFile = true;
            }
        } else if (oldUrl.contains(czggzy) && newUrl.contains(bidding)) {
            isInsertNew = true;
            isRepeatOld = true;
            String czContent = oldNotice.getContent();
            String regex = "(href=\"|src=\"|href =\"|src =\"|href = \"|src = \").*?(zip|rar|7z|docx|doc|jpg|jpeg|png|ppt|xls|wps|xlsx)";
            Pattern pa = Pattern.compile(regex);
            if (pa.matcher(czContent).find()) {
                // 郴州公告内容有附件
                isOldCzHasFile = true;
            }
        }

        String newTitle = notice.getTitle();
        newTitle = notice.getType() == 2 ? MyStringUtils.subZhongBiaoTile(newTitle) : MyStringUtils.subZhaobiaoTile(newTitle);
        String tempTitle = oldNotice.getTitle();
        tempTitle = oldNotice.getType() == 2 ? MyStringUtils.subZhongBiaoTile(tempTitle) : MyStringUtils.subZhaobiaoTile(tempTitle);
        String newContent = chineseCompressUtil.getPlainText(notice.getContent());
        newContent = MyStringUtils.deleteHtmlTag(newContent);
        newContent = newContent.replaceAll(" ", "");
        String oldContent = chineseCompressUtil.getPlainText(oldNotice.getContent());
        oldContent = MyStringUtils.deleteHtmlTag(oldContent);
        oldContent = newContent.replaceAll(" ", "");
        // 郴州公告内容是否带有附件
        if ((isNewCzHasFile || isOldCzHasFile) && (newTitle.equals(tempTitle)) && ComputeResemble.similarDegreeWrapper(newContent, oldContent) < 0.2) {
            if (oldContent.length() < 100 || newContent.length() < 100) {
                if (isNewCzHasFile) {
                    // 去重新公告，保留旧公告（省网）
                    logger.info("新公告郴州带附件被省网去重:" + notice.getTitle());
                    if (this.getJdbcTemplate().queryForObject("select count(1) from mishu.snatchurl_repetition where url=?",
                            new Object[]{notice.getUrl()}, Integer.class) < 1){ // url不同且重复记录表没有该url
                        insertNoticeRepetition(notice);//最新公告数据插入重复记录表
                    }
                } else {
                    // 去重旧公告，保留新公告(省网)
                    logger.info("新公告为省网且历史公告为郴州内容带附件,历史标题" + oldNotice.getTitle() + "被" + notice.getTitle() + "替换");
                    redisClear.clearGonggaoContent(oldNotice.getUuid());
                    redisClear.clearGonggaoDataContent(oldNotice.getUuid());
                    logger.info("清理重复缓存公告内容:" + oldNotice.getUuid());
                    notice.setUuid(oldNotice.getUuid());
                    notice.setEdit(oldNotice.getEdit());
                    //把最新进入的公告内容更新到库中
                    this.getJdbcTemplate().update("update mishu.snatchurl set " +
                                    "url=?,title=?,openDate=?,province=?," +
                                    "city=?,county=?,rank=?,websitePlanId=?,uuid=?,businessType=?," +
                                    "changeNum=changeNum+1,otherType=?,snatchDateTime=NOW(),redisId=? where id =? ",
                            notice.getUrl(), notice.getTitle(), notice.getOpenDate(), notice.getProvince(),
                            notice.getCity(), notice.getCounty(), notice.getRank(), notice.getWebsitePlanId(), notice.getSnatchNumber(), notice.getBusinessType(),
                            notice.getOtherType(), notice.getRedisId(), oldNotice.getUuid());
                    this.getJdbcTemplate().update("update mishu.snatchurlcontent set content =? where snatchUrlId = ?", notice.getContent(), notice.getUuid());
                    String plainHtml = chineseCompressUtil.getPlainText(notice.getContent());
                    this.getJdbcTemplate().update("update mishu.snatchpress set press =? where snatchUrlId = ?", plainHtml, notice.getUuid());
                    if (notice.getType() == 2) { //中标
                        try {
                            insertZhongbiaoEsNotice(notice);
                        } catch (Exception e) {
                            logger.error("@@@@ES中标去重更新失败" + e);
                        }
                    } else {
                        try {
                            updateZhaobiaoEsNotice(notice);
                        } catch (Exception e) {
                            logger.error("@@@@ES招标去重更新失败" + e);
                        }
                    }
                    insertNoticeRepetition(oldNotice);//库中的记录插入到重复记录表
                }
                return;
            }
        }



        if (isInsertNew && isRepeatOld) {
            logger.info("根据地区等级更新,历史标题" + oldNotice.getTitle() + "被" + notice.getTitle() + "替换");
            redisClear.clearGonggaoContent(oldNotice.getUuid());
            redisClear.clearGonggaoDataContent(oldNotice.getUuid());
            logger.info("清理重复缓存公告内容:" + oldNotice.getUuid());

            notice.setUuid(oldNotice.getUuid());
            notice.setEdit(oldNotice.getEdit());
            //把最新进入的公告内容更新到库中
            this.getJdbcTemplate().update("update mishu.snatchurl set " +
                            "url=?,title=?,openDate=?,province=?," +
                            "city=?,county=?,rank=?,websitePlanId=?,uuid=?,businessType=?," +
                            "changeNum=changeNum+1,otherType=?,snatchDateTime=NOW(),redisId=? where id =? ",
                    notice.getUrl(), notice.getTitle(), notice.getOpenDate(), notice.getProvince(),
                    notice.getCity(), notice.getCounty(), notice.getRank(), notice.getWebsitePlanId(), notice.getSnatchNumber(), notice.getBusinessType(),
                    notice.getOtherType(), notice.getRedisId(), oldNotice.getUuid());
            this.getJdbcTemplate().update("update mishu.snatchurlcontent set content =? where snatchUrlId = ?", notice.getContent(), notice.getUuid());
            String plainHtml = chineseCompressUtil.getPlainText(notice.getContent());
            this.getJdbcTemplate().update("update mishu.snatchpress set press =? where snatchUrlId = ?", plainHtml, notice.getUuid());
            if (notice.getType() == 2) { //中标
                try {
                    insertZhongbiaoEsNotice(notice);
                } catch (Exception e) {
                    logger.error("@@@@ES中标去重更新失败" + e);
                }
            } else {
                try {
                    updateZhaobiaoEsNotice(notice);
                } catch (Exception e) {
                    logger.error("@@@@ES招标去重更新失败" + e);
                }
            }

            if (isMainWebSideOld && oldRank == 0) {
                oldNotice.setIsShow(1);
                insertNewUrl(oldNotice);
            }else {
                insertNoticeRepetition(oldNotice);//库中的记录插入到重复记录表
            }
        } else if (isInsertNew) {
            logger.info("新增公告:" + notice.getTitle());
            //市级公共资源公告入库
            handleNotRepeatZhaobiao(notice);
            if (notice.getType() == 2) {
                insertZhongbiaoAnalyzeDetail(notice.getDetailZhongBiao(), notice);
            } else {
                insertZhaobiaoAnalyzeDetail(notice.getDetail(), notice);
            }
        } else if (!isInsertNew) {
            logger.info("新公告被去重:" + notice.getTitle());
            if (newRank == 0 && isMainWebSideNew) {//省级监管网插入url表并设置为逻辑删除
                notice.setIsShow(1);
                insertNewUrl(notice);
            } else if (this.getJdbcTemplate().queryForObject("select count(1) from mishu.snatchurl_repetition where url=?",
                    new Object[]{notice.getUrl()}, Integer.class) < 1) {    //url不同且重复记录表没有该url
                insertNoticeRepetition(notice);//最新公告数据插入重复记录表
                logger.info("@@@去重:保留新标题：" + notice.getTitle() + "url：" + notice.getUrl() + "@@@");
            } else {
                logger.info("@@@去重:重复表已存在相同公告：" + notice.getTitle() + "url：" + notice.getUrl() + "@@@");
            }
        }
    }

    
    public void insertEsNoticeElasticSearch(EsNotice notice) {
        try {
            if (notice != null) {
                Map<String, Object> map = new HashMap<String, Object>();
                IndexRequestBuilder builder = client.prepareIndex("mishu", notice.getTableName().replaceAll("mishu.", ""));

                ChineseCompressUtil util = new ChineseCompressUtil();
                String reContent = util.getPlainText(notice.getContent());

                map.put("title", notice.getTitle());
                map.put("url", notice.getUrl());
                if (notice.getOpenDate() != null) {
                    map.put("openDate", notice.getOpenDate().trim());
                }
                map.put("province", notice.getProvince());
                map.put("city", notice.getCity());
                map.put("county", notice.getCounty());
                map.put("type", notice.getType());
                map.put("rank", notice.getRank());
                map.put("redisId", notice.getRedisId());
                map.put("websitePlanId", notice.getWebsitePlanId());
                map.put("content", reContent);
                map.put("part", notice.getTitle() + notice.getOpenDate() + reContent);
                map.put("tableName", notice.getTableName());

                Map<String, Object> beanMap = new BeanMap(notice.getDetail());//公告各维度明细
                map.putAll(beanMap);

                String json = JSON.toJSONString(map);
                IndexResponse response = builder.setId(notice.getUuid()).setSource(json).execute().actionGet();
                map = null;
                util = null;
            }
        } catch (Exception e) {
            logger.error(e, e);
        }
    }

    @Override
    public int insertZhaobiaoAnalyzeDetail(AnalyzeDetail ad) {

        String sql = "INSERT INTO `mishu`.`zhaobiao_analyze_detail` (" +
                "  `redisId`,`noticeUrl`,`title`," +
                "  `tbAssureSum`,`projDq`,`projXs`," +
                "  `projSum`,`bmStartDate`,`bmEndDate`," +
                "  `bmEndTime`,`bmSite`,`kbSite`," +
                "  `tbEndDate`, `tbEndTime`, `gsDate`," +
                "  `province`,`city`, `county`," +
                "  `projType`, `zzRank`,`pbMode`," +
                "  `tbAssureEndDate`," +
                "  `tbAssureEndTime`," +
                "  `lyAssureSum`," +
                "  `slProveSum`," +
                "  `assureEndDate`," +
                "  `assureEndTime`," +
                "  `zgCheckDate`," +
                "  `kbStaffAsk`," +
                "  `fileCost`," +
                "  `otherCost`," +
                "  `zbName`," +
                "  `zbContactMan`," +
                "  `zbContactWay`," +
                "  `dlContactMan`," +
                "  `dlContactWay`," +
                "  `personRequest`," +
                "  `shebaoRequest`," +
                "  `yejiRequest`," +
                "  `registrationForm`," +
                "  `projectTimeLimit`," +
                "  `projectCompletionDate`," +
                "  `supplementNoticeNumber`," +
                "  `supplementNoticeReason`," +
                "  `flowStandardFlag`," +
                "  `money`," +
                "  `block`," +
                "  `analyzeDate`" +
                ") " +
                "VALUES" +
                "  (" +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    NOW()" +
                "  ) ;";
        int flag = this.getJdbcTemplate().update(
                sql,
                ad.getRedisId(),
                ad.getNoticeUrl(),
                ad.getTitle(),
                ad.getTbAssureSum(),
                ad.getProjDq(),
                ad.getProjXs(),
                ad.getProjSum(),
                ad.getBmStartDate(),
                ad.getBmEndDate(),
                ad.getBmEndTime(),
                ad.getBmSite(),
                ad.getKbSite(),
                ad.getTbEndDate(),
                ad.getTbEndTime(),
                ad.getGsDate(),
                ad.getProvince(),
                ad.getCity(),
                ad.getCounty(),
                ad.getProjType(),
                ad.getZzRank(),
                ad.getPbMode(),
                ad.getTbAssureEndDate(),
                ad.getTbAssureEndTime(),
                ad.getLyAssureSum(),
                ad.getSlProveSum(),
                ad.getAssureEndDate(),
                ad.getAssureEndTime(),
                ad.getZgCheckDate(),
                ad.getKbStaffAsk(),
                ad.getFileCost(),
                ad.getOtherCost(),
                ad.getZbName(),
                ad.getZbContactMan(),
                ad.getZbContactWay(),
                ad.getDlContactMan(),
                ad.getDlContactWay(),
                ad.getPersonRequest(),
                ad.getShebaoRequest(),
                ad.getYejiRequest(),
                ad.getRegistrationForm(),
                ad.getProjectTimeLimit(),
                ad.getProjectCompletionDate(),
                ad.getSupplementNoticeNumber(),
                ad.getSupplementNoticeReason(),
                ad.getFlowStandardFlag(),
                ad.getMoney(),
                ad.getBlock());
        return flag;
    }

    @Override
    public int insertZhongBiaoAnalyzeDetail(AnalyzeDetailZhongBiao ad) {
        String sql = "INSERT INTO `mishu`.`zhongbiao_analyze_detail` (" +
                "  `redisId`," +
                "  `noticeUrl`," +
                "  `title`," +
                "  `gsDate`," +
                "  `province`," +
                "  `city`," +
                "  `county`," +
                "  `projSum`," +
                "  `projDq`," +
                "  `projXs`," +
                "  `pbMode`," +
                "  `projType`," +
                "  `oneName`," +
                "  `oneUUid`," +
                "  `oneOffer`," +
                "  `oneProjDuty`," +
                "  `oneProjDutyUuid`," +
                "  `oneSkillDuty`," +
                "  `oneSgy`," +
                "  `oneAqy`," +
                "  `oneZly`," +
                "  `twoName`," +
                "  `twoOffer`," +
                "  `twoProjDuty`," +
                "  `twoSkillDuty`," +
                "  `twoSgy`," +
                "  `twoAqy`," +
                "  `twoZly`," +
                "  `threeName`," +
                "  `threeOffer`," +
                "  `threeProjDuty`," +
                "  `threeSkillDuty`," +
                "  `threeSgy`," +
                "  `threeAqy`," +
                "  `threeZly`," +
                "  `projectTimeLimit`," +
                "  `projectCompletionDate`," +
                "  `block`," +
                "  `analyzeDate`" +
                ") " +
                "VALUES" +
                "  (" +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    ?," +
                "    NOW()" +
                "  ) ;";
        int falg = this.getJdbcTemplate().update(
                sql,
                ad.getRedisId(),
                ad.getNoticeUrl(),
                ad.getTitle(),
                ad.getGsDate(),
                ad.getProvince(),
                ad.getCity(),
                ad.getCounty(),
                ad.getProjSum(),
                ad.getProjDq(),
                ad.getProjXs(),
                ad.getPbMode(),
                ad.getProjType(),
                ad.getOneName(),
                ad.getOneUUid(),
                ad.getOneOffer(),
                ad.getOneProjDuty(),
                ad.getOneProjDutyUuid(),
                ad.getOneSkillDuty(),
                ad.getOneSgy(),
                ad.getOneAqy(),
                ad.getOneZly(),
                ad.getTwoName(),
                ad.getTwoOffer(),
                ad.getTwoProjDuty(),
                ad.getTwoSkillDuty(),
                ad.getTwoSgy(),
                ad.getTwoAqy(),
                ad.getTwoZly(),
                ad.getThreeName(),
                ad.getThreeOffer(),
                ad.getThreeProjDuty(),
                ad.getThreeSkillDuty(),
                ad.getThreeSgy(),
                ad.getThreeAqy(),
                ad.getThreeZly(),
                ad.getProjectTimeLimit(),
                ad.getProjectCompletionDate(),
                ad.getBlock());
        return falg;
    }

    /**
     * 判断url或业务匹配规则是否有重复数据
     *
     * @param ad
     * @return
     */
    public Boolean existNoticeUrl(AnalyzeDetail ad) {
        String sql = "SELECT COUNT(1) FROM mishu.zhaobiao_analyze_detail " +
                "WHERE noticeUrl=? ";
        int result = this.getJdbcTemplate().queryForObject(sql,
                new Object[]{ad.getNoticeUrl()}, Integer.class);
        return result > 0;
    }

    /**
     * 判断url或维度规则是否有重复数据
     *
     * @param ad
     * @return
     */
    public Boolean existNoticeUrlZhongBiao(AnalyzeDetailZhongBiao ad) {
        String sql = "SELECT COUNT(1) FROM mishu.zhongbiao_analyze_detail " +
                "WHERE noticeUrl=?";
        int result = this.getJdbcTemplate().queryForObject(sql,
                new Object[]{ad.getNoticeUrl()}, Integer.class);
        return result > 0;
    }

    public boolean insertEsNoticeZhongBiao(EsNotice notice) {
        //url存在
        String CountNoticeUrlSql = "SELECT COUNT(*) FROM mishu.snatchurl WHERE url = ?";
        int NoticeCount = this.queryForInt(CountNoticeUrlSql, new Object[]{notice.getUrl()});
        if (NoticeCount != 0) {
            logger.info("###新入库公告url已存在" + notice.getUrl() + "###");
            return false;
        }
        notice.setTitle(MyStringUtils.trimInnerSpaceStr(notice.getTitle()));    //去title首尾空格
        //查询湖南的抓取的网站和当前url做对比。获取网站等级
        List<Map<String, Object>> webList = service.querysWebSitePlan(notice.getTableName().replaceAll("mishu.", ""));
        Integer rank = 0;
        notice.setWebsitePlanId(0);
        for (Map<String, Object> wm : webList) {
            if (notice.getUrl().indexOf(String.valueOf(wm.get("url"))) > -1) {
                rank = Integer.valueOf(String.valueOf(wm.get("rank")));
                notice.setWebsitePlanId(Integer.valueOf(String.valueOf(wm.get("id"))));
                break;
            }
        }
        notice.setRank(rank);

        EsNotice obj = null;
        obj = queryNoticeByTitle(notice.getTitle(), notice.getOpenDate(), notice.getType());//根据原始标题查询公示时间前后3天的
        if (obj == null) {//继续匹配关联的标题
            String titleCond = notice.getTitle();
            int titleEndIdx = titleCond.lastIndexOf("中标公告");
            if (titleEndIdx != -1) {
                titleCond = titleCond.substring(0, titleEndIdx);
            }
            titleCond = titleCond.replaceAll(" ", "");    //去空格
            if (MyStringUtils.isNotNull(titleCond)) {//过滤关键字后为空串的直接插入
                obj = queryNoticeByTitle("%" + titleCond + "%", notice.getOpenDate(), notice.getType());//根据标题查询公示时间前后3天的
            }
        }
        boolean bl = false;
        AnalyzeDetailZhongBiao zhongBiaoDetail = notice.getDetailZhongBiao();
        EsNotice repeatNotice = null;
        if (obj == null) {  //标题不符合去重规则走维度去重
            repeatNotice = matchAnalyzeZhongBiao(zhongBiaoDetail);
            if (repeatNotice == null) { //没有重复数据(没有相同维度)
                handleNotRepeatZhaobiao(notice);
                bl = true;
            } else {
                handleRepeatZhaobiao(repeatNotice, notice);
                bl = false;
            }
        } else {    //有重复数据
            handleRepeatZhaobiao(obj, notice);
            bl = false;
        }

        if (repeatNotice == null) {
            zhongBiaoDetail.setGsDate(notice.getOpenDate());
            insertZhongbiaoAnalyzeDetail(zhongBiaoDetail, notice);
        }
        return bl;
    }

    public List<Map<String, Object>> querysLikeNotice(String titleTemp, String openDate) {
        if (MyStringUtils.isNull(titleTemp) || MyStringUtils.isNull(openDate)) {
            return null;
        }
        String sql = "SELECT id,title,snatchDateTime FROM mishu.snatchurl " +
                "WHERE title LIKE ? " +
                "AND openDate BETWEEN DATE_SUB(?,INTERVAL 90 DAY) " +
                "AND DATE_SUB(?,INTERVAL -90 DAY)";

        return this.getJdbcTemplate().queryForList(sql, titleTemp + "%", openDate, openDate);
    }

    public void insertSnatchRelation(String mainId, String nextId) {
        String sql = "INSERT INTO mishu.snatchrelation (mainId, nextId, lastChangeTime,relationMethod)VALUES(?,?,NOW(),0)";
        this.getJdbcTemplate().update(sql, Long.valueOf(mainId), Long.valueOf(nextId));

        //清理公告关联信息缓存
        redisClear.clearGonggaoRelation(mainId);
        redisClear.clearGonggaoRelation(nextId);
        logger.info("insertSnatchRelation finished..清理关联信息缓存：[mainId：" + mainId + "][nextId:" + nextId + "]");
    }

    /**
     * 返回两个公告id所关联的公告id
     *
     * @return
     */
    public List<String> querysDifferNextId(String zhaobId, String thisId) {
        String sql = "SELECT a.nextId FROM( SELECT nextId FROM mishu.snatchrelation WHERE mainId = ? AND nextId!=? " +
                     "UNION ALL " +
                     "SELECT nextId FROM mishu.snatchrelation WHERE mainId = ? AND nextId!=? ) a " +
                     "GROUP BY a.nextId HAVING COUNT(a.nextId)=1 ";
        try {
            return this.getJdbcTemplate().queryForList(sql, new Object[]{zhaobId, thisId, thisId, zhaobId}, String.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 这个方法不用 招标入ES与资质在一起
     */
    public void insertZhaobiaoEsNotice(EsNotice notice) {
        if (notice != null) {
           /* IdxZhaobiaoSnatch zhaobiaoDoc = new IdxZhaobiaoSnatch();
            zhaobiaoDoc.setId(notice.getUuid());    //
            zhaobiaoDoc.setSnatchId(notice.getUuid());  //公告id
            zhaobiaoDoc.setUrl(notice.getUrl());
            zhaobiaoDoc.setTitle(notice.getTitle());
            zhaobiaoDoc.setContent(notice.getContent());
            zhaobiaoDoc.setPbMode(notice.getDetail().getPbMode());
            zhaobiaoDoc.setGsDate(notice.getOpenDate());
            zhaobiaoDoc.setProjDq(notice.getDetail().getProjDq());
            zhaobiaoDoc.setProjType(notice.getDetail().getProjType());
            zhaobiaoDoc.setTbEndDate(notice.getDetail().getTbEndDate());
            zhaobiaoDoc.setProjSum(notice.getDetail().getProjSum());
            zhaobiaoDoc.setBmSite(notice.getDetail().getBmSite());

            zhaobiaoDoc.setProvince(notice.getProvince());  //省
            zhaobiaoDoc.setCity(notice.getCity());  //市
            zhaobiaoDoc.setCounty(notice.getCounty());  //地区
            zhaobiaoDoc.setType(notice.getType());  //类型:0招标信息，招标变更1，中标结果2
            zhaobiaoDoc.setOtherType(notice.getOtherType());   //公告类型
            zhaobiaoDoc.setBiddingType(notice.getBiddingType());
            zhaobiaoDoc.setTableName(notice.getTableName());    //表名

            elaticsearchUtils.update(zhaobiaoDoc);*/
        }
    }

    @Override
    /**
     * 因为有资质问题所以这里局部更新
     */
    public void updateZhaobiaoEsNotice(EsNotice notice) {
        if (notice != null) {
            UpdateRequestBuilder updateBuilder = client.prepareUpdate("bdd_zhaobiao", "zhaobiao_snatch", notice.getUuid());
            Map<String, Object> map = new HashMap<>();
            map.put("url", notice.getUrl());
            String gsDate = notice.getOpenDate();
            if (gsDate != null && !gsDate.trim().equals("")) {
                gsDate = gsDate.replaceAll("[^\\d]", "");    //ES查询不需要-
            }
            map.put("gsDate", gsDate);
            map.put("title", notice.getTitle());
            map.put("pbMode", notice.getDetail().getPbMode());
            map.put("projDq", notice.getDetail().getProjDq());
            map.put("projType", notice.getDetail().getProjType());
            String tbEndDate = notice.getDetail().getTbEndDate();
            if (tbEndDate != null && !tbEndDate.trim().equals("")) {
                tbEndDate = tbEndDate.replaceAll("[^\\d]", "");    //ES查询不需要-
            }
            map.put("tbEndDate", tbEndDate);
            String projSum = notice.getDetail().getProjSum();
            if (projSum != null && !projSum.trim().equals("")) {
                map.put("projSum", Double.parseDouble(projSum));
            }

//            map.put("content", notice.getContent());
            map.put("biddingType", notice.getBiddingType());
            map.put("bmSite", notice.getDetail().getBmSite());
            String bmEndDate = notice.getDetail().getBmEndDate();
            if (bmEndDate != null && !bmEndDate.trim().equals("")) {
                bmEndDate = bmEndDate.replaceAll("[^\\d]", "");  //ES查询不需要-
            }
            map.put("bmEndDate", bmEndDate);
            map.put("otherType", notice.getOtherType());

            if (notice.getEdit() != null)
                map.put("edit", notice.getEdit());
            else
                map.put("edit", 0);

            map.put("province", notice.getDetail().getProvince());
            map.put("city", notice.getDetail().getCity());
            map.put("county", notice.getDetail().getCounty());
            map.put("projXs", notice.getDetail().getProjXs());
            map.put("type", notice.getType());
            map.put("tableName", notice.getTableName());

            String json = JSON.toJSONString(map);
            updateBuilder.setDoc(json).get();
        }
    }


    @Override
    public void insertZhongbiaoEsNotice(EsNotice notice) {
        if (notice != null) {
            IdxZhongbiaoSnatch zhongbiaoDoc = new IdxZhongbiaoSnatch();
            zhongbiaoDoc.setId(notice.getUuid());   //
            zhongbiaoDoc.setSnatchId(notice.getUuid());  //公告id
            zhongbiaoDoc.setUrl(notice.getUrl());
            zhongbiaoDoc.setTitle(notice.getTitle());
            zhongbiaoDoc.setProjDq(notice.getDetailZhongBiao().getProjDq());
            zhongbiaoDoc.setProjType(notice.getDetailZhongBiao().getProjType());
            zhongbiaoDoc.setPbMode(notice.getDetailZhongBiao().getPbMode());
            String projSum = notice.getDetailZhongBiao().getProjSum();
            if (MyStringUtils.isNotNull(projSum)) {
                zhongbiaoDoc.setProjSum(Double.parseDouble(projSum));
            }
            String oneOffer = notice.getDetailZhongBiao().getOneOffer();
            if (MyStringUtils.isNotNull(oneOffer)) {
                zhongbiaoDoc.setOneOffer(Double.parseDouble(oneOffer));
            }
            if (notice.getEdit() != null) {
                zhongbiaoDoc.setEdit(notice.getEdit());
            }else {
                zhongbiaoDoc.setEdit(0);
            }
//            zhongbiaoDoc.setContent(notice.getContent());
            String gsDate = notice.getDetailZhongBiao().getGsDate();
            if (gsDate != null && !gsDate.trim().equals("")) {
                gsDate = gsDate.replaceAll("[^\\d]", "");    //ES查询不需要-
            }
            zhongbiaoDoc.setGsDate(gsDate);
            zhongbiaoDoc.setProjXs(notice.getDetailZhongBiao().getProjXs());
            zhongbiaoDoc.setOneName(notice.getDetailZhongBiao().getOneName());  //中标企业
            zhongbiaoDoc.setProvince(notice.getProvince());  //省
            zhongbiaoDoc.setCity(notice.getCity());  //市
            zhongbiaoDoc.setCounty(notice.getCounty());  //地区
            zhongbiaoDoc.setType(notice.getType()); //类型:0招标信息，招标变更1，中标结果2
            zhongbiaoDoc.setTableName(notice.getTableName());    //表名
            elaticsearchUtils.saveOrUpdate(zhongbiaoDoc);
        }
    }

    /**
     * 以前的招标公告去重规则
     *
     * @param notice
     * @return
     */
    public boolean oldZhaoBiaoReption(EsNotice notice) {

        EsNotice obj = null;
        notice.setTitle(notice.getTitle().replaceAll(" ", ""));
        obj = queryNoticeByTitle(notice.getTitle(), notice.getOpenDate(), notice.getType());//根据原始标题查询公示时间前后3天的
        if (obj == null) {//继续匹配处理后的标题
            String titleCond = notice.getTitle();
            titleCond = MyStringUtils.subZhaobiaoTile(titleCond);    //
            if (MyStringUtils.isNotNull(titleCond)) {
                obj = queryNoticeByTitle("%" + titleCond + "%", notice.getOpenDate(), notice.getType());//根据标题查询公示时间前后3天的
                if (obj != null) {
                    String tempTitle = obj.getTitle();
                    if (tempTitle.indexOf("招标") != -1) {    //
                        List<String> keyWords = MyStringUtils.titleKWordList();
                        for (String key : keyWords) {
                            if (notice.getTitle().contains(key)) {
                                obj = null;
                            }
                        }
                    }
                }
            }
        }
        boolean bl = false;
        AnalyzeDetail zhaobiaoDetail = notice.getDetail();  //维度
        EsNotice repeatNotice = null;
        repeatNotice = matchAnalyzeZhaobiao(zhaobiaoDetail);    //根据维度查询公告
        if (obj != null && repeatNotice != null) {   //标题维度同时满足时去重
            //有重复数据
            handleRepeatZhaobiao(obj, notice);  //更新或去重
            bl = false;
        } else {    //没有重复数据（已放宽规则）
            handleNotRepeatZhaobiao(notice);
            bl = true;
        }
        if (repeatNotice == null) {
            zhaobiaoDetail.setGsDate(notice.getOpenDate());
            insertZhaobiaoAnalyzeDetail(zhaobiaoDetail, notice);
        }
        return bl;
    }

    /**
     * 以前的中标公告去重规则
     *
     * @param notice
     * @return
     */
    public boolean oldZhongBiaoReption(EsNotice notice) {
        EsNotice obj = null;
        obj = queryNoticeByTitle(notice.getTitle(), notice.getOpenDate(), notice.getType());//根据原始标题查询公示时间前后3天的
        if (obj == null) {//继续匹配关联的标题
            String titleCond = notice.getTitle();
            int titleEndIdx = titleCond.lastIndexOf("中标公告");
            if (titleEndIdx != -1) {
                titleCond = titleCond.substring(0, titleEndIdx);
            }
            titleCond = titleCond.replaceAll(" ", "");    //去空格
            if (MyStringUtils.isNotNull(titleCond)) {//过滤关键字后为空串的直接插入
                obj = queryNoticeByTitle("%" + titleCond + "%", notice.getOpenDate(), notice.getType());//根据标题查询公示时间前后3天的
            }
        }
        boolean bl = false;
        AnalyzeDetailZhongBiao zhongBiaoDetail = notice.getDetailZhongBiao();
        EsNotice repeatNotice = null;
        if (obj == null) {  //标题不符合去重规则走维度去重
            repeatNotice = matchAnalyzeZhongBiao(zhongBiaoDetail);
            if (repeatNotice == null) { //没有重复数据(没有相同维度)
                handleNotRepeatZhaobiao(notice);
                bl = true;
            } else {
                handleRepeatZhaobiao(repeatNotice, notice);
                bl = false;
            }
        } else {    //有重复数据
            handleRepeatZhaobiao(obj, notice);
            bl = false;
        }

        if (repeatNotice == null) {
            zhongBiaoDetail.setGsDate(notice.getOpenDate());
            insertZhongbiaoAnalyzeDetail(zhongBiaoDetail, notice);
        }
        return bl;
    }

    /**
     * 按ID删除ES数据
     *
     * @param type
     * @param id
     */
    private void delEsNotice(Integer type, String id) {
        if (type == 2)
            elaticsearchUtils.deleteById(IdxZhongbiaoSnatch.class, id);
        else
            elaticsearchUtils.deleteById(IdxZhaobiaoSnatch.class, id);
    }


    /**
     * 两条url是否一条为郴州一条为省网
     * @param url1
     * @param url2
     * @return
     */
    private boolean hasCzAndHnUrl (String url1,String url2) {
        return (url1.contains(bidding) && url2.contains(czggzy)) || (url2.contains(czggzy) && url1.contains(bidding));
    }


    /**
     * url查询相关公告（湘西、张家界、邵阳、湘潭）
     * @param urlKey
     * @return
     */
    @Override
    public List<String> querysLikeUrl (String urlKey,String source) {
        String sql = "SELECT id FROM mishu.snatchurl WHERE url LIKE ? AND isShow = 0 AND source=?";
        return this.getJdbcTemplate().queryForList(sql,String.class, "%" + urlKey + "%",source);
    }

    /**
     * url 查询相关公告（长沙）
     * @param urlKeys
     * @return
     */
    @Override
    public List<String> querysLikeUrl (List<String> urlKeys,String source){
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(this.getJdbcTemplate());
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("urlKeys", urlKeys);
        params.put("source",source);

        String sql = "SELECT id FROM mishu.snatchurl WHERE url IN (:urlKeys) and source=:source";
        return namedParameterJdbcTemplate.queryForList(sql,params,String.class);
    }

    @Override
    public String queryThisId(String thisUrl,String source){
        String sql = "SELECT id FROM "+RouteUtils.routeTableName("mishu.snatchurl",source)+" WHERE url = ? AND isShow = 0";
        try {
            return this.getJdbcTemplate().queryForObject(sql,String.class,thisUrl);
        } catch (EmptyResultDataAccessException e) {
            return "";
        } catch (IncorrectResultSizeDataAccessException e) {
            return this.getJdbcTemplate().queryForList(sql,String.class,thisUrl).get(0);
        }
    }

    @Override
    @Cacheable(value = "similarityNotice", key="#no.openDate+#websiteUrl+#tempTitle")
    public List<Map<String,Object>> querySimilarityNotice (EsNotice no, String websiteUrl, String tempTitle) {
        int dayRegion = 30;
        int limitCount = 1000;
        StringBuffer sql = new StringBuffer("SELECT id,title FROM " + RouteUtils.routeTableName("mishu.snatchurl", no) + " WHERE isShow = 0 ");
        sql.append(" AND openDate BETWEEN DATE_SUB(?,INTERVAL "+dayRegion+" DAY) " +
                     " AND DATE_SUB(?,INTERVAL -"+dayRegion+" DAY) ");
        if (MyStringUtils.isNotNull(tempTitle)){
            sql.append(" and title LIKE '"+tempTitle+"' ");
        }
        if(websiteUrl!=null){
            sql.append(" AND url like '"+websiteUrl+"%' ");
        }
        sql.append(" order by opendate desc limit "+limitCount);
        return this.getJdbcTemplate().queryForList(sql.toString(),no.getOpenDate(),no.getOpenDate());
    }


    @Override
    public List<String> queryRelationNextIds (String nextId) {
        String sql = "SELECT mainId AS id FROM mishu.snatchrelation WHERE nextId = ? " +
                "UNION SELECT nextId AS id FROM mishu.snatchrelation WHERE mainId = ? ";
        return this.getJdbcTemplate().queryForList(sql,String.class, nextId,nextId);
    }


    @Override
    public Map<String,String> batchInsertRelation (String thisId, List<String> nextIds){
        String sql = "INSERT INTO mishu.snatchrelation (mainId, nextId, lastChangeTime,relationMethod)VALUES(?,?,NOW(),0)";
        Map<String,String> map = new HashMap<String,String>();
        this.getJdbcTemplate().batchUpdate(sql,new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1,Long.valueOf(thisId));
                ps.setLong(2,Long.valueOf(nextIds.get(i)));
                map.put("mainId",thisId);
                map.put("nextId",nextIds.get(i));
                // 清理关联信息缓存
                redisClear.clearGonggaoRelation(thisId);
                redisClear.clearGonggaoRelation(nextIds.get(i));
                logger.info("清理关联信息缓存：[mainId：" + thisId + "][nextId:" + nextIds.get(i) + "]");
            }
            @Override
            public int getBatchSize() {
                return nextIds.size();
            }
        });
        logger.info("batchInsertRelation finished...");
        return map;
    }

    private String queryNoticeListPre =  "SELECT a.id uuid,a.title,a.url,a.openDate," +
            "ifnull(a.province,'') province,ifnull(a.city,'') city,ifnull(a.county,'') county,ifnull(type,0) type," +
            "ifnull(rank,0) rank,ifnull(redisId,0) redisId,ifnull(websitePlanId,0) websitePlanId," +
            "ifnull(tableName,'') tableName,b.content,a.otherType,a.uuid snatchNumber,a.biddingType,a.businessType,a.edit edit,a.source ";

    private List<EsNotice> wrapperNoticeList(List<Map<String, Object>> list){
        List<EsNotice> noticeList = new ArrayList<EsNotice>();
        if (list.size() > 0) {
            for (Map<String, Object> map : list) {
                EsNotice obj = new EsNotice();
                obj.setUuid(String.valueOf(map.get("uuid")));
                obj.setTitle(String.valueOf(map.get("title")));
                obj.setUrl(String.valueOf(map.get("url")));
                obj.setOpenDate(String.valueOf(map.get("openDate")));
                obj.setContent(String.valueOf(map.get("content")));
                obj.setProvince(String.valueOf(map.get("province")));
                obj.setCity(String.valueOf(map.get("city")));
                obj.setCounty(String.valueOf(map.get("county")));
                obj.setType(Integer.valueOf(String.valueOf(map.get("type"))));
                obj.setRank(Integer.valueOf(String.valueOf(map.get("rank"))));
                obj.setRedisId(Integer.valueOf(String.valueOf(map.get("redisId"))));
                obj.setWebsitePlanId(Integer.valueOf(String.valueOf(map.get("websitePlanId"))));
                obj.setTableName(String.valueOf(map.get("tableName")));
                obj.setOtherType(String.valueOf(map.get("otherType")));
                obj.setSnatchNumber(String.valueOf(map.get("snatchNumber")));
                obj.setBiddingType(String.valueOf(map.get("biddingType")));
                obj.setBusinessType(String.valueOf(map.get("businessType") == null ? "" : map.get("businessType")));
                obj.setEdit(map.get("edit") == null ? 0 : Integer.parseInt(String.valueOf(map.get("edit"))));
                obj.setSource(String.valueOf(map.get("source")));
                noticeList.add(obj);
            }
        }
        return noticeList;
    }

    @Override
    public synchronized List<EsNotice> queryNoticeList(String tempTile, String url, EsNotice n) {
        String sql = queryNoticeListPre +
                "FROM "+RouteUtils.routeTableName("mishu.snatchurl",n)+" a, "+RouteUtils.routeTableName("mishu.snatchurlcontent",n)+" b " +
                "WHERE a.id = b.snatchUrlId AND a.title LIKE ? AND url NOT LIKE ? AND a.isShow = 0 AND a.type = ? #city " +
                "AND a.openDate BETWEEN DATE_SUB(?,INTERVAL 3 DAY) AND DATE_SUB(?,INTERVAL -3 DAY)";
        List<Map<String, Object>> list = new ArrayList<>();
        if (MyStringUtils.isNotNull(n.getCity())) {
            sql = sql.replace("#city","AND a.city = ? ");
            list = this.getJdbcTemplate().queryForList(sql,tempTile,"%"+url+"%",n.getType(),n.getCity(), n.getOpenDate(), n.getOpenDate());
        } else {
            sql = sql.replace("#city","");
            list = this.getJdbcTemplate().queryForList(sql,tempTile,"%"+url+"%",n.getType(),n.getOpenDate(),n.getOpenDate());
        }
        return wrapperNoticeList(list);

    }

    @Override
    public List<EsNotice> queryNoticeList(String url, EsNotice esNotice) {
        String sql = queryNoticeListPre  +
                "FROM "+RouteUtils.routeTableName("mishu.snatchurl",esNotice)+" a, "+RouteUtils.routeTableName("mishu.snatchurlcontent",esNotice)+" b " +
                "WHERE a.id = b.snatchUrlId AND url NOT LIKE ? AND a.isShow = 0 AND a.type = ? #city " +
                "AND a.openDate BETWEEN DATE_SUB(?,INTERVAL 3 DAY) AND DATE_SUB(?,INTERVAL -3 DAY)";
        List<Map<String, Object>> list = new ArrayList<>();
        if (MyStringUtils.isNotNull(esNotice.getCity())) {
            sql = sql.replace("#city","AND a.city = ? ");
            list = this.getJdbcTemplate().queryForList(sql,"%"+url+"%",esNotice.getType(),esNotice.getCity(),esNotice.getOpenDate(),esNotice.getOpenDate());
        } else {
            sql = sql.replace("#city","");
            list = this.getJdbcTemplate().queryForList(sql,"%"+url+"%",esNotice.getType(),esNotice.getOpenDate(),esNotice.getOpenDate());
        }
        return wrapperNoticeList(list);
    }

    @Override
    public void updateSnatchUrlCert (Integer id,Integer historyId) {
        String sql = "UPDATE mishu.snatch_url_cert SET contId = ? WHERE contId = ? ";
        this.getJdbcTemplate().update(sql, id,historyId);
    }

    @Override
    public boolean isNoticeExists(EsNotice esNotice) {
        String CountNoticeUrlSql = "SELECT COUNT(*) FROM "+RouteUtils.routeTableName("mishu.snatchurl",esNotice)+" WHERE url = ? and openDate = ?";
        int NoticeCount = queryForInt(CountNoticeUrlSql, new Object[]{esNotice.getUrl(), esNotice.getOpenDate()});
        return NoticeCount > 0;
    }
}
