package com.silita.biaodaa.common.elastic.model;

import java.util.List;

/**
 * Created by dh on 2017/10/30.
 */
public class EsResultInfo {

    private long total=-1;

    private List<? extends ElasticEntity> dataList=null;

    public EsResultInfo(long total, List<ElasticEntity> dataList){
        this.total = total;
        this.dataList = dataList;
    }

    public long getTotal() {
        return total;
    }

    public List<? extends ElasticEntity> getDataList() {
        return dataList;
    }
}
