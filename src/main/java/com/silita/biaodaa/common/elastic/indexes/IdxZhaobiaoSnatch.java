package com.silita.biaodaa.common.elastic.indexes;

import com.silita.biaodaa.common.elastic.model.ElasticEntity;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * Created by gmy on 2017/10/23.
 */
//@Document(indexName = "bdd_zhaobiao", type = "zhaobiao_snatch", shards = 4, replicas = 1, indexStoreType = "memory", refreshInterval = "1")
@Document(indexName = "bdd_zhaobiao", type = "zhaobiao_snatch", shards = 4, replicas = 1, refreshInterval = "1")
public class IdxZhaobiaoSnatch extends ElasticEntity {
    @Field(type = FieldType.String)
    private String snatchId;  //公告ID
    @Field(type = FieldType.String,  index = FieldIndex.not_analyzed)
    private String url;  //公告来源URL
    @Field(type = FieldType.String)
    private String gsDate;  //公告公布时间
//    @Field(type = FieldType.String, index = FieldIndex.analyzed, indexAnalyzer="ik", searchAnalyzer="ik", store = true)
    @Field(type = FieldType.String, index = FieldIndex.not_analyzed, store = true)
    private String title;  //公告标题
    @Field(type = FieldType.String, index = FieldIndex.analyzed, indexAnalyzer="ik", searchAnalyzer="ik", store = true)
    private String certificate; //资质要求
    @Field(type = FieldType.String, index = FieldIndex.analyzed, indexAnalyzer="ik", searchAnalyzer="ik", store = true)
    private String aptitudeName; //资质类型
    @Field(type = FieldType.String, index = FieldIndex.analyzed, indexAnalyzer="ik", searchAnalyzer="ik", store = true)
    private String pbMode;  //评标办法
    @Field(type = FieldType.String, indexAnalyzer="ik", searchAnalyzer="ik", store = true)
    private String projDq;  //项目地区
    @Field(type = FieldType.String, indexAnalyzer="ik", searchAnalyzer="ik", store = true)
    private String projType;    //项目类型
    @Field(type = FieldType.String )
    private String tbEndDate;   //投标截至时间
    @Field(type = FieldType.Double, store = true)
    private Double projSum;  //项目金额

    @Field(type = FieldType.String, indexAnalyzer="ik", searchAnalyzer="ik", store = true)
    private String content;   //公告文本
    @Field(type = FieldType.String)
    private String biddingType;  //公告区分
    @Field(type = FieldType.String)
    private String bmSite;  //报名地点
    @Field(type = FieldType.String)
    private String bmEndDate;  //报名截止时间
    @Field(type = FieldType.String)
    private String otherType;   //公告类型
    @Field(type = FieldType.Integer)
    private Integer edit; //编辑次数

    @Field(type = FieldType.String, index = FieldIndex.analyzed,indexAnalyzer="ik", searchAnalyzer="ik")
    private String province;    //省
    @Field(type = FieldType.String, index = FieldIndex.analyzed,indexAnalyzer="ik", searchAnalyzer="ik")
    private String city;    //市
    @Field(type = FieldType.String, index = FieldIndex.analyzed,indexAnalyzer="ik", searchAnalyzer="ik")
    private String county;  //地区
    @Field(type = FieldType.String, indexAnalyzer="ik", searchAnalyzer="ik", store = true)
    private String projXs;  //县市
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
    public String getGsDate() {
        return gsDate;
    }
    public void setGsDate(String gsDate) {
        this.gsDate = gsDate;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getCertificate() {
        return certificate;
    }
    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }
    public String getAptitudeName() {
        return aptitudeName;
    }
    public void setAptitudeName(String aptitudeName) {
        this.aptitudeName = aptitudeName;
    }
    public String getPbMode() {
        return pbMode;
    }
    public void setPbMode(String pbMode) {
        this.pbMode = pbMode;
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
    public String getTbEndDate() {
        return tbEndDate;
    }
    public void setTbEndDate(String tbEndDate) {
        this.tbEndDate = tbEndDate;
    }
    public Double getProjSum() {
        return projSum;
    }
    public void setProjSum(Double projSum) {
        this.projSum = projSum;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getBiddingType() {
        return biddingType;
    }
    public void setBiddingType(String biddingType) {
        this.biddingType = biddingType;
    }
    public String getBmSite() {
        return bmSite;
    }
    public void setBmSite(String bmSite) {
        this.bmSite = bmSite;
    }
    public String getBmEndDate() {
        return bmEndDate;
    }
    public void setBmEndDate(String bmEndDate) {
        this.bmEndDate = bmEndDate;
    }
    public String getOtherType() {
        return otherType;
    }
    public void setOtherType(String otherType) {
        this.otherType = otherType;
    }
    public Integer getEdit() {
        return edit;
    }
    public void setEdit(Integer edit) {
        this.edit = edit;
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
    public String getProjXs() {
        return projXs;
    }
    public void setProjXs(String projXs) {
        this.projXs = projXs;
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
