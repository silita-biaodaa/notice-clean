package com.silita.biaodaa.common.elastic.indexes;

import com.silita.biaodaa.common.elastic.model.ElasticEntity;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * Created by gmy on 2017/10/24.
 */
//@Document(indexName = "bdd_zhongbiao", type = "zhongbiao_snatch", shards = 4, replicas = 1, indexStoreType = "memory", refreshInterval = "-1")
@Document(indexName = "bdd_zhongbiao", type = "zhongbiao_snatch", shards = 4, replicas = 1, refreshInterval = "1")
public class IdxZhongbiaoSnatch extends ElasticEntity {
    private String snatchId;
    private String url;
//    @Field(type = FieldType.String, indexAnalyzer="ik", index = FieldIndex.analyzed, searchAnalyzer="ik", store = true)
    @Field(type = FieldType.String, index = FieldIndex.not_analyzed, store = true)
    private String title;  //公告标题
    @Field(type = FieldType.String, indexAnalyzer="ik", searchAnalyzer="ik", store = true)
    private String projDq;  //项目地区
    @Field(type = FieldType.String, indexAnalyzer="ik", searchAnalyzer="ik", store = true)
    private String projType;    //项目类型
    @Field(type = FieldType.String, indexAnalyzer="ik", index = FieldIndex.analyzed, searchAnalyzer="ik", store = true)
    private String pbMode;  //评标办法
    @Field(type = FieldType.Double, store = true)
    private Double projSum;  //项目金额
    @Field(type = FieldType.Integer)
    private Integer edit; //编辑次数

    @Field(type = FieldType.String, indexAnalyzer="ik", searchAnalyzer="ik", store = true)
    private String content;
    @Field(type = FieldType.String)
    private String gsDate;  //公式时间
    @Field(type = FieldType.String, indexAnalyzer="ik", searchAnalyzer="ik", store = true)
    private String projXs;
//    @Field(type = FieldType.String, indexAnalyzer="ik", searchAnalyzer="ik", store = true)
    @Field(type = FieldType.String, index = FieldIndex.not_analyzed, store = true)
    private String oneName; //中标单位
    @Field(type = FieldType.String)
    private String province;    //省
    @Field(type = FieldType.String)
    private String city;    //市
    @Field(type = FieldType.String)
    private String county;  //地区

    @Field(type = FieldType.Integer)
    private Integer type;   //类型:0招标信息，招标变更1，中标结果2
    @Field(type = FieldType.String)
    private String tableName;   //表名


    public String getSnatchId() {
        return snatchId;
    }
    public void setSnatchId(String snatchId) {
        this.snatchId = snatchId;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getProjDq() {
        return projDq;
    }
    public void setProjDq(String projDq) {
        this.projDq = projDq;
    }
    public String getProjType() {
        return projType;
    }
    public void setProjType(String projType) {
        this.projType = projType;
    }
    public String getPbMode() {
        return pbMode;
    }
    public void setPbMode(String pbMode) {
        this.pbMode = pbMode;
    }
    public Double getProjSum() {
        return projSum;
    }
    public void setProjSum(Double projSum) {
        this.projSum = projSum;
    }
    public Integer getEdit() {
        return edit;
    }
    public void setEdit(Integer edit) {
        this.edit = edit;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getGsDate() {
        return gsDate;
    }
    public void setGsDate(String gsDate) {
        this.gsDate = gsDate;
    }
    public String getProjXs() {
        return projXs;
    }
    public void setProjXs(String projXs) {
        this.projXs = projXs;
    }
    public String getOneName() {
        return oneName;
    }
    public void setOneName(String oneName) {
        this.oneName = oneName;
    }
    public String getProvince() {
        return province;
    }
    public void setProvince(String province) {
        this.province = province;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getCounty() {
        return county;
    }
    public void setCounty(String county) {
        this.county = county;
    }

    public Integer getType() {
        return type;
    }
    public void setType(Integer type) {
        this.type = type;
    }
    public String getTableName() {
        return tableName;
    }
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
