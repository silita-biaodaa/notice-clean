package com.silita.biaodaa.service;

import com.silita.biaodaa.common.elastic.indexes.IdxZhaobiaoSnatch;
import com.silita.biaodaa.common.elastic.model.ElasticEntity;
import com.silita.biaodaa.dao.SnatchpressMapper;
import com.silita.biaodaa.model.Snatchpress;
import com.silita.biaodaa.utils.CommonUtil;
import com.snatch.model.EsNotice;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.stereotype.Service;

import java.lang.ref.Reference;
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
    ZhService ZhService;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;



    /**
     * 招标公告资质匹配
     */
    public List insertUrlCert(int id , EsNotice notice) {
        //公告信息
        Snatchpress snatchpress = snatchpressMapper.getSnatchpress(id);
        SoftReference<String> contentRef = new SoftReference<String>(snatchpress.getPress());
        List<Map<String,Object>> zh =new ArrayList<Map<String,Object>>();

        List<Map<String, Object>> list = ZhService.queryzh();
        for (int i = 0; i < list.size(); i++) {
            int num = contentRef.get().indexOf(list.get(i).get("name").toString());
            if(num != -1){
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("name", list.get(i).get("name"));//匹配公告资质最全名称
                map.put("uuid", list.get(i).get("mainUUid"));//匹配资质类型id
                map.put("rank", list.get(i).get("rank").toString());//等级
                String str="";
                if(contentRef.get().indexOf("安全生产许可证") != -1){//查找安全生产许可证
                    map.put("licence","yes");//有安全生产许可证条件
                }
                if(num>5){
                    str= contentRef.get().substring(num-5,num);//查找和
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
        list=null;

        if (zh.size() == 0) {   //匹配不到的别名存入Unanalysis_aptitude表
            String zzRank = "";
            String rangeHtml = "";
            List<Map<String, Object>> arList = snatchpressMapper.queryAnalyzeRangeByField("zzRank");
            for (int k = 0; k < arList.size(); k++) {
                String start = arList.get(k).get("rangeStart").toString();
                String end = arList.get(k).get("rangeEnd").toString();
                int indexStart = 0;
                int indexEnd = 0;
                if (!"".equals(start)) {
                    indexStart = contentRef.get().indexOf(start);//范围开始位置
                }
                if (!"".equals(end)) {
                    indexEnd = contentRef.get().lastIndexOf(end);
                }
                if (indexStart != -1 && indexEnd != -1) {
                    if (indexEnd > indexStart) {
                        rangeHtml = contentRef.get().substring(indexStart, indexEnd);
                        rangeHtml = rangeHtml.replaceAll("\\s*", "");    //去空格
                        if (rangeHtml.length() > 30) {
                            rangeHtml = rangeHtml.substring(0, 30);
                        }
                        zzRank = rangeHtml.replace("颁发的", "").replace("核发的", "").replace("具备", "").replace("具有", "");
                        if (zzRank.indexOf("级") == -1) {
                            zzRank = "";
                        }
                        if (zzRank.length() > 0) {
                            break;
                        }
                    }
                }
            }
            if (!"".equals(zzRank)) {
                String message = "";
                logger.info("#####为解析到" + zzRank + "#####");
                Map<String,Object> param = new HashMap<>();
                param.put("snatchUrlId",id);
                param.put("aptitude",zzRank);
                param.put("snatchContent",message);
                snatchpressMapper.insertUnanalysis_aptitude(param);
            }
        }
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
                    snatchpressMapper.insertSnatchUrlCert(param);
                }
            }
        }

        insertUrlBuild(id,contentRef);
        //建造师资格匹配
        logger.info("招标公告取消插入es");
//        try{
//            insertZhaobiaoEsNotice(notice);	//ES
//        } catch (Exception e) {
//            logger.error("@@@@ES招标入库报错" + e);
//        }
        return zh;
    }

    /**
     * 招标公告建造师匹配
     */
    public void insertUrlBuild(int id,Reference<String> contentRef) {
        String zz="";
        String muuid="";
        String licence="";//安全生产许可证
        int rank=0;
        String uuid="";
        String content = contentRef.get();
        int star = content.indexOf("注册建造师");
        if(star != -1){
            String str= content.substring(star-20,star+50);
            List<Map<String,Object>> list= ZhService.getBuildZhList();
            for (int i = 0; i < list.size(); i++) {
                int num =str.indexOf(list.get(i).get("name").toString());
                if(num != -1){
                    if(list.get(i).get("name").toString().length()>zz.length()){
                        zz = list.get(i).get("name").toString();
                        muuid = list.get(i).get("mainUUid").toString();
                        rank = Integer.parseInt(list.get(i).get("rank").toString());
                    }
                    if(str.indexOf("B") != -1 ||str.indexOf("Ｂ")!=-1){
                        licence="B";
                    }else if(str.indexOf("A") != -1 ||str.indexOf("Ａ")!=-1){
                        licence="A";
                    }
                }
            }


            Map<String,Object> paramZh = new HashMap<>();
            paramZh.put("muuid",muuid);
            paramZh.put("rank",rank);
            List<String> all = snatchpressMapper.getBuildZh(paramZh);


            for (int j = 0; j < all.size(); j++) {
                if(uuid.equals("")) {
                    //zzall +=all.get(j).get("name").toString();
                    uuid +=all.get(j);
                }
                else{
                    //zzall +=","+all.get(j).get("name").toString();
                    uuid +=","+all.get(j);
                }
            }
            Map<String,Object> param = new HashMap<>();
            param.put("contId",id);
            param.put("certificate",zz);
            param.put("certificateUUid",uuid);
            param.put("licence",licence);
            snatchpressMapper.insertSnatchUrlBuild(param);
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
