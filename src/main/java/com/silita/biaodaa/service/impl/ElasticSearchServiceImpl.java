package com.silita.biaodaa.service.impl;

import com.silita.biaodaa.common.elastic.ElaticsearchUtils;
import com.silita.biaodaa.common.elastic.indexes.AliasZhaoBiaoSnatch;
import com.silita.biaodaa.common.elastic.indexes.AliasZhongBiaoSnatch;
import com.silita.biaodaa.dao.AptitudeDictionaryMapper;
import com.silita.biaodaa.dao.SnatchurlMapper;
import com.silita.biaodaa.dao.ZhongbiaoDetailMapper;
import com.silita.biaodaa.service.IElasticSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Create by IntelliJ Idea 2018.1
 * Company: silita
 * Author: gemingyi
 * Date: 2018-08-01:15:56
 */
@Service("elasticSearchService")
public class ElasticSearchServiceImpl implements IElasticSearchService {

    @Autowired
    private ElaticsearchUtils elaticsearchUtils;

    @Autowired
    private SnatchurlMapper snatchurlMapper;
    @Autowired
    private ZhongbiaoDetailMapper zhongbiaoDetailMapper;
    @Autowired
    private AptitudeDictionaryMapper aptitudeDictionaryMapper;

    @Override
    public void insertZhaoBiaoData(String openDate) {
        elaticsearchUtils.createIndex(AliasZhaoBiaoSnatch.class);
        elaticsearchUtils.createMapping(AliasZhaoBiaoSnatch.class);
        List<Map<String, Object>> list = snatchurlMapper.listESZhaoBiaoDateByOpenDate(openDate);
        int threadCount = 4;
        int every = list.size() / threadCount;
        final CountDownLatch latch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            new Thread(new writeEsZhaobiao(i * every, (i + 1) * every, latch, list)).start();
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insertZhongBiaoData(String openDate) {
//        elaticsearchUtils.deleteIndex("bdd_zhongbiao_18_03-30");
        elaticsearchUtils.createIndex(AliasZhongBiaoSnatch.class);
        elaticsearchUtils.createMapping(AliasZhongBiaoSnatch.class);
        List<Map<String, Object>> list = snatchurlMapper.listESZhongBiaoDateByOpenDate(openDate);
        int threadCount = 4;
        int every = list.size() / threadCount;
        final CountDownLatch latch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            new Thread(new writeEsZhongbiao(i * every, (i + 1) * every, latch, list)).start();
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    class writeEsZhaobiao implements Runnable {
        int start;
        int end;
        CountDownLatch latch;
        List<Map<String, Object>> list;

        public writeEsZhaobiao(int start, int end, CountDownLatch latch, List<Map<String, Object>> list) {
            this.start = start;
            this.end = end;
            this.latch = latch;
            this.list = list;
        }

        @Override
        public void run() {
            List<AliasZhaoBiaoSnatch> zhaobiaoList = new LinkedList<>();
            SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd");
            AliasZhaoBiaoSnatch zhaobiaoDoc = null;
            for (int i = start; i < end; i++) {
                zhaobiaoDoc = new AliasZhaoBiaoSnatch();
                zhaobiaoDoc.setId(list.get(i).get("id").toString());
                zhaobiaoDoc.setSnatchId(list.get(i).get("id").toString());
                zhaobiaoDoc.setUrl((String) list.get(i).get("url"));
                String gsDate = simple.format(list.get(i).get("openDate"));
                if (gsDate != null && !gsDate.trim().equals("")) {
                    gsDate = gsDate.replaceAll("[^\\d]", "");    //ES查询不需要-
                }
                zhaobiaoDoc.setGsDate(gsDate);
                zhaobiaoDoc.setTitle((String) list.get(i).get("title"));
                String certificate = (String) list.get(i).get("certificate");
                String aptitudeName = "";
                String uuid = (String) list.get(i).get("certificateUuid");
                if (uuid != null && !uuid.trim().equals("")) {
                    uuid = uuid.substring(0, uuid.indexOf("/"));
                    aptitudeName = aptitudeDictionaryMapper.getAptitudeNameByMajorUuid(uuid);
                }
                zhaobiaoDoc.setAptitudeName(aptitudeName);  //总类型
                zhaobiaoDoc.setCertificate(certificate);    //资质
                String pbMode = (String) list.get(i).get("pbMode");
                if (pbMode != null && !pbMode.trim().equals("")) {
                    pbMode = pbMode.replaceAll("Ⅰ", "一").replaceAll("Ⅱ", "二").replace("1", "一").replace("2", "二")
                            .replace("(", "").replace(")", "").replace("（", "").replace("）", "");
                }
                zhaobiaoDoc.setPbMode(pbMode);
                zhaobiaoDoc.setProjDq((String) list.get(i).get("projDq"));
                zhaobiaoDoc.setProjXs((String) list.get(i).get("projXs"));
                String projType = (String) list.get(i).get("projType");
                if (projType == null || projType.equals("")) {
                    projType = "其他";
                }
                zhaobiaoDoc.setProjType(projType);

                String tbEndDate = (String) list.get(i).get("tbEndDate");
                if (tbEndDate != null && !tbEndDate.trim().equals("")) {
                    tbEndDate = tbEndDate.replaceAll("[^\\d]", "");
                }
                zhaobiaoDoc.setTbEndDate(tbEndDate);

                String projSum = (String) list.get(i).get("projSum");
                if (projSum != null && !projSum.trim().equals("")) {
                    projSum = projSum.replaceAll("[^\\d.]", "");
                    if (projSum != null && !projSum.trim().equals("")) {
                        zhaobiaoDoc.setProjSum(Double.parseDouble(projSum));
                    }
                }
//                zhaobiaoDoc.setContent((String) list.get(i).get("press"));    //内容
                zhaobiaoDoc.setBiddingType(list.get(i).get("biddingType").toString());
                zhaobiaoDoc.setBmSite((String) list.get(i).get("bmSite"));

                String bmEndDate = (String) list.get(i).get("bmEndDate");
                if (bmEndDate != null && !bmEndDate.trim().equals("")) {
                    bmEndDate = bmEndDate.replaceAll("[^\\d]", "");
                }
                zhaobiaoDoc.setBmEndDate(bmEndDate);

                zhaobiaoDoc.setOtherType(list.get(i).get("otherType").toString());
                zhaobiaoDoc.setProvince((String) list.get(i).get("province"));
                zhaobiaoDoc.setCity((String) list.get(i).get("city"));
                zhaobiaoDoc.setCounty((String) list.get(i).get("county"));
                zhaobiaoDoc.setType(Integer.parseInt(list.get(i).get("type").toString()));
                zhaobiaoDoc.setTableName((String) list.get(i).get("tableName"));
                zhaobiaoDoc.setEdit(Integer.parseInt(list.get(i).get("edit").toString()));
                zhaobiaoList.add(zhaobiaoDoc);
                if (zhaobiaoList.size() == 200) {   //每次500条
                    elaticsearchUtils.multipleIndexing(zhaobiaoList);
                    zhaobiaoList.clear();
                }
            }
            if (zhaobiaoList.size() > 0) {   //剩下的
                elaticsearchUtils.multipleIndexing(zhaobiaoList);
                zhaobiaoList.clear();
            }
            latch.countDown();
        }
    }

    class writeEsZhongbiao implements Runnable {

        int start;
        int end;
        CountDownLatch latch;
        List<Map<String, Object>> list;

        public writeEsZhongbiao(int start, int end, CountDownLatch latch, List<Map<String, Object>> list) {
            this.start = start;
            this.end = end;
            this.latch = latch;
            this.list = list;
        }

        @Override
        public void run() {
            List<AliasZhongBiaoSnatch> zhongbiaoList = new LinkedList<>();
            SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd");
            AliasZhongBiaoSnatch zhongbiaoDoc = null;
            for (int i = start; i < end; i++) {
                zhongbiaoDoc = new AliasZhongBiaoSnatch();
                zhongbiaoDoc.setId(list.get(i).get("id").toString());
                zhongbiaoDoc.setSnatchId(list.get(i).get("id").toString());
                zhongbiaoDoc.setUrl((String) list.get(i).get("url"));
                zhongbiaoDoc.setTitle((String) list.get(i).get("title"));
                zhongbiaoDoc.setProjDq((String) list.get(i).get("projDq"));
                zhongbiaoDoc.setProjType((String) list.get(i).get("projType"));
                String pbMode = (String) list.get(i).get("pbMode");
                if (pbMode != null && !pbMode.trim().equals("")) {
                    pbMode = pbMode.replaceAll("Ⅰ", "一").replaceAll("Ⅱ", "二").replace("1", "一").replace("2", "二")
                            .replace("(", "").replace(")", "").replace("（", "").replace("）", "");
                }
                zhongbiaoDoc.setPbMode(pbMode);
                String projSum = (String) list.get(i).get("projSum");
                if (projSum != null && !projSum.trim().equals("")) {
                    projSum = projSum.replaceAll("[^\\d.]", "");
                    if (projSum != null && !projSum.trim().equals("")) {
                        zhongbiaoDoc.setProjSum(Double.parseDouble(projSum));
                    }
                }
//                zhongbiaoDoc.setContent((String) list.get(i).get("press"));   内容
                String gsDate = simple.format(list.get(i).get("openDate"));
                if (gsDate != null && !gsDate.trim().equals("")) {
                    gsDate = gsDate.replaceAll("[^\\d]", "");    //ES查询不需要-
                }
                zhongbiaoDoc.setGsDate(gsDate);
                zhongbiaoDoc.setProjXs((String) list.get(i).get("projXs"));
                int oneNameLength = zhongbiaoDetailMapper.getOneNameLength((Integer) list.get(i).get("id"));
                if (oneNameLength > 18) {
                    zhongbiaoDoc.setOneName(zhongbiaoDetailMapper.getMegerOneName((Integer) list.get(i).get("id")));
                } else {
                    zhongbiaoDoc.setOneName((String) list.get(i).get("oneName"));
                }
                zhongbiaoDoc.setProvince((String) list.get(i).get("province"));
                zhongbiaoDoc.setCity((String) list.get(i).get("city"));
                zhongbiaoDoc.setCounty((String) list.get(i).get("county"));
                zhongbiaoDoc.setType(Integer.parseInt(list.get(i).get("type").toString()));
                zhongbiaoDoc.setTableName((String) list.get(i).get("tableName"));
                zhongbiaoDoc.setEdit(Integer.parseInt(list.get(i).get("edit").toString()));
                zhongbiaoList.add(zhongbiaoDoc);
                if (zhongbiaoList.size() == 200) {  //每次1000条
                    elaticsearchUtils.multipleIndexing(zhongbiaoList);
                    zhongbiaoList.clear();
                }
            }
            if (zhongbiaoList.size() > 0) {  //剩下的
                elaticsearchUtils.multipleIndexing(zhongbiaoList);
                zhongbiaoList.clear();
            }
            latch.countDown();
        }
    }
}
