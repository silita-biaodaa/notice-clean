package com.silita.biaodaa.service;

import com.silita.biaodaa.common.Constant;
import com.silita.biaodaa.common.config.CustomizedPropertyConfigurer;
import com.silita.biaodaa.common.elastic.indexes.IdxZhaobiaoSnatch;
import com.silita.biaodaa.common.elastic.model.ElasticEntity;
import com.silita.biaodaa.dao.CleanMapper;
import com.silita.biaodaa.dao.SnatchpressMapper;
import com.silita.biaodaa.utils.CommonUtil;
import com.silita.biaodaa.utils.RouteUtils;
import com.snatch.model.EsNotice;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.stereotype.Service;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QuaParseService {

    Logger logger = Logger.getLogger(QuaParseService.class);

    @Autowired
    SnatchpressMapper snatchpressMapper;

    @Autowired
    ZhService zhService;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private CleanMapper cleanMapper;

    private void hitCertInfo(Map<String, Object> hitMap,String content, int nIdx, List<Map<String,Object>> zh){
        if(nIdx != -1){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("name", hitMap.get("name"));//匹配公告资质最全名称
            map.put("uuid", hitMap.get("mainUUid"));//匹配资质类型id
            map.put("rank", hitMap.get("rank").toString());//等级
            String str="";
            if(content.indexOf("安全生产许可证") != -1){//查找安全生产许可证
                map.put("licence","yes");//有安全生产许可证条件
            }
            if(nIdx>5){
                str= content.substring(nIdx-5,nIdx);//查找和
            }
            if(str.indexOf("和") !=-1){
                map.put("type", "AND");//和
            }else{
                map.put("type", "OR");//或
            }

            for(int j=0;j<zh.size();j++ ){
                if(map.get("uuid").equals(zh.get(j).get("uuid"))){
                    if(Integer.parseInt(map.get("rank").toString())>Integer.parseInt(zh.get(j).get("rank").toString())){
                        zh.remove(j);
                        zh.add(j,map);
                    }
                    map=null;
                    break;
                }
            }
            if(map!=null){
                zh.add(map);
            }
        }
    }

    /**
     * 招标公告资质匹配
     */
    @Deprecated
    public List insertUrlCert(EsNotice notice) {
        int id = Integer.parseInt(notice.getUuid());
        String source = notice.getSource();
        SoftReference<String> contentRef = new SoftReference<String>(notice.getPressContent());
        List<Map<String,Object>> zh =new ArrayList<Map<String,Object>>();//命中的资质别名集合

        List<Map<String, Object>> list = zhService.queryzh();
        for (int i = 0; i < list.size(); i++) {
            int nIdx = contentRef.get().indexOf(list.get(i).get("name").toString());

            //命中别名的资质进入集合筛选
            hitCertInfo(list.get(i),contentRef.get(),nIdx,zh);
        }
        list=null;

        //插入匹配的资质记录
        certsAliaMatch(zh,id,source);
        return zh;
    }

    /**
     * 招标公告资质匹配（优化方案）
     */
    public List insertUrlCertOpt(EsNotice notice) {
        int id = Integer.parseInt(notice.getUuid());
        String source = notice.getSource();
        SoftReference<String> contentRef = new SoftReference<String>(notice.getPressContent());
        List<Map<String,Object>> zh =new ArrayList<Map<String,Object>>();//命中的资质别名集合

        List<Map<String, Object>> quaClasses = zhService.queryQuaCategory(Constant.preSize);
        logger.debug("quaClasses:"+quaClasses.size());
        List<String> matchPreList = new ArrayList<>();

        for (Map<String, Object> quaClass :quaClasses) {
            String pName = quaClass.get("preName").toString();
            int nIdx = contentRef.get().indexOf(pName);
            if(nIdx != -1){
                matchPreList.add(pName);
            }
        }
        quaClasses=null;

        Map<String,List<Map<String, Object>>> sortAliaMap = zhService.sortQuaAlias(Constant.preSize);
        logger.debug("sortAliaMap:"+sortAliaMap.size());
        List<Map<String, Object>> aliaList = new ArrayList<>();//需要遍历的别名集合
        for(String hitPreName: matchPreList){
          if(sortAliaMap.containsKey(hitPreName)){
              aliaList.addAll(sortAliaMap.get(hitPreName));
          }
        }

        if(!aliaList.isEmpty()) {
            for(int i = 0; i<aliaList.size();i++){
                int nIdx = contentRef.get().indexOf(aliaList.get(i).get("name").toString());
                hitCertInfo(aliaList.get(i), contentRef.get(), nIdx, zh);
            }
        }

        //插入匹配的资质记录
        certsAliaMatch(zh,id,source);
        return zh;
    }


    private void certsAliaMatch(List<Map<String,Object>> zh,int id,String source){
        boolean hasZz= false;
        for (int k = 0; k < zh.size(); k++) {
            Map<String, Object> mapname = snatchpressMapper.getAptitudeDictionary(zh.get(k).get("uuid").toString());
            if(mapname !=null){
                if(mapname.get("majorName")!=null && zh.get(k).get("rank") !=null){
                    String certificate=mapname.get("majorName").toString()+CommonUtil.spellRank(zh.get(k).get("rank").toString());	//规范化资质名称
                    String uuid = CommonUtil.spellUuid(zh.get(k).get("uuid").toString(), zh.get(k).get("rank").toString());	//拼接资质uuid
                    Map<String,Object> param = new HashMap<>();
                    param.put("contId",id);
                    param.put("certificate",certificate);
                    param.put("certificateUUid",uuid.replaceAll("'", ""));
                    param.put("type",zh.get(k).get("type"));
                    param.put("licence",zh.get(k).get("licence"));
                    param.put("source",source);
                    if (source==null || source.equals(Constant.HUNAN_SOURCE)) {
                        param.put("certTable","snatch_url_cert");
                    }else{
                        param.put("certTable","snatch_url_cert_others");
                    }
                    snatchpressMapper.insertSnatchUrlCert(param);
                    hasZz=true;
                }
            }
        }

        if(hasZz){
            //有资质，更新排序状态
            Map param = new HashMap<String, Object>();
            param.put("id",id);
            param.put("tableName", RouteUtils.routeTableName("mishu.snatchurl", source));
            param.put("orderNo",2);
            cleanMapper.updateNoticePx(param);
        }
    }

    /**
     * 更新公告对应的es信息（仅湖南）
     * @param notice
     */
    public void updateEsInfo(EsNotice notice){
        String source = notice.getSource();
        //仅湖南公告更新es
        if (source==null || source.equals(Constant.HUNAN_SOURCE)) {
            String insertEs = (String) CustomizedPropertyConfigurer.getContextProperty("es.data.send");
            if (insertEs != null && insertEs.equals("true")) {
                try {
                    insertZhaobiaoEsNotice(notice);
                    logger.info("湖南招标公告插入es完成");
                } catch (Exception e) {
                    logger.error("湖南招标公告插入es异常" + e, e);
                }
            } else {
                logger.info("湖南招标公告取消插入es");
            }
        }else{
            //全国公告暂无es操作
        }
    }


    public void insertZhaobiaoEsNotice(EsNotice notice) {
        if(notice != null) {
            IdxZhaobiaoSnatch zhaobiaoDoc = new IdxZhaobiaoSnatch();
            zhaobiaoDoc.setId(notice.getUuid());    //id
            zhaobiaoDoc.setSnatchId(notice.getUuid());  //公告id
            zhaobiaoDoc.setUrl(notice.getUrl());
            String gsDate = notice.getOpenDate();
            if(gsDate != null && !gsDate.trim().equals("")) {
                gsDate = gsDate.replaceAll("[^\\d]","");    //ES查询不需要-
            }
            zhaobiaoDoc.setGsDate(gsDate);
            zhaobiaoDoc.setTitle(notice.getTitle());

            List<Map<String, Object>> list = snatchpressMapper.getSnatchUrlCert(notice.getUuid());
            String zzTemp = "";

            for (int i = 0; i < list.size(); i++) {
                String temp = "";
                if (i == list.size() - 1) {
                    temp = (String) list.get(i).get("certificate");
                } else {
                    temp = list.get(i).get("certificate") + ",";
                }
                zzTemp = zzTemp + temp;
            }
//			zzTemp = zzTemp.replaceAll("壹", "一").replaceAll("贰", "二").replaceAll("叁", "三");

            String aptitudeName = "";
            if (list.size() > 0) {
                String uuid = (String) list.get(0).get("certificateUuid");
                if (uuid != "") {
                    uuid = uuid.substring(0, uuid.indexOf("/"));
                    List<String> aptitudeNameList = snatchpressMapper.getAptitudeDictionaryList(uuid);
                    if (aptitudeNameList.size() >= 1) {
                        aptitudeName = aptitudeNameList.get(0);
                    }
                }
            }

            zhaobiaoDoc.setAptitudeName(aptitudeName);	//总类型
            zhaobiaoDoc.setCertificate(zzTemp);	//标准名称资质
            zhaobiaoDoc.setPbMode(notice.getDetail().getPbMode());
            zhaobiaoDoc.setProjDq(notice.getDetail().getProjDq());
            zhaobiaoDoc.setProjType(notice.getDetail().getProjType());
            String tbEndDate = notice.getDetail().getTbEndDate();
            if(tbEndDate != null && !tbEndDate.trim().equals("")) {
                tbEndDate = tbEndDate.replaceAll("[^\\d]","");    //ES查询不需要-
            }
            zhaobiaoDoc.setTbEndDate(tbEndDate);
            String projSum = notice.getDetail().getProjSum();
            if(projSum != null && !projSum.trim().equals("")) {
                zhaobiaoDoc.setProjSum(Double.parseDouble(projSum));
            }

//			zhaobiaoDoc.setContent(notice.getContent());
            zhaobiaoDoc.setBiddingType(notice.getBiddingType());	//公告区分
            zhaobiaoDoc.setBmSite(notice.getDetail().getBmSite());
            String bmEndDate = notice.getDetail().getBmEndDate();
            if(bmEndDate != null && !bmEndDate.trim().equals("")) {
                bmEndDate = bmEndDate.replaceAll("[^\\d]","");  //ES查询不需要-
            }
            zhaobiaoDoc.setBmEndDate(bmEndDate);
            zhaobiaoDoc.setOtherType(notice.getOtherType());	//公告类型
            zhaobiaoDoc.setEdit(0);

            zhaobiaoDoc.setProvince(notice.getProvince());  //省
            zhaobiaoDoc.setCity(notice.getCity());  //市
            zhaobiaoDoc.setCounty(notice.getCounty());  //地区
            zhaobiaoDoc.setProjXs(notice.getDetail().getProjXs());
            zhaobiaoDoc.setType(notice.getType());  //类型:0招标信息，招标变更1，中标结果2
            zhaobiaoDoc.setTableName(notice.getTableName());    //表名

            saveOrUpdate(zhaobiaoDoc);
            zhaobiaoDoc=null;
        }
    }

    /**
     * 保存，更新单条索引
     * @param elasticEntity
     */
    public void saveOrUpdate(ElasticEntity elasticEntity){
        try {
            IndexQuery indexQuery = new IndexQueryBuilder().withId(String.valueOf(elasticEntity.getId())).withObject(elasticEntity).build();
            elasticsearchTemplate.index(indexQuery);
        }catch(Exception e){
            logger.error(e,e);
        }
    }






}
